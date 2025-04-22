package main.java.com.programa_costos.service;

import main.java.com.programa_costos.model.Proveedor;
import main.java.com.programa_costos.io.ExcelReader;

import java.util.List;

public class ProveedorService {

	private List<Proveedor> proveedores;

	/**
	 * Inicializa el servicio cargando proveedores desde un recurso Excel en el
	 * classpath. El archivo debe encontrarse en
	 * src/main/resources/Espumas_Ciegas.xlsx.
	 */
	public ProveedorService() {
		this.proveedores = ExcelReader.leerProveedoresDesdeExcel("Espumas_Ciegas.xlsx");
	}

	public List<Proveedor> getProveedores() {
		return proveedores;
	}

	public Proveedor buscarProveedorPorNombre(String nombre) {
		return proveedores.stream().filter(p -> p.getNombre().equalsIgnoreCase(nombre)).findFirst().orElse(null);
	}

	public void setProveedores(List<Proveedor> proveedores) {
		this.proveedores = proveedores;
	}

	public void agregarProveedor(Proveedor proveedor) {
		proveedores.add(proveedor);
	}

	/**
	 * Método de prueba: carga y muestra por consola los proveedores leídos.
	 */
	public static void main(String[] args) {
		ProveedorService service = new ProveedorService();
		System.out.println("Proveedores cargados desde Excel:");
		service.getProveedores().forEach(System.out::println);
	}
}
