package com.naoki.investmentledger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AssetSummaryResponse {

    // 投資元本
    private BigDecimal totalInvestmentAmount;

    // 現在の評価額合計
    private BigDecimal totalCurrentValue;

    // トータル含み益
    private BigDecimal totalProfitLoss;
}
