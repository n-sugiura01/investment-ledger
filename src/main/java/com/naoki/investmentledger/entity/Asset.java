package com.naoki.investmentledger.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
// import java.time.LocalDate; // ←削除

@Entity
@Data
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "銘柄名は必須です")
    private String fundName;

    // API連携用のコード (例: 03311187)
    private String code;

    @Positive(message = "取得単価はプラスの値を入力してください")
    private BigDecimal acquisitionPrice;

    @PositiveOrZero(message = "保有口数は0以上である必要があります")
    private BigDecimal holdingUnits;

    @Positive(message = "投資額はプラスの値を入力してください")
    private BigDecimal investmentAmount;

    // ★日付(investmentDate) は削除しました

    @Positive(message = "現在の基準価額はプラスの値を入力してください")
    private BigDecimal currentPrice;
}