package com.naoki.investmentledger.repository;

import com.naoki.investmentledger.entity.FundMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FundMasterRepository extends JpaRepository<FundMaster, String> {
    // 名前の一部が含まれているものを検索し、最大20件だけ返す
    List<FundMaster> findTop20ByFundNameLikeIgnoreCase(String keyword);
}
