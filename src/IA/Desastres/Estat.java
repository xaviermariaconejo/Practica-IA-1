package IA.Desastres;

import java.util.ArrayList;
import java.util.Random;

/**Representa la part dinàmica de l'estat del problema, és a dir, codifica una solució, i implementa els operadors de cerca.
 * @author Josep Sánchez Ferreres*/
public class Estat {
	
	/**Tipus de solució inicial*/
	static enum TipusInicial {RANDOM, GREEDY};
	
	/**Informació estàtica sobre l'estat*/
	private ContextEstat context;

	/**Generador de nombres aleatoris*/
	private Random random;
	
	/**Guarda, per cada helic�pter, el temps total que triga en realitzar tots els seus viatges en minuts*/
	private float temps;
	
	/**Guarda, per cada helicòpter, la llista de viatges amb els grups que ha de rescatar*/
	private ArrayList<ArrayList<ArrayList<Grupo>>> helicopters;
	
	/**Constructora buida. S'ha de cridar el generador d'estats inicials després d'aquest mètode.
	 * @param context El context del problema per a aquest estat.*/
	public Estat(ContextEstat context) {
		this.context = context;
		random = new Random();
		//Assumim que cada centre té el mateix nombre d'helicòpters.
		int nH = context.getCentros().size()*context.getCentros().get(0).getNHelicopteros();
		this.helicopters = new ArrayList<ArrayList<ArrayList<Grupo>>>(nH);
		for(int i = 0; i < nH; ++i) helicopters.set(i, new ArrayList<ArrayList<Grupo>>());
		this.temps = 0.0f;
	}
	
	/**Constructora de còpia. Crea una còpia de l'estat proporcionat.
	 * @param e L'estat anterior*/
	public Estat(Estat e) {
		this.context = e.context;
		this.helicopters = new ArrayList<ArrayList<ArrayList<Grupo>>>(e.helicopters.size());
		int i = 0;
		for(ArrayList<ArrayList<Grupo>> h : e.helicopters) {
			this.helicopters.set(i, (ArrayList<ArrayList<Grupo>>) h.clone());
			int j = 0;
			for(ArrayList<Grupo> v : e.helicopters.get(i)) {
				this.helicopters.get(i).set(j, (ArrayList<Grupo>) v.clone());
				++j;
			}
			++i;
		}
		this.temps = 0.0f;
	}
	
	
	/*  =======================================================
	  
	                 Generació de la solució inicial
	  
	    =======================================================
	 */
	
	/**Estableix la solució que guarda aquest estat*/
	public void generaSoucioInicial(TipusInicial t) {
		switch(t) {
		case RANDOM: generaSolucioInicial1(); break;
		case GREEDY: generaSolucioInicial2(); break;
		}
		calcularTemps();
	}
	
	/**Genera una solució inicial aleatoria amb els grups distribuïts equitativament entre els helicòpters
	 * @pre "helicopters" inicialitzat.*/
	private void generaSolucioInicial1() {
		int H = 0;
		int[] l = new int[helicopters.size()]; for(int i = 0; i < l.length; ++i) l[i] = i;
		shuffle(l);
		int n = l.length-1;
		Grupos grupos = context.getGrups();
		//invariant: n apunta a l'ultim element randomitzat de la llista
		for(Grupo g : grupos) {
			//Afegim grup a l'helicopter
			int h = l[0]; 
			int lastV = helicopters.get(h).size()-1;
			if(lastV < 0 && helicopters.get(h).get(lastV).size() < 2) {
				helicopters.get(h).get(lastV).add(g);
			}
			else {
				ArrayList<Grupo> nv = new ArrayList<Grupo>(3);
				nv.add(g);
				helicopters.get(h).add(nv);
			}
			
			//Arreglem la cua random
			swap(l, 0, n); 
			n = n-1;
			if(n == 0) {
				n = l.length-1;
				shuffle(l);
			}
		}
	}
	
	/**TODO: Encara hem de pensar una solucio inicial greedy.
	 * @pre "helicopters" inicialitzat.*/
	private void generaSolucioInicial2() {
		
	}
	
