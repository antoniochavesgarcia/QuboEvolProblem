package es.uma.informatica.misia.ae.simpleea;

import java.util.List;
import java.util.Random;

public class Qubo implements Problem{
	private List<List<Double>> q;
		
	public Qubo(List<List <Double>> q) {
		this.q=q;
	}
	
	public double evaluate(Individual individual) {
		BinaryString binaryString = (BinaryString)individual;
		double result = 0.0;
		for (int i=0; i < binaryString.getChromosome().length; i++) {
			List<Double> curr_row = q.get(i); 
			for (int j=0; j < binaryString.getChromosome().length; j++) {
				result += curr_row.get(j)*binaryString.getChromosome()[i];
			}
		}
		return result;
	}
	
	public BinaryString generateRandomIndividual(Random rnd) {
		return new BinaryString(q.size(),rnd);
	}
	
}
