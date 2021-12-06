package es.uma.informatica.misia.ae.simpleea;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Main {

	public static List<List <Double>> generateNewProblemInstance(int length, int lower_bound, int max_bound, int seed){
		List<List <Double>> res = new ArrayList<>();
		Random rnd = new Random(seed);
		for (int i = 0; i < length; i++){
			List <Double> row = new ArrayList<>();
			for (int j = 0; j < length; j++){
				double aux = rnd.nextDouble()*(max_bound+lower_bound) - lower_bound;
				row.add(aux);
			}
			res.add(row);
		}		
		return res;
	}

	private static Map<String, Double> readEAParameters(int seed) {
		Map<String, Double> parameters = new HashMap<>();
		parameters.put(EvolutionaryAlgorithm.POPULATION_SIZE_PARAM, (double)15);
		parameters.put(EvolutionaryAlgorithm.MAX_FUNCTION_EVALUATIONS_PARAM, (double) 1200);
		parameters.put(BitFlipMutation.BIT_FLIP_PROBABILITY_PARAM, 0.1);
		

		parameters.put(EvolutionaryAlgorithm.RANDOM_SEED_PARAM, (double)seed);
		return parameters;
	}

	public static void main (String args []) {

		// Bucle para cada instancia del problema Qubo (10 instances)
		for (int i = 0; i < 10; i++){
			System.out.println("--------------- Instance "+i+" ---------------");
			Problem problem = new Qubo(generateNewProblemInstance(105, 50, 50, i));

			List <Double> res_instance = new ArrayList<>();
			double sum_fit_instance = 0;
			int best_execution = -1;
			
			// Por cada instance del problema, 30 ejecuciones con semillas distintas.
			for (int j = 0; j <30; j++){
				System.out.println("**************** Execution "+j+" ****************");
				Map<String, Double> parameters = readEAParameters((31+i)*(37+j));
				EvolutionaryAlgorithm evolutionaryAlgorithm = new EvolutionaryAlgorithm(parameters, problem);
				
				Individual execution = evolutionaryAlgorithm.run();
				System.out.println("Fitness: "+execution.fitness);
				
				res_instance.add(execution.fitness);
				sum_fit_instance += execution.fitness;

				if (best_execution == -1){
					best_execution = j;
				}else{
					if(res_instance.get(best_execution) < execution.fitness){
						best_execution = j;
					}
				}			

			}

			double mean_fit_instance = sum_fit_instance / 30;

			double aux_sum = 0;
			for (int j = 0; j < res_instance.size(); j++){
				aux_sum += (res_instance.get(j) - mean_fit_instance)*(res_instance.get(j) - mean_fit_instance);
			}
			aux_sum /= 30;
			double std_dev = Math.sqrt(aux_sum);

			System.out.println("Mean of the Instance: "+mean_fit_instance+" | Std Desv. of the Instance: "+std_dev+ " | Best Fitness Exec: "+ best_execution+ " | Best value: "+res_instance.get(best_execution));

		}

	}

}
