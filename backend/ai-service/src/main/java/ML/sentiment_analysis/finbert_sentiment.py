import sys
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import torch

# Load pre-trained FinBERT model (specialized for financial text)
tokenizer = AutoTokenizer.from_pretrained("ProsusAI/finbert")
model = AutoModelForSequenceClassification.from_pretrained("ProsusAI/finbert")

def analyze_sentiment(text):
    """
    Analyzes sentiment of financial text

    Returns:
        Score between -1 (negative) and 1 (positive)
    """
    # Tokenize text
    inputs = tokenizer(text, return_tensors="pt", truncation=True, max_length=512)

    # Get model prediction
    outputs = model(**inputs)
    predictions = torch.nn.functional.softmax(outputs.logits, dim=-1)

    # FinBERT outputs: [positive, negative, neutral]
    positive = predictions[0][0].item()
    negative = predictions[0][1].item()
    neutral = predictions[0][2].item()

    # Calculate score: positive - negative
    score = positive - negative

    return score

if __name__ == "__main__":
    # Called from Java
    text = sys.argv[1]

    sentiment_score = analyze_sentiment(text)
    print(sentiment_score)  # Java reads this