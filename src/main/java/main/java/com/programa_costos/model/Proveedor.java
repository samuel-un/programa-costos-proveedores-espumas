package main.java.com.programa_costos.model;

public class Proveedor {
	private String nombre;
	private double precioUnitario; // Por ejemplo, precio por cm3 o m3

	// Constructor
	public Proveedor(String nombre, double precioUnitario) {
		this.nombre = nombre;
		this.precioUnitario = precioUnitario;
	}

	// Getters y Setters
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getPrecioUnitario() {
		return precioUnitario;
	}

	public void setPrecioUnitario(double precioUnitario) {
		this.precioUnitario = precioUnitario;
	}

	// toString() para facilitar la impresión de la información del proveedor
	@Override
	public String toString() {
		return "Proveedor{" + "nombre='" + nombre + '\'' + ", precioUnitario=" + precioUnitario + '}';
	}
}
