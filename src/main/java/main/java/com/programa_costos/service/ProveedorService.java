package main.java.com.programa_costos.service;

import main.java.com.programa_costos.model.Proveedor;
import main.java.com.programa_costos.io.ExcelReader;
import main.java.com.programa_costos.model.Producto;

import java.util.List;
import java.util.stream.Collectors;

public class ProveedorService {
	private List<Proveedor> proveedores;

	public ProveedorService(String resourcePath) {
		this.proveedores = ExcelReader.leerProveedoresDesdeExcel(resourcePath);
	}

	public List<Proveedor> getProveedores() {
		return proveedores;
	}

	// Método para normalizar el tipo de espuma
	private String normalizarTipoEspuma(String tipo) {
		if (tipo == null)
			return "";
		return tipo.trim().toLowerCase().replaceAll("\\s+", " ");
	}

	// Método que sólo filtra por tipo de espuma
	public List<Proveedor> getProveedoresPorTipoEspuma(String tipoEspuma) {
		final String tipoNormalizado = normalizarTipoEspuma(tipoEspuma);
		return proveedores.stream()
				.filter(p -> normalizarTipoEspuma(p.getTipoEspuma()).equals(tipoNormalizado))
				.collect(Collectors.toList());
	}

	// Nuevo método que filtra por tipo de espuma Y medidas exactas
	public List<Proveedor> getProveedoresPorTipoEspumaYMedidas(String tipoEspuma, Producto producto) {
		final String tipoNormalizado = normalizarTipoEspuma(tipoEspuma);
		return proveedores.stream()
				.filter(p -> normalizarTipoEspuma(p.getTipoEspuma()).equals(tipoNormalizado) &&
						p.getLargo() == producto.getLargo() &&
						p.getAncho() == producto.getAncho() &&
						p.getGrueso() == producto.getGrueso())
				.collect(Collectors.toList());
	}

	// Método para buscar por tipo de espuma con tolerancia en medidas
	public List<Proveedor> getProveedoresPorTipoEspumaConTolerancia(String tipoEspuma, Producto producto) {
		final String tipoNormalizado = normalizarTipoEspuma(tipoEspuma);
		return proveedores.stream()
				.filter(p -> normalizarTipoEspuma(p.getTipoEspuma()).equals(tipoNormalizado)
						&& estaDentroDeTolerancia(p, producto))
				.collect(Collectors.toList());
	}

	// Nuevo método para buscar cualquier tipo de espuma pero con medidas en rango
	// de tolerancia
	public List<Proveedor> getProveedoresCualquierTipoConTolerancia(Producto producto) {
		return proveedores.stream()
				.filter(p -> estaDentroDeTolerancia(p, producto))
				.collect(Collectors.toList());
	}

	private boolean estaDentroDeTolerancia(Proveedor proveedor, Producto producto) {
		float tolerancia = 0.10f;
		return estaDentroDelRango(proveedor.getLargo(), producto.getLargo(), tolerancia) &&
				estaDentroDelRango(proveedor.getAncho(), producto.getAncho(), tolerancia) &&
				estaDentroDelRango(proveedor.getGrueso(), producto.getGrueso(), tolerancia);
	}

	private boolean estaDentroDelRango(float medidaProveedor, float medidaProducto, float tolerancia) {
		float rangoInferior = medidaProducto - (medidaProducto * tolerancia);
		float rangoSuperior = medidaProducto + (medidaProducto * tolerancia);
		return medidaProveedor >= rangoInferior && medidaProveedor <= rangoSuperior;
	}
}