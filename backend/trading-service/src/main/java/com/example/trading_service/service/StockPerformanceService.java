package com.example.trading_service.service;

import com.example.trading_service.dto.StockHistoryPointDTO;
import com.example.trading_service.dto.StockPerformanceDTO;
import com.example.trading_service.entities.market.Stock;
import com.example.trading_service.entities.market.StockHistory;
import com.example.trading_service.repository.StockHistoryRepository;
import com.example.trading_service.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockPerformanceService {

    @Autowired
    private StockHistoryRepository stockHistoryRepository;

    @Autowired
    private StockRepository stockRepository;

    public StockPerformanceDTO getStockPerformance(String symbol, String range) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = calculateStartTime(range, now);

        List<StockHistory> rawData = stockHistoryRepository
                .findByStockSymbolAndTimestampBetweenOrderByTimestampAsc(symbol, start, now);

        List<StockHistoryPointDTO> filteredPoints = filterStockPoints(rawData, range);

        Double currentPrice = stockRepository.findById(symbol)
                .map(Stock::getPrice)
                .orElse(0.0);

        return new StockPerformanceDTO(symbol, filteredPoints, currentPrice, range);
    }

    private LocalDateTime calculateStartTime(String range, LocalDateTime now) {
        return switch (range.toLowerCase()) {
            case "1d" -> now.minusDays(1);
            case "1w" -> now.minusWeeks(1);
            case "1m" -> now.minusMonths(1);
            case "3m" -> now.minusMonths(3);
            case "6m" -> now.minusMonths(6);
            case "1y" -> now.minusYears(1);
            default -> now.minusDays(1);
        };
    }

    private List<StockHistoryPointDTO> filterStockPoints(List<StockHistory> rawData, String range) {
        if (rawData.isEmpty()) return List.of();

        return switch (range.toLowerCase()) {
            case "1d" ->
                    rawData.stream()
                            .sorted(Comparator.comparing(StockHistory::getTimestamp).reversed())
                            .limit(12)
                            .sorted(Comparator.comparing(StockHistory::getTimestamp))
                            .map(h -> new StockHistoryPointDTO(h.getPrice(), h.getTimestamp()))
                            .toList();

            case "1w" ->
                    rawData.stream()
                            .filter(h -> h.getTimestamp().getDayOfWeek().getValue() <= 5)
                            .collect(Collectors.groupingBy(
                                    h -> h.getTimestamp().toLocalDate(),
                                    TreeMap::new,
                                    Collectors.maxBy(Comparator.comparing(StockHistory::getTimestamp))
                            ))
                            .values().stream()
                            .flatMap(Optional::stream)
                            .map(h -> new StockHistoryPointDTO(h.getPrice(), h.getTimestamp()))
                            .toList();

            case "1m" ->
                    rawData.stream()
                            .filter(h -> h.getTimestamp().getDayOfWeek().getValue() == 5)
                            .collect(Collectors.groupingBy(
                                    h -> h.getTimestamp().getYear() + "-W" + h.getTimestamp().get(ChronoField.ALIGNED_WEEK_OF_YEAR),
                                    TreeMap::new,
                                    Collectors.maxBy(Comparator.comparing(StockHistory::getTimestamp))
                            ))
                            .values().stream()
                            .flatMap(Optional::stream)
                            .map(h -> new StockHistoryPointDTO(h.getPrice(), h.getTimestamp()))
                            .toList();

            case "3m", "6m", "1y" ->
                    rawData.stream()
                            .collect(Collectors.groupingBy(
                                    h -> h.getTimestamp().getYear() + "-" + h.getTimestamp().getMonthValue(),
                                    TreeMap::new,
                                    Collectors.maxBy(Comparator.comparing(StockHistory::getTimestamp))
                            ))
                            .values().stream()
                            .flatMap(Optional::stream)
                            .map(h -> new StockHistoryPointDTO(h.getPrice(), h.getTimestamp()))
                            .toList();

            default -> rawData.stream()
                    .map(h -> new StockHistoryPointDTO(h.getPrice(), h.getTimestamp()))
                    .toList();
        };
    }
}