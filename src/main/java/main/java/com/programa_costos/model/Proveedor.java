package main.java.com.programa_costos.model;

/**
 * Representa un proveedor de espuma con sus características y medidas.
 */
public class Proveedor {
	private String nombre;
	private float precioUnitario;
	private String tipoEspuma;
	private float largo;
	private float ancho;
	private float grueso;

	/**
	 * Constructor completo.
	 */
	public Proveedor(String nombre, float precioUnitario, String tipoEspuma, float largo, float ancho, float grueso) {
		this.nombre = nombre;
		this.precioUnitario = precioUnitario;
		this.tipoEspuma = tipoEspuma;
		this.largo = largo;
		this.ancho = ancho;
		this.grueso = grueso;
	}

	// Getters
	public String getNombre() {
		return nombre;
	}

	public float getPrecioUnitario() {
		return precioUnitario;
	}

	public String getTipoEspuma() {
		return tipoEspuma;
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

	// Setters (si fueran necesarios)
	public void setLargo(float largo) {
		this.largo = largo;
	}

	public void setAncho(float ancho) {
		this.ancho = ancho;
	}

	public void setGrueso(float grueso) {
		this.grueso = grueso;
	}
}
