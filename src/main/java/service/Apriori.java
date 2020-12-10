package service;


import org.paukov.combinatorics3.Generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Apriori {
    private final int minSupport;
    private final int finalSubsetSize;

    public Apriori(int minSupport, int finalSubsetSize) {
        this.minSupport = minSupport;
        this.finalSubsetSize = finalSubsetSize;
    }

    public List<List<Integer>> calculate(List<List<Integer>> dataTable) {
        List<List<Integer>> itemSets = dataTable.stream()
                .flatMap(Collection::stream)
                .distinct()
                .map(Collections::singletonList)
                .collect(Collectors.toList());
        itemSets = filterItemSetsBySupport(itemSets, dataTable);

        for (int i = 1; i < finalSubsetSize; i++) {
            itemSets = recalculateItemSets(itemSets);
            itemSets = filterItemSetsBySupport(itemSets, dataTable);
        }
        return itemSets;
    }

    private List<List<Integer>> filterItemSetsBySupport(List<List<Integer>> itemSets, List<List<Integer>> dataTable) {
        List<List<Integer>> supportSet = new ArrayList<>();
        for (List<Integer> itemSet : itemSets) {
            int support = 0;
            for (List<Integer> row : dataTable) {
                if (row.containsAll(itemSet)) {
                    support++;
                }
            }
            if (support >= minSupport) {
                supportSet.add(itemSet);
            }
        }
        return supportSet;
    }

    private List<List<Integer>> recalculateItemSets(List<List<Integer>> supportSet) {
        Set<Integer> availableItems = supportSet.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        int countOfItemsInSet = supportSet.iterator().next().size() + 1;

        return Generator.combination(availableItems)
                .simple(countOfItemsInSet)
                .stream()
                .collect(Collectors.toList());
    }
}