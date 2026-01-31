package com.naoki.investmentledger.service;

import com.naoki.investmentledger.dto.AssetSummaryResponse;
import com.naoki.investmentledger.entity.Asset;
import com.naoki.investmentledger.repository.AssetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor// finalがついているフィールドのコンストラクタを自動生成(Lombok)
public class AssetService {

    // Repository（データベース係）を呼び出せるようにする
    private final AssetRepository assetRepository;


    // 全ての資産データを取得する
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    // 新しい資産データを保存する
    public Asset saveAsset(Asset asset) {
        // ロジック：口数が未入力で、金額と基準価格がある場合、口数を自動計算する
        if (asset.getHoldingUnits() == null
                && asset.getInvestmentAmount() != null
                && asset.getAcquisitionPrice() != null) {

            // 計算式：投資額 ÷ 基準価格 × 10,000
            // 例: 10,000円 ÷ 20,000円 × 10,000 = 5,000口
            BigDecimal units = asset.getInvestmentAmount()
                    .divide(asset.getAcquisitionPrice(), 4, RoundingMode.HALF_UP)// 割り算（小数第4位まで、四捨五入）
                    .multiply(new BigDecimal("10000"));// 1万口単位なので掛ける

            asset.setHoldingUnits(units);
        }

        // ここに将来、「入力された金額から口数を自動計算する」などのロジックを追加できます
        return assetRepository.save(asset);
    }

    /*
     * 資産データを更新する
     * @param id 更新したいデータのID
     * @param assetDetails 更新する内容（送られてきたデータ）
     * @return 更新後のデータ
     */
    public Asset updateAsset(Long id, Asset assetDetails) {
        // IDでデータベースを検索。なければエラーにする。
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found for this id :: " + id));

        asset.setFundName(assetDetails.getFundName());
        asset.setInvestmentAmount(assetDetails.getInvestmentAmount());
        asset.setAcquisitionPrice(assetDetails.getAcquisitionPrice());
        asset.setCurrentPrice(assetDetails.getCurrentPrice());
        asset.setInvestmentDate(assetDetails.getInvestmentDate());

        // 計算ロジックの再適用（もし金額などが修正された場合、口数も再計算が必要）
        if (asset.getInvestmentAmount() != null && asset.getAcquisitionPrice() != null) {
            BigDecimal units = asset.getInvestmentAmount()
                    .divide(asset.getAcquisitionPrice(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("10000"));
            asset.setHoldingUnits(units);
        }
        // 上書き保存
        return assetRepository.save(asset);
    }

    /*
     * 資産データを削除する
     */
    public void deleteAsset(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found for this id :: " + id));
        assetRepository.delete(asset);
    }

    // 資産状況のサマリー（元本、評価額、含み益）を計算する
    public AssetSummaryResponse getSummary() {
        List<Asset> allAssets = assetRepository.findAll();

        // DB側で処理させた方がパフォーマンスがいい　下は非推奨
        // 1. 投資元本の合計
        BigDecimal totalInvestment = allAssets.stream()
                .map(Asset::getInvestmentAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCurrentValue = allAssets.stream()
                .filter(asset -> asset.getHoldingUnits() != null && asset.getCurrentPrice() != null)
                .map(asset -> asset.getHoldingUnits()
                        .multiply(asset.getCurrentPrice())
                        .divide(new BigDecimal("10000"), 0, RoundingMode.HALF_UP))// 円未満四捨五入
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. 含み益 = 評価額 - 元本
        BigDecimal totalProfitLoss = totalCurrentValue.subtract(totalInvestment);

        // DTOにセット
        AssetSummaryResponse response = new AssetSummaryResponse();
        response.setTotalInvestmentAmount(totalInvestment);
        response.setTotalCurrentValue(totalCurrentValue);
        response.setTotalProfitLoss(totalProfitLoss);

        return response;
    }

}





























