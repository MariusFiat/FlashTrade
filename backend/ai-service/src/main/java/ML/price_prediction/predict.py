import sys
import json
import numpy as np
from tensorflow import keras
# keras is not detectable for IntelliJ static analysis because TensorFlow loads it dynamically at runtime.

def predict_next_price(symbol, recent_prices):
    """
    Predicts tomorrow's price

    Args:
        symbol: Stock symbol (e.g., "AAPL")
        recent_prices: List of last 60 days' prices

    Returns:
        Predicted price for tomorrow
    """
    # Load trained model
    model = keras.models.load_model(f'models/lstm_{symbol}.h5')

    # Normalize input
    from sklearn.preprocessing import MinMaxScaler
    scaler = MinMaxScaler()
    scaled_prices = scaler.fit_transform(np.array(recent_prices).reshape(-1, 1))

    # Reshape for LSTM (samples, timestamps, features)
    x = scaled_prices[-60:].reshape(1, 60, 1)

    # Predict
    prediction = model.predict(x, verbose=0)

    # Denormalize (convert back to actual price)
    predicted_price = scaler.inverse_transform(prediction)[0][0]

    return predicted_price

if __name__ == "__main__":
    try:
        symbol = sys.argv[1]
        prices_json = sys.argv[2]
        prices = json.loads(prices_json)
        
        prediction = predict_next_price(symbol, prices)
        print(prediction)
    except Exception as e:
        print(f"Error: {e}", file=sys.stderr)
        print(100.0)  # Default fallback