package com.naoki.investmentledger.controller;

import com.naoki.investmentledger.entity.FundMaster;
import com.naoki.investmentledger.repository.FundMasterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class FundMasterController {

    private final FundMasterRepository repository;

    @GetMapping("/search")
    public List<FundMaster> search(@RequestParam String keyword) {
        // キーワードが空なら何も返さない
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        // 検索キーワードを「半角・小文字・スペースなし」に正規化
        String normalizedKeyword = normalize(keyword);

        // 全件検索してフィルタリング（件数制限なし！）
        return repository.findAll().stream()
                .filter(fund -> {
                    // データのファンド名も正規化して比較
                    String normalizedFundName = normalize(fund.getFundName());
                    return normalizedFundName.contains(normalizedKeyword);
                })
                // .limit(20) ← ここを削除しました！これでもう隠れません。
                .collect(Collectors.toList());
    }

    // 文字列を正規化するメソッド（全角→半角、大文字→小文字、スペース削除）
    private String normalize(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (char c : s.toCharArray()) {
            // 全角英数字を半角に
            if (c >= 0xFF01 && c <= 0xFF5E) {
                sb.append((char) (c - 0xFEE0));
            }
            // 全角スペースを半角スペースに
            else if (c == 0x3000) {
                sb.append(' ');
            }
            else {
                sb.append(c);
            }
        }
        // 小文字変換 & スペース削除
        return sb.toString().toLowerCase().replace(" ", "");
    }
}