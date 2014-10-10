package IA.Desastres;

import java.util.ArrayList;
import java.util.Random;

/**Representa la part dinàmica de l'estat del problema, és a dir, codifica una solució, i implementa els operadors de cerca.
 * @author Josep Sánchez Ferreres
 * @author Xavier Conejo*/
public class Estat {
	
	/**La capacitat dels helicòpters*/
	private static final int CAPACITAT_HELICOPTERS=15;

	/**El màxim nombre de grups que pot portar un helicopter en un viatge*/
	private static final int GRUPS_PER_HELICOPTER=15;
	
	/**Tipus de solució inicial*/
	static enum TipusInicial {RANDOM, GREEDY};
	
	/**Informació estàtica sobre l'estat*/
	private ContextEstat context;

	/**Generador de nombres aleatoris*/
	private Random random;
	
	/**Guarda, per cada helicòpter, el temps total que triga en realitzar tots els seus viatges en minuts*/
	private float temps;
	
	/**Guarda, per cada helicòpter, la llista de viatges amb els grups que ha de rescatar*/
	private ArrayList<ArrayList<ArrayList<Grupo>>> helicopters;
	
	/**Constructora buida. S'ha de cridar el generador d'estats inicials després d'aquest mètode.
	 * @param context El context del problema per a aquest estat.*/
	public Estat(ContextEstat context, long seed) {
		this.context = context;
		random = new Random(seed);
		//Assumim que cada centre té el mateix nombre d'helicòpters.
		int nH = context.getCentros().size()*context.getCentros().get(0).getNHelicopteros();
		this.helicopters = new ArrayList<ArrayList<ArrayList<Grupo>>>(nH);
		for(int i = 0; i < nH; ++i) helicopters.add(new ArrayList<ArrayList<Grupo>>());
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
		int[] l = new int[helicopters.size()]; for(int i = 0; i < l.length; ++i) l[i] = i;
		shuffle(l);
		int n = l.length-1;
		Grupos grupos = context.getGrups();
		//invariant: n apunta a l'ultim element randomitzat de la llista. Tots els viatges
		//			 de tots els helicòpters contenen menys de 3 grups i menys de 15 persones.
		for(Grupo g : grupos) {
			//Afegim grup a l'helicopter
			int h = l[0]; 
			int lastV = helicopters.get(h).size()-1;
			//lastV conté l'índex de lúltim viatge de l'helicòpter si en té algun
			if(lastV >= 0 && helicopters.get(h).get(lastV).size() < GRUPS_PER_HELICOPTER-1 && !moureIncompatible(helicopters.get(h).get(lastV),g)) {
				helicopters.get(h).get(lastV).add(g);
			}
			else {
				ArrayList<Grupo> nv = new ArrayList<Grupo>(GRUPS_PER_HELICOPTER);
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
	
	/**Intercambia el viatge Vi de l'helicòpter Hi al viatge Vj de l'helicopter Hj
	 * @pre Hi, Hj inRange(helicopters); Vi inRange(helicopters[Hi]); Vj inRange(helicopters[Hj]*/
	public void intercambiaViatges (int H1, int Vi, int H2, int Vj) {
		ArrayList<Grupo> i = helicopters.get(H1).get(Vi);
		helicopters.get(H1).set(Vi, helicopters.get(H2).get(Vj));
		helicopters.get(H2).set(Vj, i);
	}
	
	/**Intercanvia el grup G del viatge Vi de l'helicòpter Hi pel grup Gj del viatge Vj de l'helicopter Hj
	 * @pre Hi, Hj inRange(helicopters); Vi inRange(helicopters[Hi]); Vj inRange(helicopters[Hj]*/
	public boolean intercambiaGrups (int Hi, int Vi, int Gi, int Hj, int Vj, int Gj) {
		Grupo i = helicopters.get(Hi).get(Vi).get(Gi);
		Grupo j = helicopters.get(Hj).get(Vj).get(Gj);
		if(swapIncompatible(helicopters.get(Hj).get(Vj),i,j)) return false;
		if(swapIncompatible(helicopters.get(Hi).get(Vi),j,i)) return false;
		recalcularTemps(Hi, Vi, Gi, -1);
		recalcularTemps(Hj, Vj, Gj, -1);
		helicopters.get(Hi).get(Vi).set(Vi, helicopters.get(Hj).get(Vj).get(Gj));
		helicopters.get(Hj).get(Vj).set(Gj, i);
		recalcularTemps(Hi, Vi, Gj, 1);
		recalcularTemps(Hj, Vj, Gi, 1);
		return true;
	}
	
	/**Mou el grup G del viatge Vi de l'helicòpter Hi al viatge Vj de l'helicopter Hj
	 * @pre Hi, Hj inRange(helicopters); Vi inRange(helicopters[Hi]); Vj inRange(helicopters[Hj]*/
	public boolean mouGrups (int G, int Hi, int Vi, int Hj, int Vj) {
		Grupo i = helicopters.get(Hi).get(Vi).get(G);
		if (helicopters.get(Hj).get(Vj).size() == GRUPS_PER_HELICOPTER) return false;
		if (moureIncompatible(helicopters.get(Hj).get(Vj), i)) return false;
		recalcularTemps(Hi, Vi, G, -1);
		helicopters.get(Hj).get(Vj).add(i);
		helicopters.get(Hi).get(Vj).remove(G);
		recalcularTemps(Hj, Vj, G, 1);
		return true;
	}
	
	/**Mou el grup G del viatge Vi de l'helicòpter Hi en un nou viatge de
	 * l'helicopter j-èssim
	 * @pre Hi,Hj inRange(helicopters); Vi inRange(helicopters[Hi]; G inRange(helicopters[Hi][Vi])*/
	public void mouGrupNouViatge(int G, int Hi, int Vi, int Hj) {
		Grupo i = helicopters.get(Hi).get(Vi).get(G);
		recalcularTemps(Hi, Vi, G, -1);
		ArrayList<Grupo> viatge = new ArrayList<Grupo>(3);
		viatge.add(i);
		helicopters.get(Hj).add(viatge);
		helicopters.get(Hi).get(Vi).remove(G);
		recalcularTemps(Hj,  helicopters.get(Hj).size() - 1, 0, 1);
	}
	
	
	/*  =======================================================
	  
	                     Funcions d'utilitat
	  
	    =======================================================
	 */
	
	
	/**Retorna fals si en afegir gr a V el viatge té massa grups i/o persones.
	 * @pre gr !in V*/
	private boolean moureIncompatible(ArrayList<Grupo> V, Grupo gr) {
		int capacidad = gr.getNPersonas();
		for(Grupo g : V) {
			capacidad += g.getNPersonas();	
			if(capacidad > CAPACITAT_HELICOPTERS) return true;
		}
		return false;
	}
	/**Retorna fals si en ficar Gin i treure Gout de V aquest viatge queda ple. cert altrament
	 * @pre Gout in V*/
	private boolean swapIncompatible(ArrayList<Grupo> V, Grupo Gout, Grupo Gin) {
		int capacidad = Gin.getNPersonas() - Gout.getNPersonas();
		for(Grupo g : V) {
			capacidad += g.getNPersonas();	
			if(capacidad > CAPACITAT_HELICOPTERS) return true;
		}
		return false;
	}
	
	private void recalcularTemps(int H, int V, int G, int X) {
		Grupo g = helicopters.get(H).get(V).get(G);
		int x = g.getCoordX();
		int y = g.getCoordY();
		int a1, a2, b1, b2;
		int nH = context.getCentros().get(0).getNHelicopteros();
		Centro c = context.getCentros().get(H/nH);
		if (G > 0) {
			Grupo aux1 = helicopters.get(H).get(V).get(G - 1);
			a1 = aux1.getCoordX() - x;
			b1 = aux1.getCoordY() - y;
		}
		else {
			a1 = c.getCoordX() - x;
			b1 = c.getCoordY() - y;
		}
		if (G < helicopters.get(H).get(V).size() - 1) {
			Grupo aux2 = helicopters.get(H).get(V).get(G + 1);
			a2 = aux2.getCoordX() - x;
			b2 = aux2.getCoordY() - y;
		}
		else {
			a2 = c.getCoordX() - x;
			b2 = c.getCoordY() - y;
		}
		temps = (float) (temps + X*((1/1.66)*Math.sqrt((a1*a1) + (b1*b1)) + (1/1.66)*Math.sqrt((a2*a2) + (b2*b2))));	
	}
	
	/**Retorna el grup G-èssim de l'helicòpter H.
	 * @pre H inRange(helicopters); G inRange(helicopters[H])*/
	public ArrayList<Grupo> getViatge(int H, int G) {
		return helicopters.get(H).get(G);	
	}

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
	
	/**Retorna l'índex de g al vector de grups que hi ha a context, o -1 si no el troba. Notar que la 
	 * complexitat d'aquesta operació és O(|G|) (on G és el conjunt de grups).*/
	private int buscaGrupIndex(Grupo g) {
		Grupos gs = context.getGrups();
		for(int i = 0; i < gs.size(); ++i) {
			if(g == gs.get(i)) return i;
		}
		return -1;
	}
	

	/*  =======================================================
	  
                             Overrides
	  
	    =======================================================
	 */
	
	/**Imprimeix la sol·lució completa que representa aquest estat com un string.*/
	@Override
	public String toString() {
		String ret="";
		int i = 0; for(ArrayList<ArrayList<Grupo>> h : helicopters) {
			ret += "H"+i+": ";
			for(ArrayList<Grupo> v : h) {
				ret += "{"+buscaGrupIndex(v.get(0));
				for(int j = 1; j < v.size(); ++j) {
					ret += ", "+buscaGrupIndex(v.get(j));
				}
				ret += "}";
			}
			++i;
			ret += "\n";
		}
		return ret;
		
	}
	
}
