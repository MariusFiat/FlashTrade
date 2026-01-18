package com.example.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PortfolioHistoryPointDTO {
    private Double value;
    private LocalDateTime date;
}
