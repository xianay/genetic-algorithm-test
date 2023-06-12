package org.xianay.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class GaApplication {

	static class Visualization {
		XYSeries series1 = new XYSeries("bast eval");
		XYSeries series2 = new XYSeries("mean eval");
        XYSeriesCollection dataset = new XYSeriesCollection();
		{
			dataset.addSeries(series1);
			dataset.addSeries(series2);
		}
		JFreeChart chart = ChartFactory.createScatterPlot(
				"genetic algorithm visualization",
				"iteration times",          // x axis label
				"fitness evaluation",       // y axis label
				dataset,
				PlotOrientation.VERTICAL,
				true,
				true,
				false);
		ChartPanel chartPanel = new ChartPanel(chart);
		JFrame frame = new JFrame("genetic algorithm visualization");

		public Visualization() {
			//chartPanel.setPreferredSize(new Dimension(500, 300));
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(chartPanel);
			frame.pack();
		}

		public void setVisible(boolean b) {
			frame.setVisible(true);
		}

		public void update(double bestEval, double meanEval, int iteration) {
			series1.add(iteration, bestEval);
			series2.add(iteration, meanEval);
			chartPanel.repaint();
		}

	}

	static interface FitnessFcn {
		double evaluate(double[] individual);
	}

	public static void main(String[] args) {

		// objective function question 4 solution min f(x) = 0.0 , n = 10, vactor x(n) = [0,0,0,0,0,0,0,0,0,0]
		FitnessFcn objectiveFcn = (x) -> {
			/** -20*exp(-0.2*sqrt(sum(x.^2)/n)) - exp(sum(cos(2*pi*x))/n) + 20 + e; */
			double sum = Arrays.stream(x).reduce(0, (current, val) -> current + Math.pow(val, 2));
			double sum2 = Arrays.stream(x).reduce(0, (current, val) -> current + Math.cos(2 * Math.PI * val));
			double n = x.length;
			return -20 * Math.exp(-0.2 * Math.sqrt(sum / n)) - Math.exp(sum2 / n) + 20 + Math.E;
		};
		// objective function question 3 solution max f(x) , n = 2, vactor x(n) = [0,0]
		FitnessFcn objFitnessFcn2 = (x) -> {
			//** 21.5 + x[1]*sin(4*pi*x[1]) + x[2]*sin(20*pi*x[2]); */
			return 21.5 + x[0] * Math.sin(4 * Math.PI * x[0]) + x[1] * Math.sin(20 * Math.PI * x[1]);
		};

		
		question3(objFitnessFcn2);
		question4(objectiveFcn);
	}


	private static void question3(FitnessFcn objFitnessFcn) {
		FitnessFcn fitnessFcn = (x) -> {
			return objFitnessFcn.evaluate(x);
		};
		int accuracy = 8;
		int dimension = 2;
		int weight = dimension * 99;
		int populationSize = (int)(weight * 0.4) * dimension;
		int maxGenerations = (int)(weight * 0.6) * dimension;
		double[] lb = new double[] { -2.9, 4.2 };
		double[] ub = new double[] { 12, 5.7 };
		double crossRate = 0.8;
		double mutationRate = 0.01;
		long starttime = System.currentTimeMillis();
		Map<?, ?> result = ga(accuracy, fitnessFcn, dimension, lb, ub, populationSize, maxGenerations, new SelectionRoulette(), new CrossoverSinglePoint(crossRate), new MutationAdaptFeasible(mutationRate), new Visualization());
		long endtime = System.currentTimeMillis();
		System.out.println("result = " + result);
		System.out.println("time = " + (endtime - starttime) + "ms");
	}


	private static void question4(FitnessFcn objectiveFcn) {
		// fitness function more fit individual has higher fitness value
		FitnessFcn fitnessFcn = (x) -> {
			return 1 / objectiveFcn.evaluate(x);
		};
		//test objective function
		//Gene individual = {1,2,3};
		//System.out.println("test = " + objectiveFcn.evaluate(individual)); //correct answer is 7.016454
		int accuracy = 4;
		int dimension = 10;
		int weight = dimension * 9;
		int populationSize = (int)(weight * 0.4) * dimension;
		int maxGenerations = (int)(weight * 0.6) * dimension;
		double[] lb = Arrays.stream(new double[dimension]).map((x) -> -30).toArray();
		double[] ub = Arrays.stream(new double[dimension]).map((x) -> 30).toArray();
		double crossRate = 0.8;
		double mutationRate = 0.01;
		long starttime = System.currentTimeMillis();
		Map<?, ?> result = ga(accuracy, fitnessFcn, dimension, lb, ub, populationSize, maxGenerations, new SelectionRoulette(), new CrossoverSinglePoint(crossRate), new MutationAdaptFeasible(mutationRate), new Visualization());
		long endtime = System.currentTimeMillis();
		System.out.println("result = " + result);
		System.out.println("time = " + (endtime - starttime) + "ms");
	}


	/**
	 * genetic algorithm
	 * @param accuracy         n of decimal places , max is 8
	 * @param fitnessFcn
	 * @param dimension        dimension of the problem
	 * @param lb               lower bound
	 * @param ub               upper bound
	 * @param populationSize
	 * @param maxGenerations
	 * @param selectionFcn
	 * @param crossoverFcn
	 * @param mutationFcn
	 * @param visualization    jfreechart
	 * @return
	 */
	public static Map<String, Object> ga(int accuracy, FitnessFcn fitnessFcn, int dimension, double[] lb, double[] ub, int populationSize, int maxGenerations, SelectionFcn selectionFcn, CrossoverFcn crossoverFcn, MutationFcn mutationFcn, Visualization visualization) {
		Map<String, Object> result = new LinkedHashMap<>();
		// Initial population
		List<double[]> population = initPopulation(dimension, lb, ub, populationSize);
		int[] binaryCodeLength = IntStream.range(0, dimension)
		.map((i) -> (int)Math.round(log2((ub[i] - lb[i]) / (1.0 / Math.pow(10, accuracy))))).toArray(); 
		double[] increment = IntStream.range(0, dimension)
		.mapToDouble(i -> (ub[i] - lb[i]) / (Math.pow(2, binaryCodeLength[i]) - 1)).toArray();
		double[] best = null;
		double bestEval = Double.MIN_VALUE;
		double meanEval = 0;
		visualization.setVisible(true);
		// start iteration
		for (int i = 0; i < maxGenerations; i++) {
			// evaluate fitness and set best individual
			List<Double> evals = population.parallelStream().map((individual) -> fitnessFcn.evaluate(individual)).collect(Collectors.toList());
			// encode population by binary code
			List<String[]> binaryPopulation = population.parallelStream().map((individual) -> encode(individual, lb, ub, binaryCodeLength, increment)).collect(Collectors.toList());
			// select function
			List<String[]> newBinaryPopulation = selectionFcn.select(binaryPopulation, evals);
			// crossover function
			newBinaryPopulation = crossoverFcn.crossover(newBinaryPopulation);
			// mutation function
			newBinaryPopulation = mutationFcn.mutate(newBinaryPopulation);
			// decode population
			List<double[]> newPopulation = binaryPopulation.parallelStream().map((code) -> decode(code, lb, ub, increment)).collect(Collectors.toList());
			// combine population
			population.addAll(newPopulation);
			// evaluate fitness
			evals = population.parallelStream().map((individual) -> fitnessFcn.evaluate(individual)).collect(Collectors.toList());
			// accounding to newEvals to order population
			population = eliminate(populationSize, population, evals);
			//update best and bestEval
			best = population.get(0);
			bestEval = fitnessFcn.evaluate(best);
			meanEval = evals.parallelStream().reduce(0.0, (current, val) -> current + val) / evals.size();
			System.out.println("it["+(i+1)+"]" + " bestEval: " + bestEval + " meanEval: " + meanEval + " best: " + Arrays.toString(best));
			visualization.update(bestEval, meanEval, i+1);
			if(bestEval > 300) break; // if bestEval > 300 then break
		}
		result.put("bestEval", bestEval);
		result.put("meanEval", meanEval);
		result.put("best", Arrays.toString(best));
		return result;
	}


	private static List<double[]> eliminate(int populationSize, List<double[]> population, List<Double> evals) {
		List<double[]> orderedPopulation = new ArrayList<>();
		final List<Double> originalEvals = new ArrayList<>(evals);
		Collections.sort(evals);
		evals = evals.subList(populationSize, evals.size());
		Collections.reverse(evals);
		// reserve original populationSize individuals
		for(int i = 0; i < populationSize; i++) {
			int index = originalEvals.indexOf(evals.get(i));
			orderedPopulation.add(population.get(index));
		}
		return orderedPopulation;
	}

	/**
	 * init population
	 * @param accuracy n of decimal places
	 * @param dimension
	 * @param lb
	 * @param ub
	 * @param populationSize
	 * @return
	 */
	private static List<double[]> initPopulation(int dimension, double[] lb, double[] ub, int populationSize) {
		List<double[]> population = new CopyOnWriteArrayList<>();
		Random random = new Random();
		IntStream.range(0, populationSize)
		.parallel().forEach((i) -> {
			double[] individual = new double[dimension];
			IntStream.range(0, dimension)
			.parallel().forEach((j) -> {
				individual[j] = lb[j] + random.nextDouble() * (ub[j] - lb[j]);
			});
			population.add(individual);
		});
		return population;
	}

	private static String[] encode(double[] individual, double[] lb, double[] ub, int[] binaryCodeLength, double[] increment) {
		String[] code = new String[individual.length];
		for (int i = 0; i < individual.length; i++) {
				int intValue = (int) Math.round((individual[i] - lb[i]) / increment[i]);
				String bincode = Integer.toBinaryString(intValue);
				code[i] = String.format("%" + binaryCodeLength[i] + "s", bincode).replace(' ', '0');
			}
		return code;
	}

	private static double[] decode(String[] code, double[] lb, double[] ub, double[] increment) {
		double[] individual = new double[code.length];
		for (int i = 0; i < code.length; i++) {
			individual[i] = lb[i] + Integer.parseInt(code[i], 2) * increment[i];
		}
		return individual;
	}

	private static double log2(double N) {
		return Math.log(N)/Math.log(2);
	}


}
