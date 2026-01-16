import numpy as np
import tensorflow as tf
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense, Dropout

def create_model(input_shape):
    """
    Creates an LSTM model for stock price prediction

    LSTM = Long Short-Term Memory
    - A type of neural network good for time series data
    - "Remembers" patterns from past prices
    """
    model = Sequential([
        # First LSTM layer: 50 neurons, returns sequences
        LSTM(50, return_sequences=True, input_shape=input_shape),
        Dropout(0.2),  # Prevents overfitting

        # Second LSTM layer: 50 neurons
        LSTM(50, return_sequences=False),
        Dropout(0.2),

        # Dense layer: 25 neurons
        Dense(25),

        # Output layer: 1 neuron (predicted price)
        Dense(1)
    ])

    # Compile model
    model.compile(optimizer='adam', loss='mean_squared_error')

    return model

def prepare_data(prices, lookback=60):
    """
    Prepares data for LSTM

    lookback = how many days to look back
    Example: Use last 60 days to predict day 61
    """
    X, y = [], []

    for i in range(lookback, len(prices)):
        X.append(prices[i-lookback:i])  # Last 60 days
        y.append(prices[i])  # Next day

    return np.array(X), np.array(y)

def train_model(symbol, historical_prices):
    """
    Trains the model on historical data
    """
    # Normalize prices (scale to 0-1)
    from sklearn.preprocessing import MinMaxScaler
    scaler = MinMaxScaler()
    scaled_prices = scaler.fit_transform(historical_prices.reshape(-1, 1))

    # Prepare training data
    X_train, y_train = prepare_data(scaled_prices)
    X_train = X_train.reshape(X_train.shape[0], X_train.shape[1], 1)

    # Create and train model
    model = create_model((X_train.shape[1], 1))
    model.fit(X_train, y_train, epochs=50, batch_size=32, verbose=0)

    # Save model
    model.save(f'models/lstm_{symbol}.h5')

    return model, scaler