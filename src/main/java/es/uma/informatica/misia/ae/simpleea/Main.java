package es.uma.informatica.misia.ae.simpleea;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

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

	private static Map<String, Double> readEAParameters(int seed, int pop_size, double bit_flip_prob) {
		Map<String, Double> parameters = new HashMap<>();
		parameters.put(EvolutionaryAlgorithm.POPULATION_SIZE_PARAM, (double)pop_size);
		parameters.put(EvolutionaryAlgorithm.MAX_FUNCTION_EVALUATIONS_PARAM, (double) 1250);
		parameters.put(BitFlipMutation.BIT_FLIP_PROBABILITY_PARAM, bit_flip_prob);
		

		parameters.put(EvolutionaryAlgorithm.RANDOM_SEED_PARAM, (double)seed);
		return parameters;
	}

	public static void main_bfp (String args []) throws IOException{ // No controlo la excepción porque para la experimentación que vamos a hacer no me es necesario
		int DEFAULT_PROYECT_POP_SIZE = 25;
		List<List<Double>> means = new ArrayList<>();
		List<List<Double>> stdDesvs = new ArrayList<>();

		FileWriter myWriter = new FileWriter("logs_bfp.txt");

		for(double bfp = 0; bfp<1; bfp+=0.05) {
			System.out.println("Actual BFP= "+bfp);
			myWriter.write("Actual BFP"+bfp+"\n");
		
			List<Double> bfpMeans = new ArrayList<>();
			List<Double> bfpDesv = new ArrayList<>();
			// Bucle para cada instancia del problema Qubo (15 instances)
			for (int i = 0; i < 15; i++){
				System.out.println("--------------- Instance "+i+" ---------------");
				myWriter.write("--------------- Instance "+i+" ---------------\n");
				Problem problem = new Qubo(generateNewProblemInstance(105, 50, 50, i));

				List <Double> res_instance = new ArrayList<>();
				double sum_fit_instance = 0;
				int best_execution = -1;
				
				Random rnd = new Random(i);

				// Por cada instance del problema, 30 ejecuciones con semillas distintas.
				for (int j = 0; j <30; j++){
					System.out.println("**************** Execution "+j+" ****************");
					myWriter.write("**************** Execution "+j+" ****************\n");
					Map<String, Double> parameters = readEAParameters(rnd.nextInt(10000000), DEFAULT_PROYECT_POP_SIZE, bfp);
					EvolutionaryAlgorithm evolutionaryAlgorithm = new EvolutionaryAlgorithm(parameters, problem);
					
					Individual execution = evolutionaryAlgorithm.run();
					System.out.println("Fitness: "+execution.fitness);
					myWriter.write("Fitness: "+execution.fitness+"\n");
					
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
				myWriter.write("Mean of the Instance: "+mean_fit_instance+" | Std Desv. of the Instance: "+std_dev+ " | Best Fitness Exec: "+ best_execution+ " | Best value: "+res_instance.get(best_execution)+"\n");
				
				bfpMeans.add(mean_fit_instance);
				bfpDesv.add(std_dev);
			}
			means.add(bfpMeans);
			stdDesvs.add(bfpDesv);
		}

		
		myWriter.close();

		
		FileWriter csvWriter = new FileWriter("bfp_data.csv");

		System.out.println("Means");
		csvWriter.write("Means\n");
		for(int i=0; i<means.size(); i++) {
		for(int j=0; j<means.get(i).size(); j++){
			System.out.print(means.get(i).get(j)+", ");
			csvWriter.write(means.get(i).get(j)+",");
		}
		System.out.println();
		csvWriter.write("\n");
		}
				
		System.out.println("stdDesv");
		csvWriter.write("stdDesv\n");
		for(int i=0; i<stdDesvs.size(); i++) {
			for(int j=0; j<stdDesvs.get(i).size(); j++){
				System.out.print(stdDesvs.get(i).get(j)+", ");
				csvWriter.write(stdDesvs.get(i).get(j)+",");
			}
			System.out.println();
			csvWriter.write("\n");
		}

		csvWriter.close();


	}

	public static void main (String args []) throws IOException{ // Asumimos que ha ganado 0.05
		double WINNER_BFP = 0.05;
		List<List<Double>> means = new ArrayList<>();
		List<List<Double>> stdDesvs = new ArrayList<>();

		FileWriter myWriter = new FileWriter("logs_popsize.txt");

		for(int popsize = 5; popsize<100; popsize+=5) {
			System.out.println("Actual Population Size= "+popsize);
			myWriter.write("Actual Population Size"+popsize+"\n");
		
			List<Double> popsizeMeans = new ArrayList<>();
			List<Double> popsizeDesv  = new ArrayList<>();
			// Bucle para cada instancia del problema Qubo (15 instances)
			for (int i = 0; i < 15; i++){
				System.out.println("--------------- Instance "+i+" ---------------");
				myWriter.write("--------------- Instance "+i+" ---------------\n");
				Problem problem = new Qubo(generateNewProblemInstance(105, 50, 50, i));

				List <Double> res_instance = new ArrayList<>();
				double sum_fit_instance = 0;
				int best_execution = -1;

				Random rnd = new Random(i);
								
				// Por cada instance del problema, 30 ejecuciones con semillas distintas.
				for (int j = 0; j <30; j++){
					System.out.println("**************** Execution "+j+" ****************");
					myWriter.write("**************** Execution "+j+" ****************\n");
					Map<String, Double> parameters = readEAParameters(rnd.nextInt(10000000), popsize, WINNER_BFP);
					EvolutionaryAlgorithm evolutionaryAlgorithm = new EvolutionaryAlgorithm(parameters, problem);
					
					Individual execution = evolutionaryAlgorithm.run();
					System.out.println("Fitness: "+execution.fitness);
					myWriter.write("Fitness: "+execution.fitness+"\n");
					
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
				myWriter.write("Mean of the Instance: "+mean_fit_instance+" | Std Desv. of the Instance: "+std_dev+ " | Best Fitness Exec: "+ best_execution+ " | Best value: "+res_instance.get(best_execution)+"\n");
				
				popsizeMeans.add(mean_fit_instance);
				popsizeDesv.add(std_dev);
			}
			means.add(popsizeMeans);
			stdDesvs.add(popsizeDesv);
		}

		
		myWriter.close();

		
		FileWriter csvWriter = new FileWriter("popsize_data.csv");

		System.out.println("Means");
		csvWriter.write("Means\n");
		for(int i=0; i<means.size(); i++) {
		for(int j=0; j<means.get(i).size(); j++){
			System.out.print(means.get(i).get(j)+", ");
			csvWriter.write(means.get(i).get(j)+",");
		}
		System.out.println();
		csvWriter.write("\n");
		}
				
		System.out.println("stdDesv");
		csvWriter.write("stdDesv\n");
		for(int i=0; i<stdDesvs.size(); i++) {
			for(int j=0; j<stdDesvs.get(i).size(); j++){
				System.out.print(stdDesvs.get(i).get(j)+", ");
				csvWriter.write(stdDesvs.get(i).get(j)+",");
			}
			System.out.println();
			csvWriter.write("\n");
		}

		csvWriter.close();


	}

}
