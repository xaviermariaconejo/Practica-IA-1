package IA.Desastres;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class GeneradorEstatsAnnealing implements SuccessorFunction {

	@Override
	public List getSuccessors(Object state) {
		// TODO Auto-generated method stub
		List<Successor> l = new ArrayList<Successor>();
		ArrayList<ArrayList<ArrayList<Grupo>>> helicopters = ((Estat)state).getHelicopters();
		//Calculem el random ponderat
		/*Ponderaciones (n -> numero de grupos; v -> numero de viajes; valores asumidos de los experimentos)
		 * Por media un grupo tiene 7 personas, asi que por media un viaje tiene 2 grupos
		 * if() then "Rotar" -> 2v
		 * if(mou grups) -> n*v
		 * if(intercanvia grups) -> n*n
		 * Asumim n > v, ya que lo mas optimo (y por media como hemos dicho) habrá el doble de grupos que de viajes
		 * ya que de media un viaje tiene dos grupos, por lo tanto podemos asumir (por ejemplo) n = 4 y v = 2
		 */
		int V = 2;
		int N = 4;
		int[] a = new int[3];
		a[0] = 2*V;
		a[1] = N*V + a[0];
		a[2] = N*N + a[0] + a[1];
		boolean b = false;
		Estat e = new Estat ((Estat)state);
		String s = "";
		Random rand = new Random();
		while(!b)
		{
			e = new Estat ((Estat)state);
			int r = rand.nextInt(a[2]);
			boolean aux = false; int i = 0;
			while (i < 3 && !aux)
			{
				aux = r < a[i];
				if (!aux) ++i;
			}
			switch (i)
			{
				case 0: int h = rand.nextInt(helicopters.size());
						if(helicopters.get(h).size() > 0) {
						int v = rand.nextInt(helicopters.get(h).size());
							if (helicopters.get(h).get(v).size() == 3)
							{
								double n = rand.nextInt();
								if (n < 0.5)
								{
									b = e.intercambiaGrups(h, v, 0, h, v, 1);
									s = "Rotacio Grups (Hi, "+h+", Vi: "+v+", Gi: 0, Hj: "+h+", Vj: "+v+", Gj: 1)";
								}
								else
								{
									b = e.intercambiaGrups(h, v, 1, h, v, 2);
									s = "Rotacio Grups (Hi: "+h+", Vi: "+v+", Gi: 1, Hj: "+h+", Vj: "+v+", Gj: 2)";
								}
							}	
						}
						break;
						
				case 1: int Hi = rand.nextInt(helicopters.size());
						int Hj = rand.nextInt(helicopters.size());
						if(helicopters.get(Hi).size() > 0 && helicopters.get(Hj).size() > 0) {
							int Vi = rand.nextInt(helicopters.get(Hi).size()); 
							int Vj = rand.nextInt(helicopters.get(Hj).size());
							if (Vi != Vj)
							{
								int G = (int) rand.nextInt(helicopters.get(Hi).get(Vi).size());
								b = e.mouGrups (G, Hi, Vi, Hj, Vj);
								s = "Mou Grup Normal (Hi: "+Hi+", Vi: "+Vi+", Gi: "+G+", Hj: "+Hj+", Vj: "+Vj+")";
							}
						}
						break;
						
				case 2: 
						int hi = rand.nextInt(helicopters.size());
						int hj = rand.nextInt(helicopters.size());
						if(helicopters.get(hi).size() > 0 && helicopters.get(hj).size() > 0) {
							int vi = rand.nextInt(helicopters.get(hi).size()); 
							int vj = rand.nextInt(helicopters.get(hj).size());
							if (vi != vj)
							{
								int gi = rand.nextInt(helicopters.get(hi).get(vi).size());
								int gj = rand.nextInt(helicopters.get(hj).get(vj).size());
								b = e.intercambiaGrups(hi, vi, gi, hj, vj, gj);
								s = "Intercambi Grups (Hi: "+hi+", Vi: "+vi+", Gi: "+gi+", Hj: "+hj+", Vj: "+vj+", Gj: "+gj+")";
							}
						}
						break;
			}
		}
		l.add(new Successor(s, e));
		return l;
	}

}
