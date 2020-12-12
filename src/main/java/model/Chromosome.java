package model;

import java.util.ArrayList;
import java.util.List;

public class Chromosome {
    private List<Integer> genes;
    private int fitnessFunction;

    public List<Integer> getGenes() {
        return genes;
    }

    public void setGenes(List<Integer> genes) {
        this.genes = genes;
    }

    public int getFitnessFunction() {
        return fitnessFunction;
    }

    public void setFitnessFunction(int fitnessFunction) {
        this.fitnessFunction = fitnessFunction;
    }

    public Chromosome deepCopy() {
        Chromosome chromosome = new Chromosome();
        chromosome.setGenes(new ArrayList<>(genes));
        chromosome.setFitnessFunction(fitnessFunction);
        return chromosome;
    }

    @Override
    public String toString() {
        return String.format("fitnes: %4d  products: %s", fitnessFunction, genes.toString());
    }
}
