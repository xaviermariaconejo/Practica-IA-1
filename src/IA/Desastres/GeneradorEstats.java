package IA.Desastres;

import java.util.List;
import java.util.ArrayList;
import aima.search.framework.Successor;
import aima.search.framework.SuccessorFunction;

public class GeneradorEstats implements SuccessorFunction {

	@Override
	public List getSuccessors(Object state) {
		// TODO Auto-generated method stub
		List<Successor> l = new ArrayList<Successor>();
		ArrayList<ArrayList<ArrayList<Grupo>>> helicopters = ((Estat)state).getHelicopters();
		for(int h = 0; h < helicopters.size(); ++h)
		{
			for(int v = 0; v < helicopters.get(h).size(); ++v)
			{
				if (helicopters.get(h).get(v).size() == 3)
				{
					Estat e1 = new Estat ((Estat)state);
					Estat e2 = new Estat ((Estat)state);
					Estat e3 = new Estat ((Estat)state);
					e1.intercambiaGrups(h, v, 0, h, v, 1);
					e2.intercambiaGrups(h, v, 0, h, v, 2);
					e3.intercambiaGrups(h, v, 1, h, v, 2);
					String s1 = "Intercambi Grups (Hi: "+h+", Vi: "+v+", Gi: 0, Hj: "+h+", Vj: "+v+", Gj: 1)";
					String s2 = "Intercambi Grups (Hi: "+h+", Vi: "+v+", Gi: 0, Hj: "+h+", Vj: "+v+", Gj: 2)";;
					String s3 = "Intercambi Grups (Hi: "+h+", Vi: "+v+", Gi: 1, Hj: "+h+", Vj: "+v+", Gj: 2)";;
					l.add(new Successor(s1, e1));
					l.add(new Successor(s2, e2)); 
					l.add(new Successor(s3, e3));
				}				
				for(int haux = 0; haux < helicopters.size(); ++haux)
				{
					for(int vaux = 0; vaux < helicopters.get(haux).size(); ++ vaux)
					{
						if ((haux > h && vaux > v) || (haux >= h && vaux >= v && ((Estat)state).esSegonHeuristic()))
						{
							Estat e = new Estat ((Estat)state);
							e.intercambiaViatges(h, v, haux, vaux);
							l.add(new Successor("Intercambi Viatges (Hi: "+h+", Vi: "+v+", Hj: "+haux+", Vj: "+vaux+")", e));
						}
						for(int g = 0; g < helicopters.get(h).get(v).size(); ++g)
						{

							Estat estat = new Estat ((Estat)state);
							estat.mouGrupNouViatge(g, h, v, haux);
							l.add(new Successor("Mou Grup Viatge Nou (Hi: "+h+", Vi: "+v+", Gi: "+g+", Hj: "+haux+")", estat));
							if ((h == haux && v != vaux) || h != haux)
							{
								Estat est = new Estat ((Estat)state);
								if (est.mouGrups(g, h, v, haux, vaux))
									l.add(new Successor("Mou Grup (Hi: "+h+", Vi: "+v+", Gi: "+g+", Hj: "+haux+", Vj: "+vaux+")", est));
							}
							for(int gaux = 0; gaux < helicopters.get(haux).get(vaux).size(); ++gaux)
							{
								Estat aux = new Estat ((Estat)state);
								if (aux.intercambiaGrups(h, v, g, haux, vaux, gaux))
									l.add(new Successor("Intercambi Grups (Hi: "+h+", Vi: "+v+", Gi: "+g+", Hj: "+haux+", Vj: "+vaux+", Gj: "+gaux+")", aux));
							}
						}
					}
				}
			}
		}
				
		return l;
	}
}
