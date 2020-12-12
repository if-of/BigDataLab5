package service;

import model.Chromosome;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class GeneticAlgorithm {
    private final int chromosomeSize;
    private final int populationSize;

    private final int iterationCount;
    private final int tournamentsCount;

    private final double probabilityOfMutation;


    private final List<Integer> availableGenes;
    private final List<List<Integer>> dataTable;


    private final Random random = new Random();


    public GeneticAlgorithm(
            int chromosomeSize,
            int populationSize,
            int iterationCount,
            int tournamentsCount,
            double probabilityOfMutation,
            List<List<Integer>> dataTable
    ) {
        this.chromosomeSize = chromosomeSize;
        this.populationSize = populationSize;
        this.iterationCount = iterationCount;
        this.tournamentsCount = tournamentsCount;
        this.probabilityOfMutation = probabilityOfMutation;
        this.dataTable = dataTable;
        this.availableGenes = calculateAvailableGenes(dataTable);
    }

    public List<Chromosome> calculate() {
        List<Chromosome> population = createPopulation();

        for (int i = 0; i < iterationCount; i++) {
            for (int j = 0; j < tournamentsCount; j++) {
                tournamentSelection(population);
            }
        }
        mutation(population);

        return population;
    }

    private static List<Integer> calculateAvailableGenes(List<List<Integer>> dataTable) {
        return dataTable.stream()
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Chromosome> createPopulation() {
        Set<Integer> usedRows = new HashSet<>();
        while (usedRows.size() < populationSize) {
            int rowNumber = random.nextInt(dataTable.size());
            if (usedRows.contains(rowNumber)) {
                continue;
            }

            List<Integer> row = dataTable.get(rowNumber);
            if (row.size() >= chromosomeSize) {
                usedRows.add(rowNumber);
            }
        }

        return usedRows.stream()
                .map(dataTable::get)
                .map(ArrayList::new)
                .map(this::shuffleList)
                .map(list -> list.subList(0, chromosomeSize))
                .map(this::createChromosome)
                .collect(Collectors.toList());
    }

    private void tournamentSelection(List<Chromosome> population) {
        Set<Integer> firstChromosomesNumbers = new HashSet<>();
        while (firstChromosomesNumbers.size() < 2) {
            firstChromosomesNumbers.add(random.nextInt(populationSize));
        }
        Set<Integer> secondChromosomesNumbers = new HashSet<>();
        while (secondChromosomesNumbers.size() < 2) {
            secondChromosomesNumbers.add(random.nextInt(populationSize));
        }

        Chromosome firstOldChromosome = firstChromosomesNumbers.stream()
                .map(population::get)
                .max(Comparator.comparing(Chromosome::getFitnessFunction))
                .get();
        Chromosome secondOldChromosome = secondChromosomesNumbers.stream()
                .map(population::get)
                .max(Comparator.comparing(Chromosome::getFitnessFunction))
                .get();


        Chromosome firstChromosome = firstOldChromosome.deepCopy();
        Chromosome secondChromosome = secondOldChromosome.deepCopy();
        crossover(firstChromosome, secondChromosome);
        calculateFitnessFunction(firstChromosome);
        calculateFitnessFunction(secondChromosome);

        if (firstChromosome.getFitnessFunction() >= firstOldChromosome.getFitnessFunction()) {
            firstOldChromosome.setGenes(firstChromosome.getGenes());
            firstOldChromosome.setFitnessFunction(firstChromosome.getFitnessFunction());
        }
        if (secondChromosome.getFitnessFunction() >= secondOldChromosome.getFitnessFunction()) {
            secondOldChromosome.setGenes(secondChromosome.getGenes());
            secondOldChromosome.setFitnessFunction(secondChromosome.getFitnessFunction());
        }
    }

    private void crossover(Chromosome firstChromosome, Chromosome secondChromosome) {
        List<Integer> firstUniqueGenes = new ArrayList<>(firstChromosome.getGenes());
        firstUniqueGenes.removeAll(secondChromosome.getGenes());
        List<Integer> secondUniqueGenes = new ArrayList<>(secondChromosome.getGenes());
        secondUniqueGenes.removeAll(firstChromosome.getGenes());

        int countOfSwaps = random.nextInt(firstUniqueGenes.size());
        for (int i = 0; i < countOfSwaps; i++) {
            firstChromosome.getGenes().add(secondUniqueGenes.get(i));
            firstChromosome.getGenes().remove(firstUniqueGenes.get(i)); //remove by value
            secondChromosome.getGenes().add(firstUniqueGenes.get(i));
            secondChromosome.getGenes().remove(secondUniqueGenes.get(i)); //remove by value
        }
    }

    /**
     * Be aware total count of genes in population should be bigger than chromosome size
     */
    private void mutation(List<Chromosome> population) {
        for (Chromosome chromosome : population) {
            if (random.nextDouble() < probabilityOfMutation) {
                int randomGenNumber = random.nextInt(chromosome.getGenes().size());
                int newGene = availableGenes.get(random.nextInt(availableGenes.size()));
                while (!chromosome.getGenes().contains(newGene)) {
                    newGene = availableGenes.get(random.nextInt(availableGenes.size()));
                }
                chromosome.getGenes().set(randomGenNumber, newGene);
            }
        }
    }

    private <T> List<T> shuffleList(List<T> list) {
        Collections.shuffle(list, random);
        return list;
    }

    private Chromosome createChromosome(List<Integer> genes) {
        Chromosome chromosome = new Chromosome();
        chromosome.setGenes(genes);
        calculateFitnessFunction(chromosome);
        return chromosome;
    }

    private void calculateFitnessFunction(Chromosome chromosome) {
        int fitnessFunction = 0;
        List<Integer> genes = chromosome.getGenes();
        for (List<Integer> row : dataTable) {
            if (row.containsAll(genes)) {
                fitnessFunction++;
            }
        }
        chromosome.setFitnessFunction(fitnessFunction);
    }
}
