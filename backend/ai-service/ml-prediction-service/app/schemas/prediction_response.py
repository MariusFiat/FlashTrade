from pydantic import BaseModel, Field
from typing import List

class PredictionResponse(BaseModel):
    """
    Response model for stock price prediction.
    
    Attributes:
        symbol: Stock ticker symbol
        prediction_dates: List of future dates for predictions
        predicted_prices: List of predicted closing prices
        lower_bounds: List of lower confidence bounds (95% interval)
        upper_bounds: List of upper confidence bounds (95% interval)
        confidence: Overall confidence score (0-100)
        model_used: Name of the ML model used
        training_data_points: Number of data points used for training
    """
    symbol: str
    prediction_dates: List[str] = Field(..., description="Future dates (YYYY-MM-DD)")
    predicted_prices: List[float] = Field(..., description="Predicted closing prices")
    lower_bounds: List[float] = Field(..., description="Lower confidence bounds")
    upper_bounds: List[float] = Field(..., description="Upper confidence bounds")
    confidence: float = Field(..., ge=0, le=100, description="Confidence score")
    model_used: str
    training_data_points: int
    
    class Config:
        json_schema_extra = {
            "example": {
                "symbol": "AAPL",
                "prediction_dates": ["2024-01-04", "2024-01-05"],
                "predicted_prices": [152.50, 153.20],
                "lower_bounds": [150.00, 150.50],
                "upper_bounds": [155.00, 156.00],
                "confidence": 85.5,
                "model_used": "prophet",
                "training_data_points": 90
            }
        }