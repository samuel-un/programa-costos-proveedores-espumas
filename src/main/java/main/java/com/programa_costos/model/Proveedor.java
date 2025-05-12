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
	private String unidades; // NUEVO

	/**
	 * Constructor completo.
	 */
	public Proveedor(String nombre, float precioUnitario, String tipoEspuma, float largo, float ancho, float grueso,
			String unidades) {
		this.nombre = nombre;
		this.precioUnitario = precioUnitario;
		this.tipoEspuma = tipoEspuma;
		this.largo = largo;
		this.ancho = ancho;
		this.grueso = grueso;
		this.unidades = unidades; // NUEVO
	}

	// Constructor antiguo para compatibilidad
	public Proveedor(String nombre, float precioUnitario, String tipoEspuma, float largo, float ancho, float grueso) {
		this(nombre, precioUnitario, tipoEspuma, largo, ancho, grueso, "");
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

	public String getUnidades() {
		return unidades;
	} // NUEVO

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

	public void setUnidades(String unidades) {
		this.unidades = unidades;
	} // NUEVO
}
