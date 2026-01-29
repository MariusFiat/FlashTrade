import pandas as pd
import numpy as np
from prophet import Prophet
from typing import Dict, List
import logging
from .model_interface import ModelInterface

logger = logging.getLogger(__name__)

class ProphetModel(ModelInterface):
    """
    Facebook Prophet implementation for stock price prediction.
    
    Prophet is a time series forecasting model that handles:
    - Trend changes
    - Seasonal patterns (weekly, yearly)
    - Holiday effects
    - Missing data
    
    It's particularly well-suited for financial data with strong seasonal patterns.
    """
    
    def __init__(self):
        """Initialize Prophet with optimized parameters for stock data."""
        self.model = None
        
    def train_and_predict(self, df: pd.DataFrame, prediction_days: int) -> Dict[str, List]:
        """
        Train Prophet model and generate predictions.
        
        Args:
            df: DataFrame with 'ds' (datetime) and 'y' (price) columns
            prediction_days: Number of days to forecast
            
        Returns:
            Dictionary with prediction results
        """
        logger.info(f"Training Prophet model on {len(df)} data points")
        
        try:
            # Initialize Prophet with custom parameters
            self.model = Prophet(
                changepoint_prior_scale=0.05,  # Flexibility of trend changes
                seasonality_prior_scale=10.0,   # Strength of seasonality
                seasonality_mode='multiplicative',  # Better for stock prices
                daily_seasonality=False,        # Not useful for daily data
                weekly_seasonality=True,        # Capture weekly patterns
                yearly_seasonality=True,        # Capture yearly patterns
                interval_width=0.95             # 95% confidence intervals
            )
            
            # Fit the model
            self.model.fit(df)
            
            # Create future dataframe
            future = self.model.make_future_dataframe(periods=prediction_days, freq='D')
            
            # Generate forecast
            forecast = self.model.predict(future)
            
            # Extract predictions for future dates only
            predictions = forecast.tail(prediction_days)
            
            # Calculate confidence score based on prediction interval width
            confidence = self._calculate_confidence(predictions)
            
            # Format results
            result = {
                'dates': predictions['ds'].dt.strftime('%Y-%m-%d').tolist(),
                'predictions': predictions['yhat'].tolist(),
                'lower_bounds': predictions['yhat_lower'].tolist(),
                'upper_bounds': predictions['yhat_upper'].tolist(),
                'confidence': confidence
            }
            
            logger.info(f"Prophet prediction completed with confidence: {confidence:.2f}")
            return result
            
        except Exception as e:
            logger.error(f"Prophet prediction failed: {str(e)}")
            raise
    
    def _calculate_confidence(self, predictions: pd.DataFrame) -> float:
        """
        Calculate confidence score based on prediction interval width.
        
        Narrower intervals indicate higher confidence.
        
        Args:
            predictions: DataFrame with yhat, yhat_lower, yhat_upper columns
            
        Returns:
            Confidence score (0-100)
        """
        # Calculate average interval width as percentage of predicted value
        interval_widths = (predictions['yhat_upper'] - predictions['yhat_lower']) / predictions['yhat']
        avg_interval_width = interval_widths.mean()
        
        # Convert to confidence score (narrower interval = higher confidence)
        # Typical interval width for stocks: 5-20%
        # Map 5% -> 95 confidence, 20% -> 70 confidence
        confidence = 100 - (avg_interval_width * 100 * 1.5)
        
        # Clamp between 60 and 95
        confidence = max(60.0, min(95.0, confidence))
        
        return round(confidence, 2)
    
    def get_model_name(self) -> str:
        """Return model name."""
        return "prophet"