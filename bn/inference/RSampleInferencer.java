package bn.inference;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.base.Distribution;
import bn.core.RandomVariable;
import bn.core.Inference;
import bn.core.Value;

public class RSampleInferencer implements Inference {
	
	private int N;
	
	public RSampleInferencer(int n) {
        this.N = n;
	}
	
	public Assignment priorSample(BayesianNetwork bn) {
		Assignment e = new bn.base.Assignment();
		
		// get all random variables in bn
		List<RandomVariable> rvs = bn.getVariablesSortedTopologically();
		
		for (RandomVariable rv : rvs) {
			// generate a random variable between 0 and 1
			Random random = new java.util.Random();
			double randnum = random.nextDouble();
			
			// for each value in RV's domain
			for (Value val : rv.getRange()) {	
				e.put(rv, val);
				randnum -= bn.getProbability(rv, e);
				if (randnum <= 0) {
					break;
				}
			}
		}
		
		return e;
	}
	
    
	public bn.core.Distribution query(RandomVariable X, Assignment e, BayesianNetwork bn) {

		// store counts for each value
		bn.core.Distribution valueCnt = new Distribution(X);
		for (Value val : X.getRange()) {
			valueCnt.put(val, 0.0);
		}
		
		for(int j = 1; j <= N; j++) {
			Assignment eps = priorSample(bn);

			if (eps.isConsistent(e)) {
				valueCnt.put(eps.get(X), valueCnt.get(eps.get(X)) + 1);
			}
		}
		
		valueCnt.normalize();
		return valueCnt;
	}
	
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

	}
}