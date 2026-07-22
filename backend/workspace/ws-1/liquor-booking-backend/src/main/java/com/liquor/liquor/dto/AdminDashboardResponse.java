package com.liquor.liquor.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminDashboardResponse {

    private BigDecimal totalRevenue;

    private Long totalOrders;

    private Long paidPayments;

    private Long activeUsers;

    private Integer stockUnits;

    private List<RevenuePointResponse> monthlyRevenue;

    private List<RevenuePointResponse> yearlyRevenue;
}