	private void calcularTemps() {
		int nH = context.getCentros().get(0).getNHelicopteros();
		for(int h = 0; h < helicopters.size(); ++h) {
			for(ArrayList<Grupo> v :  helicopters.get(h)) {
				for(int i = 0; i < v.size() - 1; ++i) {
					Grupo g = v.get(i);
					temps = temps + g.getPrioridad()*g.getNPersonas();
					Grupo aux = v.get(i + 1);
					int x = g.getCoordX();
					int y = g.getCoordY();
					int a = aux.getCoordX() - x;
					int b = aux.getCoordY() - y;
					temps = (float) (temps + (1/1.66)*Math.sqrt((a*a) + (b*b)));
				}
				Centro c = context.getCentros().get(h/nH);
				int x = c.getCoordX();
				int y = c.getCoordY();
				int a1 = v.get(0).getCoordX() - x;
				int b1 = v.get(0).getCoordY() - y;
				int a2 = v.get(v.size() - 1).getCoordX() - x;
				int b2 = v.get(v.size() - 1).getCoordY() - y;
				temps = (float) (temps + (1/1.66)*Math.sqrt((a1*a1) + (b1*b1))
				+ (1/1.66)*Math.sqrt((a2*a2) + (b2*b2)) + v.get(v.size() - 1).getPrioridad()*v.get(v.size() - 1).getNPersonas());
			}
		}
	}

	
	/*  =======================================================
	  
	                      Operadors de cerca
	  
	    =======================================================
	 */
	
	public void intercambiaViatgesFora (int H1, int Vi, int H2, int Vj) {
		ArrayList<Grupo> i = helicopters.get(H1).get(Vi);
		//recalcularTemps1(H1, Gi);
		//recalcularTemps1(H2, Gj);
		helicopters.get(H1).set(Vi, helicopters.get(H2).get(Vj));
		helicopters.get(H2).set(Vj, i);
		//recalcularTemps2(H1, Gj);
		//recalcularTemps2(H2, Gi);
	}
	
	public void intercambiaGrups (int Hi, int Vi, int Gi, int Hj, int Vj, int Gj) {
		Grupo i = helicopters.get(Hi).get(Vi).get(Gi);
		//recalcularTemps1(H1, Gi);
		//recalcularTemps1(H2, Gj);
		helicopters.get(Hi).get(Vi).set(Vi, helicopters.get(Hj).get(Vj).get(Gj));
		helicopters.get(Hj).get(Vj).set(Gj, i);
		//recalcularTemps2(H1, Gj);
		//recalcularTemps2(H2, Gi);
	}
	
	
	public boolean mouGrups (int G, int Hi, int Vi, int Hj, int Vj) {
		Grupo i = helicopters.get(Hi).get(Vi).get(G);
		if (helicopters.get(Hj).get(Vj).size() == 3) return false;
		helicopters.get(Hj).get(Vj).add(i);
		helicopters.get(Hi).get(Vj).remove(G);
		return true;
	}
	
	private void recalcularTemps1(int H, int G) {
		/*Grupo g = helicopters.get(H).get(G);
		temps[H] = temps[H] - g.getPrioridad()*g.getNPersonas();
		Grupo aux = helicopters.get(H).get(G - 1);
		int x = g.getCoordX();
		int y = g.getCoordY();
		int a1 = aux.getCoordX() - x;
		int b1 = aux.getCoordY() - y;
		aux = helicopters.get(H).get(G + 1);
		int a2 = aux.getCoordX() - x;
		int b2 = aux.getCoordY() - y;
		temps[H] = temps[H] - (1/1.66)*Math.sqrt((a1*a1) + (b1*b1)) - (1/1.66)*Math.sqrt((a2*a2) + (b2*b2));*/		
	}
	
	private void recalcularTemps2(int H, int G) {
		/*Grupo g = helicopters.get(H).get(G);
		temps[H] = temps[H] + g.getPrioridad()*g.getNPersonas();*/
		// i en teoria lo de la altre funcio pero en comptes de restar en suma
		// que no he fet copy paste pq tic pensat reaprofitar codi entre les dos
		// funcions, pero com encara no se quans "if" i histories hi haura no ho puc fer encara
	}
	
	/**Retorna el grup G-èssim de l'helicòpter H.
	 * @pre H inRange(helicopters); G inRange(helicopters[H])*/
	public ArrayList<Grupo> getViatge(int H, int G) {
		return helicopters.get(H).get(G);	
	}


	/*  =======================================================
	  
	                     Funcions d'utilitat
	  
	    =======================================================
	 */
	
	/**Shuffle senzill que aleatoritza una llista d'enters fent swaps entre els elements. Implementat
	 * amb l'algorisme de Knuth shuffle.*/
	private void shuffle(int[] v) {
		for(int i = 0; i < v.length; ++i) swap(v, i, random.nextInt(i+v.length-i));
	}
	
	/**Intercanvia els elements i-èssim i j-èssim de v
	 * @pre i,j inRange(v)*/
	private void swap(int[] v, int i, int j) {
		int x = v[i];
		v[i] = v[j];
		v[j] = x;
	}
	

	/*  =======================================================
	  
                             Overrides
	  
	    =======================================================
	 */
	
	@Override
	public String toString() {
		//TODO: Retornar una representació en string de l'estat.
		return "LOL";
	}
	
}
