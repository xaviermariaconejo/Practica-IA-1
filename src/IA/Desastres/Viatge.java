package IA.Desastres;

public class Viatge {
	private Grupo[] grups;
	private int capacitat;
	
	public Viatge(Grupo[] gs) {
	}
	
	public static void swap(Viatge v1, Viatge v2, int G1, int G2, int C1, int C2) {
		Grupo g = v1.grups[G1];
		v1.grups[G1] = v2.grups[G2];
		v1.capacitat = -C1+C2;
		v2.grups[G2] = g;
		v2.capacitat = -C2+C1;
	}
	
	public void swap(int G1, int G2) {
		Grupo g = grups[G1];
		grups[G1] = grups[G2];
		grups[G2] = g;
	}
}
