import requests
import threading
import time
import json
import random
import concurrent.futures

# Configuration
GATEWAY_URL = "http://localhost:8080/api/orders"
ACTIVE_ORDERS_URL = "http://localhost:8080/api/orders/active"
TRADE_HISTORY_URL = "http://localhost:8080/api/trades/history"

SYMBOL = "AAPL"
QUANTITY = 10
TOTAL_BUYERS = 20   # Number of concurrent buyers
TOTAL_SELLERS = 20  # Number of concurrent sellers

def place_order(user_id, side, order_id):
    """
    Sends a place order request to the Gateway.
    """
    payload = {
        "userId": user_id,
        "symbol": SYMBOL,
        "quantity": QUANTITY,
        "side": side
    }
    
    headers = {'Content-Type': 'application/json'}
    
    try:
        # print(f"[{order_id}] {user_id} placing {side}...")
        response = requests.post(GATEWAY_URL, headers=headers, data=json.dumps(payload))
        
        if response.status_code == 202:
            pass # Success, keeping output clean
            # data = response.json()
            # print(f"✅ [{order_id}] {side} Accepted: {data.get('correlationId')}")
        else:
            print(f"❌ [{order_id}] Failed: {response.status_code} - {response.text}")
            
    except Exception as e:
        print(f"❌ [{order_id}] Error: {e}")

def get_active_orders(user_id):
    try:
        response = requests.get(f"{ACTIVE_ORDERS_URL}/{user_id}")
        if response.status_code == 200:
            return response.json().get('activeOrders', [])
        return []
    except Exception as e:
        print(f"Error getting active orders for {user_id}: {e}")
        return []

def get_trade_history(user_id):
    try:
        response = requests.get(f"{TRADE_HISTORY_URL}/{user_id}")
        if response.status_code == 200:
            return response.json().get('trades', [])
        return []
    except Exception as e:
        print(f"Error getting trade history for {user_id}: {e}")
        return []

def cancel_order(order_id, user_id):
    try:
        url = f"{GATEWAY_URL}/{order_id}?userId={user_id}"
        response = requests.delete(url)
        if response.status_code == 202:
            print(f"✅ Cancel request sent for Order {order_id} (User: {user_id})")
            return True
        else:
            print(f"❌ Failed to cancel Order {order_id}: {response.status_code} - {response.text}")
            return False
    except Exception as e:
        print(f"Error cancelling order {order_id}: {e}")
        return False

def verify_results(buyers, sellers):
    print("\n--- Verifying Results ---")
    time.sleep(2) # Give some time for async processing
    
    total_trades = 0
    active_orders_count = 0
    
    all_users = buyers + sellers
    
    for user in all_users:
        trades = get_trade_history(user)
        total_trades += len(trades)
        
        active = get_active_orders(user)
        active_orders_count += len(active)
        
    print(f"Total Trade Records Found: {total_trades} (Expected: {len(all_users)})")
    print(f"Total Active Orders Remaining: {active_orders_count}")
    
    if total_trades >= len(all_users) and active_orders_count == 0:
        print("✅ SYSTEM WORKING FINE: All orders matched and executed.")
    else:
        print("⚠️ SYSTEM STATUS CHECK REQUIRED.")
        print(f"   - Trades found: {total_trades}")
        print(f"   - Active orders: {active_orders_count}")

def test_cancellation():
    print("\n--- Testing Cancellation ---")
    user_id = "cancel_test_user@test.com"
    # Place a BUY order. Since we removed price, we rely on the backend to set it.
    # To ensure it doesn't match immediately, we might need a different strategy if the backend matches everything at market price.
    # However, for now, we will just place an order and try to cancel it quickly.
    # If the system is fast, it might match before we cancel.
    
    print(f"Placing BUY order for {user_id}...")
    place_order(user_id, "BUY", 9999)
    
    time.sleep(1)
    
    active_orders = get_active_orders(user_id)
    if not active_orders:
        print("❌ Order not found in active orders! It might have matched or failed.")
        return

    order_id = active_orders[0]['orderId']
    print(f"Found active order {order_id}. Cancelling...")
    
    cancel_order(order_id, user_id)
    
    time.sleep(1)
    
    active_orders_after = get_active_orders(user_id)
    if not active_orders_after:
        print("✅ Order successfully cancelled (not found in active orders).")
    else:
        # Check if the specific order is gone
        found = any(o['orderId'] == order_id for o in active_orders_after)
        if not found:
            print("✅ Order successfully cancelled.")
        else:
            print("❌ Order still active after cancellation!")

def run_load_test():
    print(f"--- Starting Load Test: {TOTAL_BUYERS} Buyers vs {TOTAL_SELLERS} Sellers ---")
    start_time = time.time()
    
    buyers = []
    sellers = []
    
    with concurrent.futures.ThreadPoolExecutor(max_workers=50) as executor:
        futures = []
        
        # Submit Buyer Orders
        for i in range(TOTAL_BUYERS):
            user_id = f"buyer_{i}@test.com"
            buyers.append(user_id)
            futures.append(executor.submit(place_order, user_id, "BUY", i))
            
        # Submit Seller Orders
        for i in range(TOTAL_SELLERS):
            user_id = f"seller_{i}@test.com"
            sellers.append(user_id)
            futures.append(executor.submit(place_order, user_id, "SELL", i + 1000))
            
        # Wait for all to complete
        concurrent.futures.wait(futures)
        
    duration = time.time() - start_time
    print(f"--- Load Test Completed in {duration:.2f} seconds ---")
    print(f"Total Orders Sent: {TOTAL_BUYERS + TOTAL_SELLERS}")
    
    verify_results(buyers, sellers)

if __name__ == "__main__":
    run_load_test()
    test_cancellation()
