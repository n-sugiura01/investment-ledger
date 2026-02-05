package com.naoki.investmentledger.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FundApiService {

    // あなたが見つけてくれた正しいURL
    private final String API_BASE_URL = "https://developer.am.mufg.jp/fund_information_latest/association_fund_cd/";

    public Integer fetchFundDetails(String code) {
        if (code == null || code.isEmpty()) return null;

        String url = API_BASE_URL + code;

        try {
            RestTemplate restTemplate = new RestTemplate();
            String jsonResponse = restTemplate.getForObject(url, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonResponse);

            // ★修正ポイント: いただいたJSON構造に合わせました
            // root -> datasets -> [0] -> nav

            JsonNode datasets = root.path("datasets");

            // データが空でないかチェック
            if (datasets.isMissingNode() || datasets.isEmpty()) {
                System.out.println("データが見つかりません: " + code);
                return null;
            }

            // 配列の0番目（最初のデータ）を取り出す
            JsonNode firstData = datasets.get(0);

            // "nav" (基準価額) を取得
            int currentPrice = firstData.path("nav").asInt();

            System.out.println("MUFG API取得成功: " + code + " = " + currentPrice + "円");
            return currentPrice;

        } catch (Exception e) {
            System.err.println("APIエラー (" + code + "): " + e.getMessage());
            return null;
        }
    }
}