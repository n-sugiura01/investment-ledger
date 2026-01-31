package com.naoki.investmentledger.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //銘柄名(例: "eMAXIS Slim 全世界株式")
    @NotBlank(message = "銘柄名は必須です")
    private String fundName;

    //取得単価
    @Positive(message = "取得単価はプラスの値を入力してください")
    private BigDecimal acquisitionPrice;

    //保有口数
    @PositiveOrZero(message = "保有口数は0以上である必要があります")
    private BigDecimal holdingUnits;

    //投資額
    @Positive(message = "投資額はプラスの値を入力してください")
    private BigDecimal investmentAmount;

    //購入日・投資日
    @PastOrPresent(message = "投資日は過去か今日の日付にしてください")
    private LocalDate investmentDate;

    //現在の基準価額
    @Positive(message = "現在の基準価額はプラスの値を入力してください")
    private BigDecimal currentPrice;
}
