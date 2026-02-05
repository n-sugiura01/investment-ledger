package com.naoki.investmentledger.config;

import com.naoki.investmentledger.entity.FundMaster;
import com.naoki.investmentledger.repository.FundMasterRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final FundMasterRepository repository;

    public DataLoader(FundMasterRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (repository.count() > 0) {
            return;
        }

        System.out.println("マスタデータをCSVから読み込み開始...");
        long start = System.currentTimeMillis();

        ClassPathResource resource = new ClassPathResource("data/funds.csv");
        List<FundMaster> masterList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                // ヘッダー行をスキップ
                if (isFirstLine) { isFirstLine = false; continue; }

                // CSVの分割（カンマ区切り）
                // ※データの中にカンマが含まれているとずれますが、簡易実装としてsplitを使います
                String[] data = line.split(",");

                // 「対象商品一覧.csv」の列に合わせてインデックスを指定
                // [2]:投信協会コード, [3]:ファンド名称
                if (data.length > 3) {
                    String code = data[2].trim();
                    String name = data[3].trim();

                    if (!code.isEmpty() && !name.isEmpty()) {
                        FundMaster master = new FundMaster();
                        master.setCode(code);
                        master.setFundName(name);
                        masterList.add(master); // ここではまだ保存しない！リストに入れるだけ
                    }
                }
            }

            // ★ここが高速化のポイント！
            // 全データをまとめて一回で保存する
            if (!masterList.isEmpty()) {
                repository.saveAll(masterList);
            }

            long end = System.currentTimeMillis();
            System.out.println("読み込み完了！ 件数: " + masterList.size() + " (" + (end - start) + "ms)");

        } catch (Exception e) {
            System.out.println("CSV読み込みエラー (ファイルがない場合はスキップします): " + e.getMessage());
        }
    }
}
