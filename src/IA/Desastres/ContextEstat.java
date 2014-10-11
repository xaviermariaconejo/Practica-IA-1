package IA.Desastres;

/**Representa la part estàtica de l'estat del problema, són les dades que no cambien entre solucions.
 * @author Josep Sánchez Ferreres*/
public class ContextEstat {

	/**Dimensions de la zona de rescat (és quadrada)*/
	public static final float dimensions = 50;
	
	/**Vector de grups que s'han de rescatar*/
	private Grupos grups;
	
	/**Vector de centres de rescat amb helicòpters*/
	private Centros centres;
	
	/**Indicador si realitzem el segon heuristic*/
	private boolean esSegonHeuristic;
	
	public ContextEstat(Grupos grups, Centros centres, boolean esSegonHeuristic) {
		this.grups = grups;
		this.centres = centres;
		this.esSegonHeuristic = esSegonHeuristic;
	}
	
	public Grupos getGrups() {return grups;}

	public Centros getCentros() {return centres;}
	
	public boolean getEsSegonHeuristic() {return esSegonHeuristic;}
}
