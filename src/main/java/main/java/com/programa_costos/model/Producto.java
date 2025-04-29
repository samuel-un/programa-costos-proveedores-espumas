package main.java.com.programa_costos.model;

public class Producto {
	private float alto;
	private float ancho;
	private float largo;
	private String sistema; // "ciego", "pasante" o "mecanizado"
	private String tipoEspuma; // "ecs", "poliuretano", etc.

	// Constructor
	public Producto(float alto, float ancho, float largo, String sistema, String tipoEspuma) {
		this.alto = alto / 100;
		this.ancho = ancho / 100;
		this.largo = largo / 100;
		this.sistema = sistema;
		this.tipoEspuma = tipoEspuma;
	}

	// Getters y Setters
	public float getAlto() {
		return alto;
	}

	public void setAlto(float alto) {
		this.alto = alto;
	}

	public float getAncho() {
		return ancho;
	}

	public void setAncho(float ancho) {
		this.ancho = ancho;
	}

	public float getLargo() {
		return largo;
	}

	public void setLargo(float largo) {
		this.largo = largo;
	}

	public String getSistema() {
		return sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}

	public String getTipoEspuma() {
		return tipoEspuma;
	}

	public void setTipoEspuma(String tipoEspuma) {
		this.tipoEspuma = tipoEspuma;
	}

	// Método de conveniencia: retorna el volumen (por ejemplo, en cm3)
	public float getVolumen() {
		return alto * ancho * largo;
	}

	// toString() para facilitar la representación en String del objeto Producto
	@Override
	public String toString() {
		return "Producto{" + "alto=" + alto + ", ancho=" + ancho + ", largo=" + largo + ", sistema='" + sistema + '\''
				+ ", tipoEspuma='" + tipoEspuma + '\'' + ", volumen=" + getVolumen() + '}';
	}
}
