package IA.Desastres;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class GeneradorEstatsAnnealing implements SuccessorFunction {

	@Override
	public List getSuccessors(Object state) {
		// TODO Auto-generated method stub
		List<Successor> l = new ArrayList<Successor>();
		//Calculem el random ponderat
		int[] a = new int[3];
		//a[0] = 2*v;
		//a[1] = n*v;
		//a[2] = n*n;
		boolean b = false;
		while(!b)
		{
			/*Ponderaciones (n -> numero de grupos; v -> numero de viajes; H -> numero de helicopteros | valores asumidos de los experimentos)
			 * Por media un grupo tiene 7 personas, asi que por media un viaje tiene 2 grupos, i de media para helicoptero tiene nH/2n
			 * if() then "Rotar" -> 2v
			 * if(mou grups) -> n*v
			 * if(intercanvia grups) -> n*n
			 * Asumim n > v, per optamilitat, 
			 */
		}
		return l;
	}

}
