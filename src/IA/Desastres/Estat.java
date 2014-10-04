package IA.Desastres;

import java.util.ArrayList;
import java.util.Random;

/**Representa la part dinàmica de l'estat del problema, és a dir, codifica una solució, i implementa els operadors de cerca.
 * @author Josep Sánchez Ferreres*/
public class Estat {
	
	private ContextEstat context;
	private Random random;
	
	/**Guarda, per cada helicòpter, la llista de grups que ha de rescatar*/
	private ArrayList<ArrayList<Grupo>> helicopters;
	
	/**Constructora
	 * @param context El context del problema per a aquest estat.*/
	public Estat(ContextEstat context) {
		this.context = context;
		random = new Random();
		//Assumim que cada centre té el mateix nombre d'helicòpters.
		int nH = context.getCentros().size()*context.getCentros().get(0).getNHelicopteros();
		this.helicopters = new ArrayList<ArrayList<Grupo>>(nH);
		for(int i = 0; i < nH; ++i) helicopters.set(i, new ArrayList<Grupo>());
	}
	
	/**Genera una solució inicial aleatoria amb els grups distribuïts equitativament entre els helicòpters
	 * @pre helicopters inicialitzat.*/
	private void generaSolucioInicial1() {
		int H = 0;
		int[] l = new int[helicopters.size()]; for(int i = 0; i < l.length; ++i) l[i] = i;
		shuffle(l);
		int n = l.length-1;
		Grupos grupos = context.getGrups();
		//invariant: n apunta a l'ultim element randomitzat de la llista
		for(Grupo g : grupos) {
			int h = l[0]; 
			helicopters.get(h).add(g);
			swap(l, 0, n); 
			n = n-1;
			if(n == 0) {
				n = l.length-1;
				shuffle(l);
			}
		}
	}
	
	/**Shuffle senzill que aleatoritza una llista d'enters fent swaps entre els elements. Implementat
	 * amb l'algorisme de Knuth shuffle.*/
	private void shuffle(int[] v) {
		for(int i = 0; i < v.length; ++i) swap(v, i, random.nextInt(i+v.length-i));
	}
	
	/**Swaps the elements i and j in v*/
	private void swap(int[] v, int i, int j) {
		int x = v[i];
		v[i] = v[j];
		v[j] = x;
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
