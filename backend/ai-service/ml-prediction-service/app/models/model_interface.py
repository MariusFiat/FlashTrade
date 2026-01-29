from abc import ABC, abstractmethod
from typing import Dict, List
import pandas as pd

class ModelInterface(ABC):
    """
    Abstract base class for all ML prediction models.
    
    This interface ensures consistent behavior across different model implementations.
    """
    
    @abstractmethod
    def train_and_predict(self, df: pd.DataFrame, prediction_days: int) -> Dict[str, List]:
        """
        Train the model on historical data and generate predictions.
        
        Args:
            df: DataFrame with 'ds' (date) and 'y' (price) columns
            prediction_days: Number of days to predict into the future
            
        Returns:
            Dictionary with keys:
                - 'dates': List of prediction dates (str)
                - 'predictions': List of predicted prices (float)
                - 'lower_bounds': List of lower confidence bounds (float)
                - 'upper_bounds': List of upper confidence bounds (float)
                - 'confidence': Overall confidence score (float)
        """
        pass
    
    @abstractmethod
    def get_model_name(self) -> str:
        """Return the name of the model."""
        pass