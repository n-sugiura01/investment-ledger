package com.naoki.investmentledger.repository;

import com.naoki.investmentledger.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    // ここに何も書かなくても、以下のメソッドが自動で使えるようになります！
    // .save()   -> 保存
    // .findAll() -> 全件取得
    // .findById() -> IDで検索
    // .delete() -> 削除
}
