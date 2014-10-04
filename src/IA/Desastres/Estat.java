package IA.Desastres;

import java.util.ArrayList;

/**Representa la part dinàmica de l'estat del problema, és a dir, codifica una solució, i implementa els operadors de cerca.
 * @author Josep Sánchez Ferreres*/
public class Estat {
	
	private ContextEstat context;
	
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
	}
	
	/**Constructor de còpia*/
	public Estat(Estat e) {
		this.context = e.context;
		this.helicopters = new ArrayList<ArrayList<Grupo>>(e.helicopters.size());
		//No fem deep copy perquè no ens interessa clonar grups, només punters a grups.
		for(int i = 0; i < helicopters.size(); ++i) this.helicopters.set(i, (ArrayList<Grupo>)e.helicopters.get(i).clone());
	}
	
	/**Intercanvia els grups i-èssim i j-èssim dels helicòpters H1 i H2
	 * @pre H1,H2 inRange(helicopters); Gi inRange(helicopters[H1]); Gj inRange[helicopters[H2])
	 * @post helicopters[H1][Gi] <- pre.helicopters[H2][Gj]; helicopters[H2][Gj] <- pre.helicopters[H1][Gi]*/
	public void intercambiaGrupsFora (int H1, int Gi, int H2, int Gj) {
		Grupo i = helicopters.get(H1).get(Gi);
		helicopters.get(H1).set(Gi, helicopters.get(H2).get(Gj));
		helicopters.get(H2).set(Gj, i);
	}
	
	/**Intercanvia els grups i-èssim i j-èssim de l'helicòpter H
	 * @pre H inRange(helicopters); Gi,Gj inRange(helicopters[H]);
	 * @post helicopters[H][Gi] <- pre.helicopters[H][Gj]; helicopters[H][Gj] <- pre.helicopters[H][Gi]*/
	public void intercambiaGrupsDins (int H, int Gi, int Gj) {
		Grupo i = helicopters.get(H).get(Gi);
		helicopters.get(H).set(Gi, helicopters.get(H).get(Gj));
		helicopters.get(H).set(Gj, i);
	}
	
	/**Retorna el grup G-èssim de l'helicòpter H.
	 * @pre H inRange(helicopters); G inRange(helicopters[H])*/
	public Grupo getGrup(int H, int G) {
		return helicopters.get(H).get(G);		
	}
	
	@Override
	public String toString() {
		//TODO: Retornar una representació en string de l'estat.
		return "";
	}
	
}
