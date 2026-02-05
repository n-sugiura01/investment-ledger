package com.naoki.investmentledger.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "fund_master")
public class FundMaster {

    @Id
    private String code; // 投信協会コード

    private String fundName; // ファンド名称

}
