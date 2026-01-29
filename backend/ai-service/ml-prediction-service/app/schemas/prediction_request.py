from pydantic import BaseModel, Field, validator
from typing import List, Optional
from datetime import datetime

class PredictionRequest(BaseModel):
    """
    Request model for stock price prediction.
    
    Attributes:
        symbol: Stock ticker symbol (e.g., "AAPL")
        dates: List of historical dates in YYYY-MM-DD format
        prices: List of historical closing prices
        prediction_days: Number of days to predict into the future
        model_type: ML model to use ("prophet", "lstm", etc.)
    """
    symbol: str = Field(..., min_length=1, max_length=10, description="Stock ticker symbol")
    dates: List[str] = Field(..., min_items=30, description="Historical dates (YYYY-MM-DD)")
    prices: List[float] = Field(..., min_items=30, description="Historical closing prices")
    prediction_days: int = Field(default=7, ge=1, le=30, description="Days to predict")
    model_type: str = Field(default="prophet", description="Model type to use")
    
    @validator('dates')
    def validate_dates(cls, v):
        """Validate date format."""
        for date_str in v:
            try:
                datetime.strptime(date_str, '%Y-%m-%d')
            except ValueError:
                raise ValueError(f"Invalid date format: {date_str}. Expected YYYY-MM-DD")
        return v
    
    @validator('prices')
    def validate_prices(cls, v):
        """Validate prices are positive."""
        if any(p <= 0 for p in v):
            raise ValueError("All prices must be positive")
        return v
    
    @validator('prediction_days')
    def validate_prediction_days(cls, v):
        """Validate prediction days range."""
        if v < 1 or v > 30:
            raise ValueError("Prediction days must be between 1 and 30")
        return v
    
    class Config:
        json_schema_extra = {
            "example": {
                "symbol": "AAPL",
                "dates": ["2024-01-01", "2024-01-02", "2024-01-03"],
                "prices": [150.25, 151.30, 149.80],
                "prediction_days": 7,
                "model_type": "prophet"
            }
        }