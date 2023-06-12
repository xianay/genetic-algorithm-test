package org.xianay.ga;

import java.util.List;

public interface CrossoverFcn {

    /**
     * crossover individuals from newPopulation according to crossRate
     * @param population
     * @return
     */
    List<String[]> crossover(List<String[]> opulation);
    
    void setCrossRate(double crossRate);
}
