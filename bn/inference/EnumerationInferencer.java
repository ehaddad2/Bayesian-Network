package bn.inference;
import bn.core.RandomVariable;
import bn.core.Assignment;
import bn.core.BayesianNetwork;
import bn.core.Inference;
import java.util.List;
import bn.base.Distribution;
import bn.base.Value;

public class EnumerationInferencer implements Inference{
    
    public EnumerationInferencer() {

    }
    
    public bn.core.Distribution query (RandomVariable a, Assignment e, BayesianNetwork b) {

        Distribution dist = new Distribution(a);
        for (Object xi : a.getRange()) {
            Assignment exi = e.copy();
            exi.put(a, (Value)xi);
            dist.put((Value)xi, enumerateAll(b.getVariablesSortedTopologically(), exi, b));
        }
        dist.normalize();
        return dist;
    }


    public static double enumerateAll(List<RandomVariable> vars, Assignment e, BayesianNetwork bn) {
        if (vars.isEmpty()) {
            return 1.0;
        }
        RandomVariable Y = vars.get(0);
        List<RandomVariable> rest = vars.subList(1, vars.size());
        if (e.containsKey(Y)) {
            return bn.getProbability(Y, e) * enumerateAll(rest, e, bn);
        } else {
            double sum = 0.0;
            for (Object y : Y.getRange()) {
                Assignment ey = e.copy();
                ey.put(Y, (Value) y);
                sum += bn.getProbability(Y, ey) * enumerateAll(rest, ey, bn);
            }
            return sum;
        }
    }
}