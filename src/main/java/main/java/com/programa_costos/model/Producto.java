package main.java.com.programa_costos.model;

public class Producto {
	private double alto;
	private double ancho;
	private double largo;
	private String sistema; // "ciego", "pasante" o "mecanizado"
	private String tipoEspuma; // "ecs", "poliuretano", etc.

	// Constructor
	public Producto(double alto, double ancho, double largo, String sistema, String tipoEspuma) {
		this.alto = alto;
		this.ancho = ancho;
		this.largo = largo;
		this.sistema = sistema;
		this.tipoEspuma = tipoEspuma;
	}

	// Getters y Setters
	public double getAlto() {
		return alto;
	}

	public void setAlto(double alto) {
		this.alto = alto;
	}

	public double getAncho() {
		return ancho;
	}

	public void setAncho(double ancho) {
		this.ancho = ancho;
	}

	public double getLargo() {
		return largo;
	}

	public void setLargo(double largo) {
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
	public double getVolumen() {
		return alto * ancho * largo;
	}

	// toString() para facilitar la representación en String del objeto Producto
	@Override
	public String toString() {
		return "Producto{" + "alto=" + alto + ", ancho=" + ancho + ", largo=" + largo + ", sistema='" + sistema + '\''
				+ ", tipoEspuma='" + tipoEspuma + '\'' + ", volumen=" + getVolumen() + '}';
	}
}
