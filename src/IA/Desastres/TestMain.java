package IA.Desastres;

import java.util.ArrayList;
import java.util.Arrays;

import IA.Desastres.Estat.TipusInicial;

public class TestMain {
	public static void main (String[] args) {
		Grupos grupos = new Grupos(50,1234);
		int i = 0; for(Grupo g : grupos) printGrupo(g, i++);
		System.out.println("");
		Centros c = new Centros(5,2,1234);
		ContextEstat cntx = new ContextEstat(grupos,c, false);
		Estat e = new Estat(cntx, 1234);
		e.generaSoucioInicial(TipusInicial.GREEDY);
		Estat e1 = new Estat(e);
		System.out.println(e.toString());
		System.out.println(e1.toString());
		System.out.println("TEMPS");
		System.out.println("START");
		System.out.println(e.toString());
		System.out.println(e.getTempsViatges());
		System.out.println(e.getTempsViatges());
		//H3: {1, 12}{15, 20, 22}{36}
		//e.mouGrups(Gi, Hi, Vi, Hj, Vj)
		e.mouGrups(1, 3, 0, 3, 2);
		System.out.println("MOU");
		System.out.println(e.toString());
		System.out.println(e.getTempsViatges());
		System.out.println(e.getTempsViatges());
		//e.intercambiaGrups(Hi, Vi, Gi, Hj, Vj, Gj)
		e.intercambiaGrups(3, 0, 0, 3, 2, 0);
		System.out.println("Intercambia");
		System.out.println(e.toString());
		System.out.println(e.getTempsViatges());
		System.out.println(e.getTempsViatges());
	}

	private static void printGrupo(Grupo g, int i) {
		System.out.print("G"+i+"("+g.getNPersonas()+"), ");
	}
	
	private static void printDebug(Estat e) {
		ArrayList<ArrayList<ArrayList<Grupo>>> helicopters = e.getHelicopters();
		int i = 0;
		for(ArrayList<ArrayList<Grupo>> h : helicopters) {
			System.out.print("H"+i+": ");
			for(ArrayList<Grupo> v : h) {
				int sum = 0; for(Grupo g : v) sum += g.getNPersonas(); 
				System.out.print("{"+sum+"}");
			}
			System.out.println();
			++i;
		}
	}
}
