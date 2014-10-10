package IA.Desastres;

import aima.search.framework.GoalTest;

public class EstatFinal implements GoalTest {

	@Override
	public boolean isGoalState(Object state) {
		//Tots els estats són finals
		return true;
	}

}
