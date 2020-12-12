package service;


import org.paukov.combinatorics3.Generator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AprioriAlgorithm {
    private final int minSupport;
    private final int finalCombinationSize;
    private final List<List<Integer>> unsupportedItemSets;
    private final List<List<Integer>> dataTable;

    public AprioriAlgorithm(int minSupport, int finalCombinationSize, List<List<Integer>> dataTable) {
        this.minSupport = minSupport;
        this.finalCombinationSize = finalCombinationSize;
        this.dataTable = dataTable;
        unsupportedItemSets = new ArrayList<>();
    }

    public List<List<Integer>> calculate() {
        List<List<Integer>> itemSets = dataTable.stream()
                .flatMap(Collection::stream)
                .distinct()
                .map(Collections::singletonList)
                .collect(Collectors.toList());
        itemSets = filterItemSetsBySupport(itemSets);

        for (int i = 1; i < finalCombinationSize; i++) {
            itemSets = recalculateItemSets(itemSets);
            itemSets = filterItemSetsBySupport(itemSets);
        }
        return itemSets;
    }

    private List<List<Integer>> filterItemSetsBySupport(List<List<Integer>> itemsList) {
        List<List<Integer>> supportSet = new ArrayList<>();
        for (List<Integer> itemList : itemsList) {
            int support = 0;
            for (List<Integer> row : dataTable) {
                if (row.containsAll(itemList)) {
                    support++;
                }
            }
            if (support >= minSupport) {
                supportSet.add(itemList);
            } else {
                unsupportedItemSets.add(itemList);
            }
        }
        return supportSet;
    }

    private List<List<Integer>> recalculateItemSets(List<List<Integer>> supportSet) {
        Set<Integer> availableItems = supportSet.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        if (availableItems.size() == 0) {
            return Collections.emptyList();
        }

        int countOfItemsInSet = supportSet.iterator().next().size() + 1;
        return Generator.combination(availableItems)
                .simple(countOfItemsInSet)
                .stream()
                .filter(this::isSupportedItemList)
                .collect(Collectors.toList());
    }

    private boolean isSupportedItemList(List<Integer> itemList) {
        for (List<Integer> unsupportedItem : unsupportedItemSets) {
            if (itemList.containsAll(unsupportedItem)) {
                return false;
            }
        }
        return true;
    }
}