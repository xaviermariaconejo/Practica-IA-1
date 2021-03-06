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
	private static final int GRUPS_PER_HELICOPTER=3;

	private static final double INV_VEL_HEL = 1/1.66;
	
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
		this.helicopters = new ArrayList<ArrayList<ArrayList<Grupo>>>(e.helicopters);
		int i = 0;
		for(ArrayList<ArrayList<Grupo>> h : e.helicopters) {
			ArrayList<ArrayList<Grupo>> new_h = new ArrayList<ArrayList<Grupo>>(h);
			helicopters.set(i,new_h);
			int j = 0;
			for(ArrayList<Grupo> v : h) {
				new_h.set(j,new ArrayList<Grupo>(v));
				++j;
			}
			i++;
		}
		this.temps = e.temps;
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
	
	/**TODO: Genera una solució on s'assigan un grup a un helicopter del centre que esta més a prop. 
	 * @pre "helicopters" inicialitzat.*/
	private void generaSolucioInicial2() {
		ArrayList<ArrayList<Grupo>> cercania = new ArrayList<ArrayList<Grupo>>(context.getCentros().size()); 
		for(int i = 0; i < context.getCentros().size(); ++i) cercania.add(new ArrayList<Grupo>());
		Grupos grupos = context.getGrups();
		Centros centros = context.getCentros();
		float dist, aux;
		/*Per cada grup mirem quin es el centre que te mes a prop*/
		for(Grupo g : grupos) {
			int gx = g.getCoordX();
			int gy = g.getCoordY();
			int asignado = 0;
			dist = -1;
			int i = 0;
			for (Centro c : centros) {
				int cx = c.getCoordX() - gx;
				int cy = c.getCoordY() - gy;
				aux = (float)Math.sqrt((cx*cx) + (cy*cy));
				if (dist == -1 || dist > aux) {
					dist = aux;
					asignado = i;
				}
				++i;
			}
			cercania.get(asignado).add(g);			
		}
		int z = 0;
		int m = 0;
		/*Amb els grups ya repartits per els centres s'assigan cada grup a un helicopter del centre equitativament*/
		for(ArrayList<Grupo> h : cercania) {
			for (int j = 0; j < h.size(); ++j) {
				int lastV = helicopters.get(z+m*context.getCentros().get(0).getNHelicopteros()).size()-1;
				if(lastV >= 0 && helicopters.get(z+m*context.getCentros().get(0).getNHelicopteros()).get(lastV).size() < GRUPS_PER_HELICOPTER-1 && !moureIncompatible(helicopters.get(z+m*context.getCentros().get(0).getNHelicopteros()).get(lastV),h.get(j))) {
					helicopters.get(z+m*context.getCentros().get(0).getNHelicopteros()).get(lastV).add(h.get(j));
				}
				else {
					ArrayList<Grupo> nv = new ArrayList<Grupo>(GRUPS_PER_HELICOPTER);
					nv.add(h.get(j));
					helicopters.get(z+m*context.getCentros().get(0).getNHelicopteros()).add(nv);
				}
				++z;
				if (context.getCentros().get(0).getNHelicopteros() == z) z = 0;
			}
			z = 0;
			++m;
		}	
	}
	
	/**Calcula el temps total d'una solució
	 * @pre "helicopters" inicialitzat.*/
	private void calcularTemps() {
		//nH conté el nombre de helicopters de cada centre
		int nH = context.getCentros().get(0).getNHelicopteros();
		for(int h = 0; h < helicopters.size(); ++h) {
			for(ArrayList<Grupo> v :  helicopters.get(h)) {
				//Sumem el temps del viatge que hi ha entre dos grups i el temps que es triga en recollir-los, excepte el últim grup
				for(int i = 0; i < v.size() - 1; ++i) {
					Grupo g = v.get(i);
					temps = temps + g.getPrioridad()*g.getNPersonas();
					Grupo aux = v.get(i + 1);
					int x = g.getCoordX();
					int y = g.getCoordY();
					int a = aux.getCoordX() - x;
					int b = aux.getCoordY() - y;
					temps = (float) (temps + (INV_VEL_HEL)*Math.sqrt((a*a) + (b*b)));
				}
				//Sumem al temps el viatge que hi ha entre el centre al primer grup, i de l'últim grup al centre, a més
				//del temps de recollida de l'últim grup
				Centro c = context.getCentros().get(h/nH);
				int x = c.getCoordX();
				int y = c.getCoordY();
				int a1 = v.get(0).getCoordX() - x;
				int b1 = v.get(0).getCoordY() - y;
				int a2 = v.get(v.size() - 1).getCoordX() - x;
				int b2 = v.get(v.size() - 1).getCoordY() - y;
				temps = (float) (temps + (INV_VEL_HEL)*Math.sqrt((a1*a1) + (b1*b1))
				+ (INV_VEL_HEL)*Math.sqrt((a2*a2) + (b2*b2)) + v.get(v.size() - 1).getPrioridad()*v.get(v.size() - 1).getNPersonas());
				temps += 10; //Parche
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
		recalcularIntercambiaViatges(H1, Vi, -1);
		recalcularIntercambiaViatges(H2, Vj, -1);
		helicopters.get(H1).set(Vi, helicopters.get(H2).get(Vj));
		helicopters.get(H2).set(Vj, i);
		recalcularIntercambiaViatges(H1, Vi, 1);
		recalcularIntercambiaViatges(H2, Vj, 1);
	}
	
	/**Intercanvia el grup G del viatge Vi de l'helicòpter Hi pel grup Gj del viatge Vj de l'helicopter Hj
	 * @pre Hi, Hj inRange(helicopters); Vi inRange(helicopters[Hi]); Vj inRange(helicopters[Hj]*/
	public boolean intercambiaGrups (int Hi, int Vi, int Gi, int Hj, int Vj, int Gj) {
		Grupo i = helicopters.get(Hi).get(Vi).get(Gi);
		Grupo j = helicopters.get(Hj).get(Vj).get(Gj);
		if(swapIncompatible(helicopters.get(Hj).get(Vj),j,i)) return false;
		if(swapIncompatible(helicopters.get(Hi).get(Vi),i,j)) return false;
		recalcularIntercambiaGrups(Hi, Vi, Gi, -1);
		recalcularIntercambiaGrups(Hj, Vj, Gj, -1);
		helicopters.get(Hi).get(Vi).set(Gi, j);
		helicopters.get(Hj).get(Vj).set(Gj, i);
		recalcularIntercambiaGrups(Hi, Vi, Gi, 1);
		recalcularIntercambiaGrups(Hj, Vj, Gj, 1);
		return true;
	}
	
	/**Mou el grup G del viatge Vi de l'helicòpter Hi al viatge Vj de l'helicopter Hj
	 * @pre Hi, Hj inRange(helicopters); Vi inRange(helicopters[Hi]); Vj inRange(helicopters[Hj]*/
	public boolean mouGrups (int G, int Hi, int Vi, int Hj, int Vj) {
		Grupo i = helicopters.get(Hi).get(Vi).get(G);
		if (helicopters.get(Hj).get(Vj).size() == GRUPS_PER_HELICOPTER) return false;
		if (moureIncompatible(helicopters.get(Hj).get(Vj), i)) return false;
		recalcularTreuGrup(Hi, Vi, G);
		helicopters.get(Hj).get(Vj).add(i);
		helicopters.get(Hi).get(Vi).remove(G);
		if(helicopters.get(Hi).get(Vi).size() == 0) {
			helicopters.get(Hi).remove(Vi);
			temps -= 10;//Parche
			if(Hi == Hj && Vj > Vi) --Vj; //Parche del parche
		}
		recalcularPosaGrup(Hj, Vj, helicopters.get(Hj).get(Vj).size()-1);
		return true;
	}
	
	/**Mou el grup G del viatge Vi de l'helicòpter Hi en un nou viatge de
	 * l'helicopter j-èssim
	 * @pre Hi,Hj inRange(helicopters); Vi inRange(helicopters[Hi]; G inRange(helicopters[Hi][Vi])*/
	public void mouGrupNouViatge(int G, int Hi, int Vi, int Hj) {
		Grupo i = helicopters.get(Hi).get(Vi).get(G);
		recalcularTreuGrup(Hi, Vi, G);
		ArrayList<Grupo> viatge = new ArrayList<Grupo>(3);
		viatge.add(i);
		helicopters.get(Hj).add(viatge);
		helicopters.get(Hi).get(Vi).remove(G);
		if(helicopters.get(Hi).get(Vi).size() == 0)
			helicopters.get(Hi).remove(Vi);
		//Aqui cridem a "recalcularIntercambiaGrups" ja que el que tenim que fer es
		//sumar el temps dels viatges del grup "G" al centre del seu nou helicopters
		//dos vegades, el de anada i el de tornada
		recalcularIntercambiaGrups(Hj,  helicopters.get(Hj).size() - 1, 0, 1);
	}

	/*  =======================================================
	  
	                        Heurístics
	  
	    =======================================================
	*/
	
		
	
	
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
	
	/**Segons el valor de "X", restarà o sumarà el temps del viatge que hi ha en el viatge "V" del helicopter "H"
	 * entre el seu primer grup al centre, i el seu últim grup al centre, al que pertany el helicopter
	 * @pre H inRange(helicopters); V inRange(helicopters[H]); X == 1, si vol sumar; X == -1, si vol restar.*/
	private void recalcularIntercambiaViatges(int H, int V, int X) {
		int a1, a2, b1, b2;
		//nH es el nombre d'helicopters que hi ha en cada centre
		int nH = context.getCentros().get(0).getNHelicopteros();
		//El centre c és el centre al que pertany el helicopter H
		Centro c = context.getCentros().get(H/nH);
		a1 = a2 = c.getCoordX();
		b1 = b2 = c.getCoordY();
		Grupo g1 = helicopters.get(H).get(V).get(0);
		Grupo g2 = helicopters.get(H).get(V).get((helicopters.get(H).get(V).size() - 1));
		a1 = a1 - g1.getCoordX();
		b1 = b1 - g1.getCoordY();
		a2 = a2 - g2.getCoordX();
		b2 = b2 - g2.getCoordY();
		temps = (float) (temps + X*((INV_VEL_HEL)*Math.sqrt((a1*a1) + (b1*b1))
				+ (INV_VEL_HEL)*Math.sqrt((a2*a2) + (b2*b2))));
	}
	
	/**Restarà el temps dels viatges que hi ha en el grup "G" amb amb la seva següent localització
	 * (ja sigui un altre grup o un centre) i la seva anterior localització que es trobe en 
	 * el viatge "V" del helicopter "H". I sumarà el temps que hi ha en el nou viatge de la
	 * localització anterior al grup "G", amb la seva localització posterior, per tal de tornar a tancar el viatge "V"
	 * @pre H inRange(helicopters); V inRange(helicopters[H]); G inRange(helicopters[H][V]).*/
	private void recalcularTreuGrup(int H, int V, int G) {
		Grupo g = helicopters.get(H).get(V).get(G);
		int a1, a2, b1, b2, c1, c2;
		a1 = a2 = g.getCoordX();
		b1 = b2 = g.getCoordY();
		//nH es el nombre d'helicopters que hi ha en cada centre
		int nH = context.getCentros().get(0).getNHelicopteros();
		//El centre c és el centre al que pertany el helicopter H
		Centro c = context.getCentros().get(H/nH);
		if (G > 0) {
			Grupo aux = helicopters.get(H).get(V).get(G - 1);
			c1 = aux.getCoordX();
			c2 = aux.getCoordY();
			a1 = c1 - a1;
			b1 = c2 - b1;			
		}
		else {
			c1 = c.getCoordX();
			c2 = c.getCoordY();
			a1 = c1 - a1;
			b1 = c2 - b1;
		}
		if (G < helicopters.get(H).get(V).size() - 1) {
			Grupo aux = helicopters.get(H).get(V).get(G + 1);
			c1 = c1 - aux.getCoordX();
			c2 = c2 - aux.getCoordY();
			a2 = aux.getCoordX() - a2;
			b2 = aux.getCoordY() - b2;
		}
		else {
			c1 = c1 - c.getCoordX();
			c2 = c2 - c.getCoordY();
			a2 = c.getCoordX() - a2;
			b2 = c.getCoordY() - b2;
		}
		temps = (float) (temps - (INV_VEL_HEL)*Math.sqrt((a1*a1) + (b1*b1))
				- (INV_VEL_HEL)*Math.sqrt((a2*a2) + (b2*b2)) 
				+ (INV_VEL_HEL)*Math.sqrt((c1*c1) + (c2*c2)));
		
	}
	
	/**Sumarà el temps dels viatges que hi ha en el grup "G" amb amb la seva següent localització
	 * (ja sigui un altre grup o un centre) i la seva anterior localització que es troba en 
	 * el viatge "V" del helicopter "H". I restarà el temps que hi ha en el nou viatge de la
	 * localització anterior al grup "G", amb la seva localització posterior, per tal de esborrar
	 * aquest viatge que hi habia abans de situar en la nova posició el grup "G" en el viatge "V"
	 * @pre H inRange(helicopters); V inRange(helicopters[H]); G inRange(helicopters[H][V]).*/
	private void recalcularPosaGrup(int H, int V, int G) {
		Grupo g = helicopters.get(H).get(V).get(G);
		//nH es el nombre d'helicopters que hi ha en cada centre
		int nH = context.getCentros().get(0).getNHelicopteros();
		//El centre c és el centre al que pertany el helicopter H
		Centro c = context.getCentros().get(H/nH);
		int a1, a2, b1, b2, c1, c2;
		a1 = a2 = g.getCoordX();
		b1 = b2 = g.getCoordY();
		c1 = c.getCoordX();
		c2 = c.getCoordY();
		if (G > 0) {
			Grupo aux = helicopters.get(H).get(V).get(G - 1);
			c1 = c1 - aux.getCoordX();
			c2 = c2 - aux.getCoordY();
			a1 = aux.getCoordX() - a1;
			b1 = aux.getCoordY() - b1;
			
		}
		else {
			c1 = c1 - c.getCoordX();
			c2 = c2 - c.getCoordY();
			a1 = c.getCoordX() - a1;
			b1 = c.getCoordY() - b1;
		}
		if (G < helicopters.get(H).get(V).size() - 1) {
			Grupo aux = helicopters.get(H).get(V).get(G + 1);
			a2 = aux.getCoordX() - a2;
			b2 = aux.getCoordY() - b2;
		}
		else {
			a2 = c.getCoordX() - a2;
			b2 = c.getCoordY() - b2;
		}
		temps = (float) (temps + (INV_VEL_HEL)*Math.sqrt((a1*a1) + (b1*b1))
				+ (INV_VEL_HEL)*Math.sqrt((a2*a2) + (b2*b2)) 
				- (INV_VEL_HEL)*Math.sqrt((c1*c1) + (c2*c2)));
	}
	
	/**Segons el valor de "X", restarà o sumarà el temps del viatge que hi ha en el viatge "V" del helicopter "H"
	 * entre en el grup "G" amb amb la seva següent localització (ja sigui un altre grup o un centre) i la seva anterior
	 * @pre H inRange(helicopters); V inRange(helicopters[H]); G inRange(helicopters[H][V]);
	 * X == 1, si vol sumar; X == -1, si vol restar.*/
	private void recalcularIntercambiaGrups(int H, int V, int G, int X) {
		Grupo g = helicopters.get(H).get(V).get(G);
		int a1, a2, b1, b2;
		a1 = a2 = g.getCoordX();
		b1 = b2 = g.getCoordY();
		//nH es el nombre d'helicopters que hi ha en cada centre
		int nH = context.getCentros().get(0).getNHelicopteros();
		//El centre c és el centre al que pertany el helicopter H
		Centro c = context.getCentros().get(H/nH);
		if (G > 0) {
			Grupo aux = helicopters.get(H).get(V).get(G - 1);
			a1 = aux.getCoordX() - a1;
			b1 = aux.getCoordY() - b1;
		}
		else {
			a1 = c.getCoordX() - a1;
			b1 = c.getCoordY() - b1;
		}
		if (G < helicopters.get(H).get(V).size() - 1) {
			Grupo aux = helicopters.get(H).get(V).get(G + 1);
			a2 = aux.getCoordX() - a2;
			b2 = aux.getCoordY() - b2;
		}
		else {
			a2 = c.getCoordX() - a2;
			b2 = c.getCoordY() - b2;
		}
		temps = (float) (temps + X*((INV_VEL_HEL)*Math.sqrt((a1*a1) + (b1*b1))
				+ (INV_VEL_HEL)*Math.sqrt((a2*a2) + (b2*b2))));
	}
	
	/**Retorna el grup G-èssim de l'helicòpter H.
	 * @pre H inRange(helicopters); V inRange(helicopters[H])*/
	public ArrayList<Grupo> getViatge(int H, int V) {
		return helicopters.get(H).get(V);	
	}
	
	public double getTempsViatges() {
		return (double) temps;
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
	
	/**Retorna l'estructura interna de l'estat.*/
	public ArrayList<ArrayList<ArrayList<Grupo>>> getHelicopters() {
		return helicopters;
	}
	
	/**Indica si aquest estat pertany al problema plantejat pel segon cas de l'enunciat*/
	public boolean esSegonHeuristic() {
		return context.getEsSegonHeuristic();
	}
	
	/**Retorna el temps màxim per rescatar a tots els grups amb ferits
	 * @pre Aquest estat és copia d'un estat inicialitzat o s'ha inicialitzat amb una solució inicial*/
	public double tempsMaximFerits() {
		double tempsMax = 0;
		int nH = context.getCentros().get(0).getNHelicopteros();
		//Inv: tempsMax és, pels h vistos, el temps máxim de rescatar tots els grups amb ferits
		for(int h = 0; h < helicopters.size(); ++h) {
			double t_max = 0; //pels v vistos, temps de rescatar a tots els grups amb ferits d'h
			double t_acc = 0; //pels v vistos, temps que triguen a realitzar-se 
			for(ArrayList<Grupo> v :  helicopters.get(h)) {
				double t_viatge = 0; //temps d'aquest viatge
				boolean prio1 = false; //té algun grup de prioritat 1?
				
				for(int i = 0; i < v.size() - 1; ++i) {
					Grupo g = v.get(i);
					t_viatge = t_viatge + g.getPrioridad()*g.getNPersonas();
					Grupo aux = v.get(i + 1);
					int x = g.getCoordX();
					int y = g.getCoordY();
					int a = aux.getCoordX() - x;
					int b = aux.getCoordY() - y;
					t_viatge = (float) (t_viatge + (INV_VEL_HEL)*Math.sqrt((a*a) + (b*b)));
					
					if(g.getPrioridad() == 1) prio1 = true;
					
				}
				Centro c = context.getCentros().get(h/nH);
				int x = c.getCoordX();
				int y = c.getCoordY();
				int a1 = v.get(0).getCoordX() - x;
				int b1 = v.get(0).getCoordY() - y;
				int a2 = v.get(v.size() - 1).getCoordX() - x;
				int b2 = v.get(v.size() - 1).getCoordY() - y;
				t_viatge = (double) (t_viatge + (INV_VEL_HEL)*Math.sqrt((a1*a1) + (b1*b1))
				+ (INV_VEL_HEL)*Math.sqrt((a2*a2) + (b2*b2)) + v.get(v.size() - 1).getPrioridad()*v.get(v.size() - 1).getNPersonas()+10);
				
				t_acc += t_viatge;
				if(prio1) t_max = t_acc;
			}
			if(t_max > 0) tempsMax = (tempsMax>t_max)?tempsMax:t_max;
		}
		return tempsMax;
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
					if(v.get(j).getPrioridad() == 1) ret+="*";
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
