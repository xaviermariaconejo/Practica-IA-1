package IA.Desastres;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import IA.Desastres.Estat.TipusInicial;

public class Main2 {
	
	private static long seed = 1234;
	
	
	public static void main (String[] args) {
		Random random = new Random(1234);
		for(int i = 0; i < 15; ++i) {
			seed = random.nextLong();
			System.out.println("SEED: "+seed);
			
			try {
				Grupos grupos = new Grupos(100,(int) seed);
				Centros c = new Centros(5,1,(int) seed);
				ContextEstat cntx = new ContextEstat(grupos,c, false);
				Estat e_ini = new Estat(cntx, seed);
				e_ini.generaSoucioInicial(TipusInicial.GREEDY);
				//System.out.println("El greedy triga un total de: "+e_ini.getTempsViatges()+"min");
				Problem problem = new Problem(e_ini, new GeneradorEstats(), new EstatFinal(), new FuncioHeuristica1());
				Search search = new HillClimbingSearch();
				long time = System.currentTimeMillis();
				SearchAgent agent = new SearchAgent(problem, search);
				//System.out.println("Elapsed time: "+(System.currentTimeMillis() - time));
				long elapsedTime = System.currentTimeMillis() - time;
				List<Estat> l = search.getPathStates();
				double millorat = l.get(l.size()-1).getTempsViatges();
				System.out.println(e_ini.getTempsViatges()+ "\t" + elapsedTime + "\t" +  millorat);
				printInstrumentation(agent.getInstrumentation());
			} catch (Exception e) {
				e.printStackTrace();
			}
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
