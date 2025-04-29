package main.java.com.programa_costos.model;

public class Producto {
	private float largo;
	private float ancho;
	private float grueso;
	private String tipoEspuma;
	private String denominacion;

	// Constructor
	public Producto(float largo, float ancho, float grueso, String tipoEspuma, String denominacion) {
		this.largo = largo;
		this.ancho = ancho;
		this.grueso = grueso;
		this.tipoEspuma = tipoEspuma;
		this.denominacion = denominacion;
	}

	// Métodos getters
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

	// Método para calcular el volumen del producto
	public float getVolumen() {
		// Volumen = largo * ancho * grueso
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