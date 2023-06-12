package org.xianay.ga;

import java.util.List;

public interface MutationFcn {

    /**
     * mutate individuals from lb to ub according to mutationRate 
     * @param population
     * @param lb 
     * @param ub
     * @return
     */
    List<String[]> mutate(List<String[]> population);

    void setMutationRate(double mutationRate);
    
}
