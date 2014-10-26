package IA.Desastres;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import IA.Desastres.Estat.TipusInicial;
import aima.search.framework.HeuristicFunction;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;

public class MainEntrega {
	public static void main (String[] args) throws Exception {
		boolean verbose = true;
		for(String s : args) if(s.equals("-nv")) verbose = false;
		Scanner scr = new Scanner(System.in);
		if(verbose)System.out.println("Introdueix la llavor del problema (-1 per una d'aleatoria):");
		long l = scr.nextLong();
		if(verbose)System.out.println("Introdueix els següents paràmetres: \n\t- Nombre de grups\n\t- Nombre de centres\n\t- Helicòpters per centre");
		int G = scr.nextInt();
		int C = scr.nextInt();
		int H = scr.nextInt();
		
		Grupos grupos = new Grupos(G,(int) l);
		Centros c = new Centros(C,H,(int) l);
		
		if(verbose)System.out.println("Quin heurístic vols utilitzar?\n\t1. Suma total de temps\n\t2. Suma total de temps minimitzant el temps mínim de rescat de ferits");
		int heur = scr.nextInt();
		boolean segonHeuristic = (heur == 2);
		ContextEstat cntx = new ContextEstat(grupos,c, segonHeuristic);
		Estat e_ini = new Estat(cntx, l);
		e_ini.generaSoucioInicial(TipusInicial.GREEDY);
		if(verbose)System.out.println("Quin algorisme de cerca vols utilitzar?\n\t1. Hill Climbing\n\t2.Simulated Annealing");
		int alg = scr.nextInt();
		
		System.out.println("================================================");
		System.out.println("            Resultats de l'execució");
		System.out.println("================================================");
		System.out.println();
		System.out.println("Solució inicial: "+e_ini.getTempsViatges()+" min");
		System.out.println(e_ini.toString());
		
		HeuristicFunction f;
		if(heur == 1) f = new FuncioHeuristica1();
		else f = new FuncioHeuristica2();
		
		Problem problem = new Problem(e_ini, new GeneradorEstats(), new EstatFinal(), f);
		Search search;
		if(alg == 1) {
			search = new HillClimbingSearch();
		}
		else {
			search = new SimulatedAnnealingSearch(10000,100,5,0.005);
		}
		long time = System.currentTimeMillis();
		SearchAgent agent = new SearchAgent(problem, search);
		long elapsedtime = System.currentTimeMillis() - time;
		
		List<Estat> l1 = search.getPathStates();
		if(l1.size() > 0) {
			System.out.println("Solució millorada: "+(l1.get(l1.size()-1).getTempsViatges())+" min");
			System.out.println(l1.get(l1.size()-1).toString());
		}
		else {
			System.out.println("L'algoritme no ha pogut millorar la solució.");
			System.exit(0);
		}
		System.out.println();

		System.out.println("Temps d'execució: "+ (elapsedtime)+" ms");
		
		System.out.println("Vols veure la traça d'operadors d'AIMA? (1 - si, 2 - no)");
		int trace = scr.nextInt();
		if(trace == 1) {
			printActions(agent.getActions());
			printInstrumentation(agent.getInstrumentation());
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
