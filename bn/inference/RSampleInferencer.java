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
	
	private int numSamples;
	
	public RSampleInferencer(int numSamples) {
        this.numSamples = numSamples;
	}
	
	public Assignment priorSample(BayesianNetwork bn) {
		//get topological sorted list of bn nodes
		List<RandomVariable> bnNodes = bn.getVariablesSortedTopologically();
		Assignment e = new bn.base.Assignment();

		for (RandomVariable rVar : bnNodes) {
			Random random = new java.util.Random();
			double randnum = random.nextDouble();
            
			for (Value val : rVar.getRange()) {	
				e.put(rVar, val);
				randnum -= bn.getProbability(rVar, e);
				if (randnum <= 0) {//break if we exceeded bound (ie found right assignment)
					break;
				}
			}
		}
		
		return e;
	}
	
    
	public bn.core.Distribution query(RandomVariable X, Assignment e, BayesianNetwork bn) {

		bn.core.Distribution valueCnt = new Distribution(X);
		for (Value val : X.getRange()) {//init distribution
			valueCnt.put(val, 0.0);
		}
		
		for(int j = 1; j <= numSamples; j++) {
			Assignment psEvidence = priorSample(bn);

			if (matches(psEvidence,e)) {//ensure evidence matches
				valueCnt.put(psEvidence.get(X), valueCnt.get(psEvidence.get(X)) + 1);
			}
		}
		
		valueCnt.normalize();
		return valueCnt;
	}
	
    //method for ensuring query evidence matches each assignment the sample vars take on
    public static Boolean matches(Assignment a1, Assignment a2) {

        for (RandomVariable rv : a2.keySet()) {
    		if (a1.containsKey(rv)) {
    			if (!(a1.get(rv).equals(a2.get(rv)))) {
    				return false;
    			}
    		}
    	}

        return true;
    }
	public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {

	}
}