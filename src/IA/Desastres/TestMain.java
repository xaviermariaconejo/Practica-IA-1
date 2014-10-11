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
		e.generaSoucioInicial(TipusInicial.RANDOM);
		printDebug(e);
		System.out.println(e.toString());
		System.out.println(e.mouGrups(0, 2, 1, 6, 0));
		System.out.println(e.toString());
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
