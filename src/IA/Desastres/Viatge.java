package IA.Desastres;

/**@deprecated*/
public class Viatge {
	private Grupo[] grups;
	private int capacitat;
	private int size;
	
	public Viatge(Grupo[] gs) {
		grups = new Grupo[3];
		for(int i = 0; i < gs.length; ++i) grups[i] = gs[i];
		for(int i = gs.length; i < 3; ++i) grups[i] = null;
		size = gs.length;
	}
	
	public static void swap(Viatge v1, Viatge v2, int G1, int G2, int C1, int C2) {
		Grupo g = v1.grups[G1];
		v1.grups[G1] = v2.grups[G2];
		v1.capacitat = -C1+C2;
		v2.grups[G2] = g;
		v2.capacitat = -C2+C1;
	}
	
	public void swapInside(int G1, int G2) {
		Grupo g = grups[G1];
		grups[G1] = grups[G2];
		grups[G2] = g;
	}
	
	public static void move(Viatge v1, Viatge v2, int G, int C) {
		v2.grups[v2.size] = v1.grups[G];
		v1.grups[G] = null;
		-- v1.size; ++ v2.size;
		for(int i = G; i < 2; ++ i) {
			if(v1.grups[i] == null) return;
			v1.grups[i] = v1.grups[i+1];
		}
	}
	
	public static void main (String[] args) {
		Viatge v1, v2;
		Grupos gr = new Grupos(15, 1231);
		Grupo[] gs1 = {gr.get(0)/*,gr.get(1), gr.get(2)*/};
		Grupo[] gs2 = {gr.get(3),gr.get(4), /*gr.get(5)*/};
		
		v1 = new Viatge(gs1);
		v2 = new Viatge(gs2);

		for(int i = 0; i < v1.size; ++i) System.out.print(v1.grups[i].toString() + " "); System.out.println();
		for(int i = 0; i < v2.size; ++i) System.out.print(v2.grups[i].toString() + " "); System.out.println();
		
		move(v1, v2, 0, 2);
		move(v2, v1, 1, 2);

		for(int i = 0; i < v1.size; ++i) System.out.print(v1.grups[i].toString() + " "); System.out.println();
		for(int i = 0; i < v2.size; ++i) System.out.print(v2.grups[i].toString() + " "); System.out.println();
	}
}
