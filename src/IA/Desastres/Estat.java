package IA.Desastres;

import java.util.ArrayList;

/**Representa la part dinàmica de l'estat del problema, és a dir, codifica una solució, i soporta els operadors de cerca.
 * @author Josep Sánchez Ferreres*/
public class Estat {
	
	private ContextEstat context;
	
	/**Guarda, per cada helic�pter, el temps total que triga en realitzar tots els seus viatges en minuts*/
	private double[] temps;
	
	/**Guarda, per cada helicòpter, la llista de grups que ha de rescatar*/
	private ArrayList<ArrayList<Grupo>> helicopters;
	
	/**Constructora
	 * @param context El context del problema per a aquest estat.*/
	public Estat(ContextEstat context) {
		this.context = context;
		//Assumim que cada centre té el mateix nombre d'helicòpters.
		int nH = context.getCentros().size()*context.getCentros().get(0).getNHelicopteros();
		this.helicopters = new ArrayList<ArrayList<Grupo>>(nH);
		for(int i = 0; i < nH; ++i) helicopters.set(i, new ArrayList<Grupo>());
		this.temps = new double[nH];
	}
	
	/**Constructor de còpia*/
	public Estat(Estat e) {
		this.context = e.context;
		this.helicopters = new ArrayList<ArrayList<Grupo>>(e.helicopters.size());
		//No fem deep copy perquè no ens interessa clonar grups, només punters a grups.
		for(int i = 0; i < helicopters.size(); ++i) this.helicopters.set(i, (ArrayList<Grupo>)e.helicopters.get(i).clone());
		this.temps = new double[e.temps.length];
		System.arraycopy(e.temps,0,temps,0,temps.length); 
	}
	
	/**Intercanvia els grups i-èssim i j-èssim dels helicòpters H1 i H2
	 * @pre H1,H2 inRange(helicopters); Gi inRange(helicopters[H1]); Gj inRange[helicopters[H2])
	 * @post helicopters[H1][Gi] <- pre.helicopters[H2][Gj]; helicopters[H2][Gj] <- pre.helicopters[H1][Gi]*/
	public void intercambiaGrupsFora (int H1, int Gi, int H2, int Gj) {
		Grupo i = helicopters.get(H1).get(Gi);
		recalcularTemps1(H1, Gi);
		recalcularTemps1(H2, Gj);
		helicopters.get(H1).set(Gi, helicopters.get(H2).get(Gj));
		helicopters.get(H2).set(Gj, i);
		recalcularTemps2(H1, Gj);
		recalcularTemps2(H2, Gi);
	}
	
	/**Intercanvia els grups i-èssim i j-èssim de l'helicòpter H
	 * @pre H inRange(helicopters); Gi,Gj inRange(helicopters[H]);
	 * @post helicopters[H][Gi] <- pre.helicopters[H][Gj]; helicopters[H][Gj] <- pre.helicopters[H][Gi]*/
	public void intercambiaGrupsDins (int H, int Gi, int Gj) {
		Grupo i = helicopters.get(H).get(Gi);
		recalcularTemps1(H, Gi);
		recalcularTemps1(H, Gj);
		helicopters.get(H).set(Gi, helicopters.get(H).get(Gj));
		helicopters.get(H).set(Gj, i);
		recalcularTemps2(H, Gi);
		recalcularTemps2(H, Gj);
	}
	
	private void recalcularTemps1(int H, int G) {
		Grupo g = helicopters.get(H).get(G);
		temps[H] = temps[H] - g.getPrioridad()*g.getNPersonas();
		if ()
		Grupo aux = helicopters.get(H).get(G - 1);
		int x = g.getCoordX();
		int y = g.getCoordY();
		int a1 = aux.getCoordX() - x;
		int b1 = aux.getCoordY() - y;
		aux = helicopters.get(H).get(G + 1);
		int a2 = aux.getCoordX() - x;
		int b2 = aux.getCoordY() - y;
		temps[H] = temps[H] - (1/1.66)*Math.sqrt((a1*a1) + (b1*b1)) - (1/1.66)*Math.sqrt((a2*a2) + (b2*b2));		
	}
	
	private void recalcularTemps2(int H, int G) {
		Grupo g = helicopters.get(H).get(G);
		temps[H] = temps[H] + g.getPrioridad()*g.getNPersonas();
	}
	
	@Override
	public String toString() {
		//TODO: Retornar una representació en string de l'estat.
		return "LOL";
	}
	
}
