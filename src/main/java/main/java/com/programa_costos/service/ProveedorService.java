// Archivo: src/main/java/com/programa_costos/service/ProveedorService.java
package main.java.com.programa_costos.service;

import main.java.com.programa_costos.model.Proveedor;
import main.java.com.programa_costos.io.ExcelReader;

import java.util.List;

public class ProveedorService {
	private List<Proveedor> proveedores;
	private String resourcePath;

	/**
	 * Inicializa el servicio cargando proveedores desde un recurso Excel. La ruta
	 * se pasa dinámicamente.
	 *
	 * @param resourcePath Ruta al archivo Excel (p.ej. "Espumas_Ciegas.xlsx").
	 */
	public ProveedorService(String resourcePath) {
		this.resourcePath = resourcePath;
		this.proveedores = ExcelReader.leerProveedoresDesdeExcel(resourcePath);
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
}