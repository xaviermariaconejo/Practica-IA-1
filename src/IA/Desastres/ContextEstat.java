package IA.Desastres;

public class ContextEstat {

	/**Dimensions de la zona de rescat (és quadrada)*/
	public static final float dimensions = 50;
	
	/**Vector de grups que s'han de rescatar*/
	private Grupos grups;
	
	/**Vector de centres de rescat amb helicòpters*/
	private Centros centres;
	
	private ContextEstat(Grupos grups, Centros centres) {
		this.grups = grups;
		this.centres = centres;
	}
}