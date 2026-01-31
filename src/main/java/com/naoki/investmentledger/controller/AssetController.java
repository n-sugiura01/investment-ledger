package com.naoki.investmentledger.controller;

import com.naoki.investmentledger.dto.AssetSummaryResponse;
import com.naoki.investmentledger.entity.Asset;
import com.naoki.investmentledger.service.AssetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {

    private final AssetService assetService;

    // GET /api/assets
    // 登録されているすべてのデータを返す
    @GetMapping
    public List<Asset> getAllAssets() {
        return assetService.getAllAssets();
    }

    // POST /api/assets
    // 新しいデータを登録する
    @PostMapping
    public Asset createAsset(@Valid @RequestBody Asset asset) {
        // @RequestBody は「送られてきたJSONデータをAssetクラスに返還してね」という指示
        return assetService.saveAsset(asset);
    }

    // Get /api/assets/summary
    // 合計情報を返す新しい窓口
    @GetMapping("/summary")
    public AssetSummaryResponse getSummary() {
        return assetService.getSummary();
    }

    // PUT /api/assets/{id}
    // 指定したIDのデータを更新する
    @PutMapping("/{id}")
    public Asset updateAsset(@PathVariable Long id, @Valid @RequestBody Asset assetDetails) {
        return assetService.updateAsset(id, assetDetails);
    }

    // DELETE /api/assets/{id}
    // 指定したIDのデータを削除する
    @DeleteMapping("/{id}")
    public void deleteAsset(@PathVariable Long id) {
        assetService.deleteAsset(id);
    }
}
