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
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    // 新規保存（口数自動計算付き）
    public Asset saveAsset(Asset asset) {
        calculateUnits(asset); // ロジックをメソッドに切り出しました
        return assetRepository.save(asset);
    }

    // 更新（口数再計算付き）
    public Asset updateAsset(Long id, Asset assetDetails) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found for this id :: " + id));

        asset.setFundName(assetDetails.getFundName());
        asset.setInvestmentAmount(assetDetails.getInvestmentAmount());
        asset.setAcquisitionPrice(assetDetails.getAcquisitionPrice());
        asset.setCurrentPrice(assetDetails.getCurrentPrice());
        asset.setCode(assetDetails.getCode());

        // 金額や取得単価が変わっていたら口数を再計算
        calculateUnits(asset);

        return assetRepository.save(asset);
    }

    public void deleteAsset(Long id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset not found for this id :: " + id));
        assetRepository.delete(asset);
    }

    // ★修正: 正確な集計ロジック
    public AssetSummaryResponse getSummary() {
        List<Asset> allAssets = assetRepository.findAll();

        // 1. 投資元本の合計
        BigDecimal totalInvestment = allAssets.stream()
                .map(Asset::getInvestmentAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. 保有金額（時価総額）の合計
        BigDecimal totalCurrentValue = allAssets.stream()
                .filter(asset -> asset.getCurrentPrice() != null) // 現在値さえあれば計算する
                .map(asset -> {
                    // 口数を取得
                    BigDecimal units = asset.getHoldingUnits();

                    // ★ここが強化ポイント
                    // もし口数が保存されていなければ、投資額と取得単価からその場で計算する
                    if (units == null
                            && asset.getInvestmentAmount() != null
                            && asset.getAcquisitionPrice() != null
                            && asset.getAcquisitionPrice().compareTo(BigDecimal.ZERO) > 0) {

                        units = asset.getInvestmentAmount()
                                .divide(asset.getAcquisitionPrice(), 4, RoundingMode.HALF_UP)
                                .multiply(new BigDecimal("10000"));
                    }

                    // それでも計算できなければ 0
                    if (units == null) return BigDecimal.ZERO;

                    // 評価額 = 口数 × 現在値 ÷ 10000
                    return units.multiply(asset.getCurrentPrice())
                            .divide(new BigDecimal("10000"), 0, RoundingMode.HALF_UP);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. 含み益
        BigDecimal totalProfitLoss = totalCurrentValue.subtract(totalInvestment);

        return new AssetSummaryResponse(totalInvestment, totalCurrentValue, totalProfitLoss);
    }

    // 共通の計算ロジック（投資額と取得単価から口数を計算）
    private void calculateUnits(Asset asset) {
        if (asset.getInvestmentAmount() != null
                && asset.getAcquisitionPrice() != null
                && asset.getAcquisitionPrice().compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal units = asset.getInvestmentAmount()
                    .divide(asset.getAcquisitionPrice(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("10000"));

            // 小数点以下を切り捨てて整数にするかどうかはお好みで（通常は口数はデータ上は少数もあり得るが、一旦そのまま）
            asset.setHoldingUnits(units);
        }
    }
}