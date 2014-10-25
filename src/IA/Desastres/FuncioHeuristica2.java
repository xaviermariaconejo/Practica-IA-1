package IA.Desastres;

import aima.search.framework.HeuristicFunction;

public class FuncioHeuristica2 implements HeuristicFunction {

	@Override
	public double getHeuristicValue(Object state) {
		// TODO Auto-generated method stub
		Estat e = (Estat) state;
		return e.getTempsViatges()+e.tempsMaximFerits();
	}

}
