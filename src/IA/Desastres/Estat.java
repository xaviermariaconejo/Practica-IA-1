package IA.Desastres;

import java.util.ArrayList;

public class Estat {
	
	private ContextEstat context;
	
	/**Guarda, per cada helicòpter, la llista de grups que ha de rescatar*/
	private ArrayList<Grupo>[] helicopters;
	
	/**Guarda, per cada grup, l'índex de l'helicòpter que el rescata*/
	private int[] grups;

	/**Constructora
	 * @param context El context del problema per a aquest estat.*/
	public Estat(ContextEstat context) {
		this.context = context;
		// ...
	}
	
}
