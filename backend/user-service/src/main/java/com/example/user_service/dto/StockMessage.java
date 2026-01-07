package com.example.user_service.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class StockMessage implements Serializable {
    private String symbol;
    private Double price;
    private String companyName;
}