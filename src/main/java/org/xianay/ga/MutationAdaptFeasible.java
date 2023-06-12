package org.xianay.ga;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MutationAdaptFeasible implements MutationFcn{

    private double mutationRate = 0.01;

    public MutationAdaptFeasible() {
    }
    /**
     * init MutationAdaptFeasible function with mutationRate
     * @param mutationRate
     */
    public MutationAdaptFeasible(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    @Override
    public List<String[]> mutate(List<String[]> population) {
        int individualLength = population.get(0).length;
        Random random = new Random();
        population.parallelStream().forEach(individual -> {
            for(int i = 0;i < individualLength; i++){
                if(random.nextDouble() < mutationRate){// if mutationRate is 0.01, then 1% of gene will be mutated
                    invertGene(individual, i);
                }
            }
        });
        return population;
    }

    /**
     * invert gene
     * @param lb
     * @param ub
     * @param individual
     * @param i
     */
    private void invertGene(String[] individual, int i) {
        String gene = individual[i];
        String invertGene = gene.chars().mapToObj(c -> {
            switch (c) {
                case '0':
                    return '1';
                case '1':
                    return '0';
                default:
                    return (char) c;
            }
        }).map(Object::toString).collect(Collectors.joining());
        individual[i] = invertGene;
    }

    @Override
    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }
    
}
