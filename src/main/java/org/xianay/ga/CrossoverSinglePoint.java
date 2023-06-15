package org.xianay.ga;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

public class CrossoverSinglePoint implements CrossoverFcn {

    private double crossRate = 0.8;

    public CrossoverSinglePoint() {
    }

    /**
     * init CrossoverSinglePoint function with crossRate
     * 
     * @param crossRate default 0.8
     */
    public CrossoverSinglePoint(double crossRate) {
        this.crossRate = crossRate;
    }

    /**
     * crossover single point , if crossRate is 0.8, then 80% of newPopulation will be crossover
     */
    @Override
    public List<String[]> crossover(final List<String[]> population) {
        int[] chromosomeLength = Arrays.stream(population.get(0))
        .map(s -> s.length()).mapToInt(Integer::intValue).toArray();
        int populationSize = population.size();
        Random random = new Random();
        List<String[]> newPopulation = new CopyOnWriteArrayList<>();
        population.parallelStream().filter(individual -> random.nextDouble() < crossRate).forEach(individual -> {
            String[] parent1 = individual;
            String[] parent2 = population.get(random.nextInt(populationSize));
            executeSingleCrossover(chromosomeLength, random, parent1, parent2);
            newPopulation.add(parent1);
            newPopulation.add(parent2);
        });

        return newPopulation.subList(0, populationSize);
    }

    private void executeSingleCrossover(int[] chromosomeLength, Random random, String[] parent1, String[] parent2) {
        int[] crossoverPoint = IntStream.range(0, chromosomeLength.length)
        .map(i -> random.nextInt(chromosomeLength[i])).toArray(); // random crossover single point
        for(int i = 0;i < crossoverPoint.length;i++){
            String chromesome1 = parent1[i].substring(0, crossoverPoint[i]) + parent2[i].substring(crossoverPoint[i]);
            String chromesome2 = parent2[i].substring(0, crossoverPoint[i]) + parent1[i].substring(crossoverPoint[i]);
            parent1[i] = chromesome1;
            parent2[i] = chromesome2;
        }        
        
    }

    @Override
    public void setCrossRate(double crossRate) {
        this.crossRate = crossRate;
    }

}
