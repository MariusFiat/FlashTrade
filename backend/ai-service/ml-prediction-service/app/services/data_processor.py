# app/utils/data_processor.py

import pandas as pd
import numpy as np
from datetime import datetime, timedelta
from typing import Dict, List, Optional, Tuple
import logging
from app.schemas.prediction import UserStockData

logger = logging.getLogger(__name__)


class DataProcessor:
    """
    Handles all data processing operations for the ML prediction service.
    Includes data validation, cleaning, merging, and preparation for Prophet.
    """
    
    def __init__(self):
        self.min_data_points = 30  # Minimum required data points for training
        self.max_missing_ratio = 0.3  # Maximum allowed ratio of missing data
        
    def validate_data(self, df: pd.DataFrame) -> Tuple[bool, Optional[str]]:
        """
        Validate the input dataframe for ML model training.
        
        Args:
            df: DataFrame with 'ds' (date) and 'y' (price) columns
            
        Returns:
            Tuple of (is_valid, error_message)
        """
        try:
            # Check if dataframe is empty
            if df is None or df.empty:
                return False, "DataFrame is empty"
            
            # Check required columns
            required_columns = ['ds', 'y']
            missing_columns = [col for col in required_columns if col not in df.columns]
            if missing_columns:
                return False, f"Missing required columns: {missing_columns}"
            
            # Check minimum data points
            if len(df) < self.min_data_points:
                return False, f"Insufficient data points. Required: {self.min_data_points}, Got: {len(df)}"
            
            # Check for null values in critical columns
            null_counts = df[required_columns].isnull().sum()
            if null_counts['y'] > 0:
                missing_ratio = null_counts['y'] / len(df)
                if missing_ratio > self.max_missing_ratio:
                    return False, f"Too many missing price values: {missing_ratio:.2%}"
            
            # Check data types
            if not pd.api.types.is_datetime64_any_dtype(df['ds']):
                return False, "Column 'ds' must be datetime type"
            
            if not pd.api.types.is_numeric_dtype(df['y']):
                return False, "Column 'y' must be numeric type"
            
            # Check for negative or zero prices
            if (df['y'] <= 0).any():
                return False, "Price values must be positive"
            
            # Check for duplicate dates
            if df['ds'].duplicated().any():
                return False, "Duplicate dates found in data"
            
            # Check date ordering
            if not df['ds'].is_monotonic_increasing:
                return False, "Dates must be in ascending order"
            
            return True, None
            
        except Exception as e:
            logger.error(f"Error validating data: {str(e)}")
            return False, f"Validation error: {str(e)}"
    
    def clean_data(self, df: pd.DataFrame) -> pd.DataFrame:
        """
        Clean and preprocess the data.
        
        Args:
            df: Raw dataframe
            
        Returns:
            Cleaned dataframe
        """
        try:
            df_clean = df.copy()
            
            # Remove duplicates (keep last)
            df_clean = df_clean.drop_duplicates(subset=['ds'], keep='last')
            
            # Sort by date
            df_clean = df_clean.sort_values('ds').reset_index(drop=True)
            
            # Handle missing values in price
            if df_clean['y'].isnull().any():
                # Forward fill first, then backward fill
                df_clean['y'] = df_clean['y'].fillna(method='ffill').fillna(method='bfill')
            
            # Remove outliers using IQR method
            df_clean = self._remove_outliers(df_clean)
            
            # Ensure positive values
            df_clean = df_clean[df_clean['y'] > 0]
            
            return df_clean
            
        except Exception as e:
            logger.error(f"Error cleaning data: {str(e)}")
            raise
    
    def _remove_outliers(self, df: pd.DataFrame, column: str = 'y') -> pd.DataFrame:
        """
        Remove outliers using the IQR (Interquartile Range) method.
        
        Args:
            df: Input dataframe
            column: Column to check for outliers
            
        Returns:
            DataFrame with outliers removed
        """
        Q1 = df[column].quantile(0.25)
        Q3 = df[column].quantile(0.75)
        IQR = Q3 - Q1
        
        # Define outlier bounds (using 1.5 * IQR is standard)
        lower_bound = Q1 - 1.5 * IQR
        upper_bound = Q3 + 1.5 * IQR
        
        # Filter outliers
        df_filtered = df[(df[column] >= lower_bound) & (df[column] <= upper_bound)]
        
        outliers_removed = len(df) - len(df_filtered)
        if outliers_removed > 0:
            logger.info(f"Removed {outliers_removed} outliers from {column}")
        
        return df_filtered
    
    def merge_data_sources(
        self,
        yahoo_data: pd.DataFrame,
        user_data: Optional[List[UserStockData]]
    ) -> pd.DataFrame:
        """
        Merge Yahoo Finance data with user-provided data.
        User data takes precedence over Yahoo data for overlapping dates.
        
        Args:
            yahoo_data: DataFrame from Yahoo Finance
            user_data: Optional list of user stock data
            
        Returns:
            Merged dataframe
        """
        try:
            # Start with Yahoo data
            merged_df = yahoo_data.copy()
            
            # If no user data, return Yahoo data
            if not user_data:
                return merged_df
            
            # Convert user data to DataFrame
            user_df = pd.DataFrame([
                {
                    'ds': pd.to_datetime(item.date),
                    'y': item.price,
                    'volume': item.volume if hasattr(item, 'volume') else None
                }
                for item in user_data
            ])
            
            # Remove any invalid entries
            user_df = user_df.dropna(subset=['ds', 'y'])
            user_df = user_df[user_df['y'] > 0]
            
            if user_df.empty:
                logger.warning("User data is empty after cleaning")
                return merged_df
            
            # Mark source of data
            merged_df['source'] = 'yahoo'
            user_df['source'] = 'user'
            
            # Combine dataframes
            combined_df = pd.concat([merged_df, user_df], ignore_index=True)
            
            # Sort by date and source (user data takes precedence)
            combined_df = combined_df.sort_values(['ds', 'source'], ascending=[True, False])
            
            # Remove duplicates, keeping first (which will be user data due to sorting)
            combined_df = combined_df.drop_duplicates(subset=['ds'], keep='first')
            
            # Sort by date again
            combined_df = combined_df.sort_values('ds').reset_index(drop=True)
            
            logger.info(f"Merged data: {len(yahoo_data)} Yahoo records + {len(user_df)} user records = {len(combined_df)} total")
            
            return combined_df
            
        except Exception as e:
            logger.error(f"Error merging data sources: {str(e)}")
            # Return Yahoo data as fallback
            return yahoo_data
    
    def prepare_for_prophet(self, df: pd.DataFrame) -> pd.DataFrame:
        """
        Prepare dataframe for Prophet model training.
        Prophet requires columns named 'ds' (date) and 'y' (value).
        
        Args:
            df: Input dataframe
            
        Returns:
            DataFrame formatted for Prophet
        """
        try:
            # Select only required columns
            prophet_df = df[['ds', 'y']].copy()
            
            # Ensure datetime format
            prophet_df['ds'] = pd.to_datetime(prophet_df['ds'])
            
            # Ensure numeric format
            prophet_df['y'] = pd.to_numeric(prophet_df['y'], errors='coerce')
            
            # Remove any NaN values
            prophet_df = prophet_df.dropna()
            
            # Sort by date
            prophet_df = prophet_df.sort_values('ds').reset_index(drop=True)
            
            return prophet_df
            
        except Exception as e:
            logger.error(f"Error preparing data for Prophet: {str(e)}")
            raise
    
    def add_regressors(self, df: pd.DataFrame) -> pd.DataFrame:
        """
        Add additional regressors (features) to the dataframe for Prophet.
        These can improve prediction accuracy.
        
        Args:
            df: Input dataframe with 'ds' and 'y' columns
            
        Returns:
            DataFrame with additional regressor columns
        """
        try:
            df_with_regressors = df.copy()
            
            # Add volume if available
            if 'volume' in df.columns:
                df_with_regressors['volume_regressor'] = df['volume'].fillna(0)
            
            # Add day of week (0 = Monday, 6 = Sunday)
            df_with_regressors['day_of_week'] = df_with_regressors['ds'].dt.dayofweek
            
            # Add month
            df_with_regressors['month'] = df_with_regressors['ds'].dt.month
            
            # Add quarter
            df_with_regressors['quarter'] = df_with_regressors['ds'].dt.quarter
            
            # Add year
            df_with_regressors['year'] = df_with_regressors['ds'].dt.year
            
            # Add moving averages as regressors
            if len(df_with_regressors) >= 7:
                df_with_regressors['ma_7'] = df_with_regressors['y'].rolling(window=7, min_periods=1).mean()
            
            if len(df_with_regressors) >= 30:
                df_with_regressors['ma_30'] = df_with_regressors['y'].rolling(window=30, min_periods=1).mean()
            
            # Add price momentum (rate of change)
            df_with_regressors['momentum'] = df_with_regressors['y'].pct_change().fillna(0)
            
            # Add volatility (rolling standard deviation)
            if len(df_with_regressors) >= 7:
                df_with_regressors['volatility'] = df_with_regressors['y'].rolling(window=7, min_periods=1).std().fillna(0)
            
            return df_with_regressors
            
        except Exception as e:
            logger.error(f"Error adding regressors: {str(e)}")
            # Return original dataframe if error occurs
            return df
    
    def calculate_confidence_intervals(
        self,
        predictions: pd.DataFrame,
        confidence_level: float = 0.95
    ) -> pd.DataFrame:
        """
        Calculate confidence intervals for predictions.
        
        Args:
            predictions: DataFrame with predictions from Prophet
            confidence_level: Confidence level (default 0.95 for 95%)
            
        Returns:
            DataFrame with confidence intervals
        """
        try:
            # Prophet already provides yhat_lower and yhat_upper
            # We can adjust these based on the confidence level if needed
            
            result_df = predictions.copy()
            
            # Ensure confidence intervals are positive
            result_df['yhat_lower'] = result_df['yhat_lower'].clip(lower=0)
            result_df['yhat_upper'] = result_df['yhat_upper'].clip(lower=0)
            
            # Calculate confidence interval width
            result_df['ci_width'] = result_df['yhat_upper'] - result_df['yhat_lower']
            
            # Calculate confidence score (inverse of CI width, normalized)
            max_ci_width = result_df['ci_width'].max()
            if max_ci_width > 0:
                result_df['confidence_score'] = 1 - (result_df['ci_width'] / max_ci_width)
            else:
                result_df['confidence_score'] = 1.0
            
            # Ensure confidence score is between 0 and 1
            result_df['confidence_score'] = result_df['confidence_score'].clip(0, 1)
            
            return result_df
            
        except Exception as e:
            logger.error(f"Error calculating confidence intervals: {str(e)}")
            raise
    
    def format_prediction_output(
        self,
        predictions: pd.DataFrame,
        symbol: str
    ) -> List[Dict]:
        """
        Format predictions into the output schema.
        
        Args:
            predictions: DataFrame with predictions
            symbol: Stock symbol
            
        Returns:
            List of prediction dictionaries
        """
        try:
            output = []
            
            for _, row in predictions.iterrows():
                prediction = {
                    'symbol': symbol,
                    'date': row['ds'].strftime('%Y-%m-%d'),
                    'predicted_price': float(row['yhat']),
                    'lower_bound': float(row['yhat_lower']),
                    'upper_bound': float(row['yhat_upper']),
                    'confidence_score': float(row.get('confidence_score', 0.8))
                }
                output.append(prediction)
            
            return output
            
        except Exception as e:
            logger.error(f"Error formatting prediction output: {str(e)}")
            raise
    
    def detect_anomalies(self, df: pd.DataFrame, threshold: float = 3.0) -> pd.DataFrame:
        """
        Detect anomalies in the data using z-score method.
        
        Args:
            df: Input dataframe
            threshold: Z-score threshold for anomaly detection
            
        Returns:
            DataFrame with anomaly flag
        """
        try:
            df_with_anomalies = df.copy()
            
            # Calculate z-scores
            mean = df_with_anomalies['y'].mean()
            std = df_with_anomalies['y'].std()
            
            if std > 0:
                df_with_anomalies['z_score'] = (df_with_anomalies['y'] - mean) / std
                df_with_anomalies['is_anomaly'] = df_with_anomalies['z_score'].abs() > threshold
            else:
                df_with_anomalies['z_score'] = 0
                df_with_anomalies['is_anomaly'] = False
            
            anomaly_count = df_with_anomalies['is_anomaly'].sum()
            if anomaly_count > 0:
                logger.info(f"Detected {anomaly_count} anomalies in data")
            
            return df_with_anomalies
            
        except Exception as e:
            logger.error(f"Error detecting anomalies: {str(e)}")
            return df
    
    def get_data_quality_metrics(self, df: pd.DataFrame) -> Dict:
        """
        Calculate data quality metrics.
        
        Args:
            df: Input dataframe
            
        Returns:
            Dictionary with quality metrics
        """
        try:
            metrics = {
                'total_records': len(df),
                'date_range': {
                    'start': df['ds'].min().strftime('%Y-%m-%d'),
                    'end': df['ds'].max().strftime('%Y-%m-%d')
                },
                'missing_values': int(df['y'].isnull().sum()),
                'missing_percentage': float(df['y'].isnull().sum() / len(df) * 100),
                'price_statistics': {
                    'mean': float(df['y'].mean()),
                    'median': float(df['y'].median()),
                    'std': float(df['y'].std()),
                    'min': float(df['y'].min()),
                    'max': float(df['y'].max())
                }
            }
            
            return metrics
            
        except Exception as e:
            logger.error(f"Error calculating data quality metrics: {str(e)}")
            return {}