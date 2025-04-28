package main.java.com.programa_costos.service;

import main.java.com.programa_costos.model.Proveedor;
import main.java.com.programa_costos.io.ExcelReader;

import java.util.List;
import java.util.stream.Collectors;

public class ProveedorService {
	private List<Proveedor> proveedores;
	private String resourcePath;

	public ProveedorService(String resourcePath) {
		this.resourcePath = resourcePath;
		this.proveedores = ExcelReader.leerProveedoresDesdeExcel(resourcePath);
	}

	public List<Proveedor> getProveedores() {
		return proveedores;
	}

	// Nuevo método para filtrar por tipoEspuma
	public List<Proveedor> getProveedoresPorTipoEspuma(String tipoEspuma) {
		return proveedores.stream()
				.filter(p -> p.getTipoEspuma().equalsIgnoreCase(tipoEspuma))
				.collect(Collectors.toList());
	}

	public Proveedor buscarProveedorPorNombre(String nombre) {
		return proveedores.stream()
				.filter(p -> p.getNombre().equalsIgnoreCase(nombre))
				.findFirst()
				.orElse(null);
	}

	public void setProveedores(List<Proveedor> proveedores) {
		this.proveedores = proveedores;
	}

	public void agregarProveedor(Proveedor proveedor) {
		proveedores.add(proveedor);
	}
}
