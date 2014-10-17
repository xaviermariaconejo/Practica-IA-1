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
		Estat estat2 = new Estat((Estat)state);
		Estat estat3 = new Estat ((Estat)state);
		boolean op_exit = false;
		boolean op_exit2 = false;
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
					e3.intercambiaGrups(h, v, 1, h, v, 2);
					String s1 = "Rotacio Grups (Hi, "+h+", Vi: "+v+", Gi: 0, Hj: "+h+", Vj: "+v+", Gj: 1)";
					String s3 = "Rotacio Grups (Hi: "+h+", Vi: "+v+", Gi: 1, Hj: "+h+", Vj: "+v+", Gj: 2)";
					l.add(new Successor(s1, e1));
					l.add(new Successor(s3, e3));
				}				
				for(int haux = 0; haux < helicopters.size(); ++haux)
				{

					/*for(int g = 0; g < helicopters.get(h).get(v).size(); ++g)
					{
						Estat estat1 = new Estat ((Estat)state);
						estat1.mouGrupNouViatge(g, h, v, haux);
						l.add(new Successor("Mou Grup Viatge Nou (Hi: "+h+", Vi: "+v+", Gi: "+g+", Hj: "+haux+")", estat1));
					}*/
					for(int vaux = 0; vaux < helicopters.get(haux).size(); ++ vaux)
					{
						/*if (haux > h || (haux == h && v != vaux && ((Estat)state).esSegonHeuristic()))
						{
							Estat e = new Estat ((Estat)state);
							e.intercambiaViatges(h, v, haux, vaux);
							l.add(new Successor("Intercambi Viatges (Hi: "+h+", Vi: "+v+", Hj: "+haux+", Vj: "+vaux+")", e));
						}*/
						for(int g = 0; g < helicopters.get(h).get(v).size(); ++g)
						{
							if ((h == haux && v != vaux) || h != haux)
							{
								if(op_exit) estat2 = new Estat ((Estat)state);
								op_exit = estat2.mouGrups(g, h, v, haux, vaux);
								if(op_exit)
									l.add(new Successor("Mou Grup Normal (Hi: "+h+", Vi: "+v+", Gi: "+g+", Hj: "+haux+", Vj: "+vaux+")", estat2));
							}
							for(int gaux = 0; gaux < helicopters.get(haux).get(vaux).size(); ++gaux)
							{
								if((h == haux && v != vaux) || haux > h)
								{
									if(op_exit2) estat3 = new Estat ((Estat)state);
									op_exit2 = estat3.intercambiaGrups(h, v, g, haux, vaux, gaux);
									if(op_exit2)
										l.add(new Successor("Intercambi Grups (Hi: "+h+", Vi: "+v+", Gi: "+g+", Hj: "+haux+", Vj: "+vaux+", Gj: "+gaux+")", estat3));
								}
							}
						}
					}
				}
			}
		}				
		return l;
	}
}
