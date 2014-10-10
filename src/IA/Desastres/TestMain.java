package IA.Desastres;

import java.util.Arrays;

import IA.Desastres.Estat.TipusInicial;

public class TestMain {
	public static void main (String[] args) {
		Grupos grupos = new Grupos(50,1234);
		int i = 0; for(Grupo g : grupos) printGrupo(g, i++);
		Centros c = new Centros(5,2,1234);
		ContextEstat cntx = new ContextEstat(grupos,c);
		Estat e = new Estat(cntx);
		e.generaSoucioInicial(TipusInicial.RANDOM);
		System.out.println(e.toString());
	}

	private static void printGrupo(Grupo g, int i) {
		System.out.print("G"+i+"("+g.getNPersonas()+"), ");
	}
}
