package org.xianay.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class SelectionRoulette implements SelectionFcn {

    /** selection roulette */
    @Override
    public List<String[]> select(List<String[]> population, List<Double> evals) {
        // calculate fitness evaluation sum
		double evalSum = evals.parallelStream().reduce(0.0, (current, val) -> current + val);
		// calculate fitness evaluation probability
		List<Double> evalProbs = evals.parallelStream().map((val) -> val / evalSum).collect(Collectors.toList());
        int evalProbsLength = evalProbs.size();
        double[] cumulativeProbabilities = new double[evalProbsLength];
        double cumulativeProbability = 0.0;
        for (int i = 0; i < evalProbsLength; i++) {
            cumulativeProbability += evalProbs.get(i); // calculate cumulative probability
            cumulativeProbabilities[i] = cumulativeProbability;
        }
        Random random = new Random();
        List<String[]> selectedPopulation = new CopyOnWriteArrayList<>();

        Arrays.stream(cumulativeProbabilities).parallel().forEach((e) -> {
            double randomValue = random.nextDouble(); // generate random value
            for (int j = 0; j < cumulativeProbabilities.length; j++) {
                if (randomValue <= cumulativeProbabilities[j]) {
                    int selectedIndex = j;
                    selectedPopulation.add(population.get(selectedIndex));
                    break;
                }
            }
        });

        // for (int i = 0; i < cumulativeProbabilities.length; i++) {
        //     double randomValue = random.nextDouble(); // generate random value
        //     for (int j = 0; j < cumulativeProbabilities.length; j++) {
        //         if (randomValue <= cumulativeProbabilities[j]) {
        //             int selectedIndex = j;
        //             selectedPopulation.add(population.get(selectedIndex));
        //             break;
        //         }
        //     }
        // }
        
        return selectedPopulation;
    }

    
}
