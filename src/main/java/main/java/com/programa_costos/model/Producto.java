package main.java.com.programa_costos.model;

/**
 * Representa un producto con sus dimensiones y tipo de espuma.
 */
public class Producto {
	private float largo;
	private float ancho;
	private float grueso;
	private String tipoEspuma;
	private String denominacion;

	public Producto(float largo, float ancho, float grueso, String tipoEspuma, String denominacion) {
		this.largo = largo;
		this.ancho = ancho;
		this.grueso = grueso;
		this.tipoEspuma = tipoEspuma;
		this.denominacion = denominacion;
	}

	public float getLargo() {
		return largo;
	}

	public float getAncho() {
		return ancho;
	}

	public float getGrueso() {
		return grueso;
	}

	public String getTipoEspuma() {
		return tipoEspuma;
	}

	public String getDenominacion() {
		return denominacion;
	}

	/**
	 * Calcula el volumen del producto.
	 */
	public float getVolumen() {
		return largo * ancho * grueso;
	}

	@Override
	public String toString() {
		return "Producto{" +
				"largo=" + largo +
				", ancho=" + ancho +
				", grueso=" + grueso +
				", tipoEspuma='" + tipoEspuma + '\'' +
				", denominacion='" + denominacion + '\'' +
				'}';
	}
}
