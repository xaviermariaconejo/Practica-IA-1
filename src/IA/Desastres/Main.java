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

public class Main {
	
	private static long seed = 1234;
	
	
	public static void main (String[] args) {
		Random r = new Random(1234);
		for(int j = 1; j<=10;++j){
			long t = 0;
			int n = 15;
			double sol = 0;
		for(int i = 0; i < 15; ++i) {
			seed = 1234;
			try {
				Grupos grupos = new Grupos(100,(int) seed);
				Centros c = new Centros(5,j,(int) seed);
				ContextEstat cntx = new ContextEstat(grupos,c, false);
				Estat e_ini = new Estat(cntx, seed);
				e_ini.generaSoucioInicial(TipusInicial.GREEDY);
				//System.out.println("El greedy triga un total de: "+e_ini.getTempsViatges()+"min");
				Problem problem = new Problem(e_ini, new GeneradorEstats(), new EstatFinal(), new FuncioHeuristica1());
				Search search = new HillClimbingSearch();
				long time = System.currentTimeMillis();
				SearchAgent agent = new SearchAgent(problem, search);
				t += System.currentTimeMillis() - time;
				int rot, mov, mou_n, swap_g, swap_v;
				rot = mov = mou_n = swap_g = swap_v = 0;
				/*for(String s : (List<String>)agent.getActions()) {
					if(s.contains("Mou Grup Normal")) ++mov;
					if(s.contains("Rotaci")) ++rot;
					if(s.contains("Nou")) ++mou_n;
					if(s.contains("Intercambi Viatges")) ++swap_v;
					if(s.contains("Intercambi Grups")) ++swap_g;
				}*/
				//System.out.println(swap_g+"\t"+mov+"\t"+rot+"\t"+mou_n+"\t"+swap_v);
				//System.out.println(seed);
				List<Estat> l = search.getPathStates();
				if(l.size() > 0) sol += l.get(l.size()-1).getTempsViatges();
				else --n;
				//printActions(agent.getActions());
				//printInstrumentation(agent.getInstrumentation());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(t/n);
		System.out.println(sol/n);
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
