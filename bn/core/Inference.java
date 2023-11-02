package bn.core;

public interface Inference {
    
    public Distribution query (RandomVariable a, Assignment e, BayesianNetwork b);
}
