import model.Chromosome;
import model.Transaction;
import service.AprioriAlgorithm;
import service.GeneticAlgorithm;
import service.XLSXReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Application {

    public static void main(String[] args) throws IOException {
        XLSXReader xlsxReader = new XLSXReader();
        List<Transaction> transactions = xlsxReader.readCorrectTransaction("Online Retail.xlsx");
        List<List<Integer>> normalizedTransactions = normalizeTransaction(transactions);
        System.out.println("data was loaded");

        long startTime;
        long endTime;


        startTime = System.nanoTime();
        AprioriAlgorithm aprioriAlgorithm = new AprioriAlgorithm(
                200,
                3,
                normalizedTransactions
        );
        List<List<Integer>> aprioriResult = aprioriAlgorithm.calculate();
        endTime = System.nanoTime();
        System.out.println("================= Apriori ==================");
        aprioriResult.forEach(System.out::println);
        System.out.println("============================================");
        System.out.println("time: " + (endTime - startTime) / 1000_000_000 + " seconds");


        startTime = System.nanoTime();
        GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm(
                5,
                100,
                100,
                100,
                0.1,
                normalizedTransactions
        );
        List<Chromosome> geneticResult = geneticAlgorithm.calculate();
        endTime = System.nanoTime();
        System.out.println("================= Genetic ==================");
        geneticResult.forEach(System.out::println);
        System.out.println("============================================");
        System.out.println("time: " + (endTime - startTime) / 1000_000_000 + " seconds");
    }

    private static List<List<Integer>> normalizeTransaction(List<Transaction> transactions) {
        Map<Integer, Set<Integer>> normalizedMap = new HashMap<>();
        for (Transaction transaction : transactions) {
            normalizedMap
                    .computeIfAbsent(transaction.getCustomerId(), k -> new HashSet<>())
                    .add(transaction.getStockCode());
        }

        return normalizedMap.values().stream()
                .map(ArrayList::new)
                .collect(Collectors.toList());
    }
}
