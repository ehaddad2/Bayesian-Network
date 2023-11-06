package bn.inference;
import bn.core.RandomVariable;
import bn.base.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Range;
import bn.base.Distribution;
import bn.core.Value;
import java.util.List;
import java.util.Random;


public class LikelihoodWeighting 
{
    private int N;

    public LikelihoodWeighting(int N) {
        this.N = N;
    }

    public static class weight {
        Assignment e;
        double weight;
        weight(Assignment e, double weight) {
            this.e = e;
            this.weight = weight;
        }
    }

    // Sample an event from bn that's consistent with the evidence e;
    // return the event and its weight, the likelihood that the event
    // accords to the evidence.

    public static weight weightedSample(BayesianNetwork bn, Assignment e) {
        double w = 1.0;
        Assignment x = e.copy();

        //get all random variables in bn
        List<RandomVariable> rvs = bn.getVariablesSortedTopologically();

        for (RandomVariable rand : rvs) {

            if (e.containsKey(rand)) {
                x.put(rand, e.get(rand));
                w *= bn.getProbability(rand, x);
			} 

            else {
				// to get a random variable between 0 and 1
				Random random = new java.util.Random();
				double randnum = random.nextDouble();
							
				Range Y = rand.getRange();
				// for each value in RV's domain
				for (Value val : Y) {	
					x.put(rand, val);
					randnum -= bn.getProbability(rand, x);
					if (randnum <= 0) {
						break;
                    }
                }
            }  
        }
        return new weight(x, w);
    }

    public Distribution query(RandomVariable X, Assignment e, BayesianNetwork bn) {
        
        //store counts for each value
        Distribution dist = new Distribution(X);
        for (Value val : X.getRange()) {
            dist.set(val, 0.0);
        }
        
        
        for(int j = 1; j <= N; j++) {
            weight wa = weightedSample(bn, e);
            Assignment tempAssignment = wa.e;
            double w = wa.weight;

            dist.put(tempAssignment.get(X), dist.get(tempAssignment.get(X)) + w);
        }
        dist.normalize();
        return dist;
    }

}

