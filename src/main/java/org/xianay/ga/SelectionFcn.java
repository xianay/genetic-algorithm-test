package org.xianay.ga;

import java.util.List;

public interface SelectionFcn {
    /**
     * select individuals from population according to evalProbs
     * @param population
     * @param evalProbs
     */
    List<String[]> select(List<String[]> population, List<Double> evals);

}
