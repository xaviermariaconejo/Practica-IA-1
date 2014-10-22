package IA.Desastres;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;
import IA.Desastres.Estat.TipusInicial;

public class Main3 {
	
	private static long seed = 1234;
	private static double lamda[] = {0.001, 0.005 , 0.5, 1, 2};
	private static int k[] = {1, 5, 20, 50, 100};
	
	public static void main (String[] args) {
		
		for(int i = 0; i < lamda.length; ++i) {
			for(int j = 0; j < k.length; ++j) {
			Random r = new Random(1234);
			int n = 15;
			double sum = 0;
			for(int t = 0; t < 15; ++t) {
				seed = r.nextLong();
				//System.out.println("seed");
				try {
					Grupos grupos = new Grupos(100,(int) seed);
					Centros c = new Centros(5,1,(int) seed);
					ContextEstat cntx = new ContextEstat(grupos,c, false);
					Estat e_ini = new Estat(cntx, seed);
					e_ini.generaSoucioInicial(TipusInicial.GREEDY);
					//System.out.println("El greedy triga un total de: "+e_ini.getTempsViatges()+"min");
					Problem problem = new Problem(e_ini, new GeneradorEstatsAnnealing(), new EstatFinal(), new FuncioHeuristica1());
					Search search = new SimulatedAnnealingSearch(10000,100,k[j],lamda[i]);
					long time = System.currentTimeMillis();
					SearchAgent agent = new SearchAgent(problem, search);
					//System.out.println((System.currentTimeMillis() - time));
					int rot, mov, mou_n, swap_g, swap_v;
					rot = mov = mou_n = swap_g = swap_v = 0;
					List<Estat> l = search.getPathStates();
					if(l.size() > 0) sum += l.get(l.size()-1).getTempsViatges();
					else --n;
					//printActions(agent.getActions());
					//printInstrumentation(agent.getInstrumentation());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(n != 0) System.out.print((sum/(double)n)+"\t");
			else System.out.print("NULL\t");
			}
			System.out.println();
		}
	}
	
	private static void printInstrumentation(Properties properties) {
		Iterator keys = properties.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			String property = properties.getProperty(key);
			System.out.println(key + " : " + property);
		}

	}

	private static void printActions(List actions) {
		for (int i = 0; i < actions.size(); i++) {
			String action = (String) actions.get(i);
			System.out.println(action);
		}
	}

}
