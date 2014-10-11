package IA.Desastres;

import aima.search.framework.HeuristicFunction;

public class FuncioHeuristica1 implements HeuristicFunction {

	@Override
	public double getHeuristicValue(Object state) {
		Estat e = (Estat) state;
		return e.getTempsViatges();
	}

}
