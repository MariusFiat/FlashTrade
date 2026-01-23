package com.example.user_service.messaging;

import com.example.user_service.entities.Portfolio;
import com.example.user_service.entities.User;
import com.example.user_service.entities.UserDetails;
import com.example.user_service.entities.Wallet;
import com.example.user_service.messaging.dto.*;
import com.example.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderRequestListener {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitStockConfig.BUY_ORDER_QUEUE)
    @Transactional
    public void handleBuyOrder(BuyOrderRequest request) {
        System.out.println("Request for order: " + request.getOrderId());

        User user = userRepository.findByEmail(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = user.getUserDetails().getWallet();

        double totalCost = request.getQuantity() * request.getPriceAtOrder();
        BuyOrderResponse response = new BuyOrderResponse();
        response.setOrderId(request.getOrderId());

        if (wallet.getBalance() >= totalCost) {
            wallet.setBalance(wallet.getBalance() - totalCost);
            wallet.setPandingBalance(wallet.getPandingBalance() + totalCost);

            response.setApproved(true);
            response.setReason("Funds reserved successfully.");
        } else {
            response.setApproved(false);
            response.setReason("Insufficient funds. Needed: " + totalCost);
        }

        rabbitTemplate.convertAndSend(
                RabbitStockConfig.ORDER_RESPONSE_EXCHANGE,
                RabbitStockConfig.ORDER_RESPONSE_ROUTING_KEY,
                response
        );

        System.out.println("Response send: " + response.isApproved());
    }

    @RabbitListener(queues = RabbitStockConfig.BUY_ORDER_CLOSE_QUEUE)
    @Transactional
    public void handleOrderClose(BuyOrderCloseRequest message) {
        System.out.println("Processing close for order: " + message.getOrderId() + " with status: " + message.getStatus());

        User user = userRepository.findByEmail(message.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Wallet wallet = user.getUserDetails().getWallet();

        if ("CLOSED".equalsIgnoreCase(message.getStatus())) {
            double reservedAmount = message.getAmountSpent();
            wallet.setPandingBalance(wallet.getPandingBalance() - reservedAmount);
            wallet.setBalance(wallet.getBalance() + reservedAmount);

            System.out.println("Order canceled. Funds returned to balance.");

        } else if ("MATCHED".equalsIgnoreCase(message.getStatus())) {
            double spentAmount = message.getAmountSpent();
            wallet.setPandingBalance(wallet.getPandingBalance() - spentAmount);
            wallet.setTotalInvested(wallet.getTotalInvested() + spentAmount);

            updatePortfolio(user.getUserDetails(), message);

            System.out.println("Order matched. Portfolio updated and pending funds cleared.");
        }
    }

    private void updatePortfolio(UserDetails details, BuyOrderCloseRequest message) {
        Optional<Portfolio> existing = details.getPortfolioItems().stream()
                .filter(p -> p.getStock().equalsIgnoreCase(message.getSymbol()))
                .findFirst();

        if (existing.isPresent()) {
            Portfolio p = existing.get();
            p.setShares(p.getShares() + message.getQuantity());
        } else {
            Portfolio newItem = new Portfolio();
            newItem.setStock(message.getSymbol());
            newItem.setShares(message.getQuantity());
            newItem.setPortfolioValue(message.getAmountSpent() / message.getQuantity());
            newItem.setUserDetails(details);
            details.getPortfolioItems().add(newItem);
        }
    }

    @RabbitListener(queues = RabbitStockConfig.SELL_ORDER_QUEUE)
    @Transactional
    public void handleSellOrderRequest(SellOrderRequest request) {
        User user = userRepository.findByEmail(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Optional<Portfolio> stockInPortfolio = user.getUserDetails().getPortfolioItems().stream()
                .filter(p -> p.getStock().equalsIgnoreCase(request.getSymbol()))
                .findFirst();

        SellOrderResponse response = new SellOrderResponse();
        response.setOrderId(request.getOrderId());

        if (stockInPortfolio.isPresent() && stockInPortfolio.get().getShares() >= request.getQuantity()) {
            response.setApproved(true);
            response.setMessage("Stock available for sale.");
        } else {
            response.setApproved(false);
            response.setMessage("Insufficient shares in portfolio.");
        }

        rabbitTemplate.convertAndSend(
                RabbitStockConfig.SELL_RESPONSE_EXCHANGE,
                RabbitStockConfig.SELL_RESPONSE_ROUTING_KEY,
                response
        );
    }

    @RabbitListener(queues = RabbitStockConfig.SELL_ORDER_CLOSE_QUEUE)
    @Transactional
    public void handleSellOrderClose(SellOrderCloseRequest request) {
        System.out.println("Closing Sell Order: " + request.getOrderId() + " | Status: " + request.getStatus());

        User user = userRepository.findByEmail(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = user.getUserDetails().getWallet();

        if ("MATCHED".equalsIgnoreCase(request.getStatus())) {
            wallet.setBalance(wallet.getBalance() + request.getAmountReceived());

            Portfolio stock = user.getUserDetails().getPortfolioItems().stream()
                    .filter(p -> p.getStock().equalsIgnoreCase(request.getSymbol()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Stock not found in portfolio for selling"));

            double remainingShares = stock.getShares() - request.getQuantity();

            if (remainingShares <= 0) {
                user.getUserDetails().getPortfolioItems().remove(stock);
            } else {
                stock.setShares(remainingShares);
            }

            System.out.println("Sell Success: " + request.getAmountReceived() + " added to wallet.");
        } else {
            System.out.println("Sell Order Canceled/Closed. No wallet changes.");
        }
    }
}