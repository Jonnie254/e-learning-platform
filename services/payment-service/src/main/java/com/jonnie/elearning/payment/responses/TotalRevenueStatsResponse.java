package com.jonnie.elearning.payment.responses;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TotalRevenueStatsResponse {
    BigDecimal totalEarning;
}
