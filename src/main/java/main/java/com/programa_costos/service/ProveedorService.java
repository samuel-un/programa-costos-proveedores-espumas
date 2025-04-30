package main.java.com.programa_costos.service;

import main.java.com.programa_costos.model.Proveedor;
import main.java.com.programa_costos.io.ExcelReader;
import main.java.com.programa_costos.model.Producto;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar y filtrar proveedores.
 */
public class ProveedorService {

	private final List<Proveedor> proveedores;

	/**
	 * Inicializa el servicio leyendo los proveedores desde el Excel.
	 * 
	 * @param resourcePath Ruta del archivo Excel.
	 */
	public ProveedorService(String resourcePath) {
		this.proveedores = ExcelReader.leerProveedoresDesdeExcel(resourcePath);
	}

	/**
	 * Devuelve la lista de proveedores cargados.
	 * 
	 * @return Lista de proveedores.
	 */
	public List<Proveedor> getProveedores() {
		return proveedores;
	}

	/**
	 * Normaliza el tipo de espuma (minúsculas, sin espacios extra).
	 */
	private String normalizarTipoEspuma(String tipo) {
		if (tipo == null)
			return "";
		return tipo.trim().toLowerCase().replaceAll("\\s+", " ");
	}

	/**
	 * Filtra proveedores por tipo de espuma exacto.
	 */
	public List<Proveedor> getProveedoresPorTipoEspuma(String tipoEspuma) {
		final String tipoNormalizado = normalizarTipoEspuma(tipoEspuma);
		return proveedores.stream()
				.filter(p -> normalizarTipoEspuma(p.getTipoEspuma()).equals(tipoNormalizado))
				.collect(Collectors.toList());
	}

	/**
	 * Filtra proveedores por tipo de espuma y medidas exactas (con tolerancia
	 * mínima para float).
	 */
	public List<Proveedor> getProveedoresPorTipoEspumaYMedidas(String tipoEspuma, Producto producto) {
		final String tipoNormalizado = normalizarTipoEspuma(tipoEspuma);
		return proveedores.stream()
				.filter(p -> normalizarTipoEspuma(p.getTipoEspuma()).equals(tipoNormalizado)
						&& sonIguales(p.getLargo(), producto.getLargo())
						&& sonIguales(p.getAncho(), producto.getAncho())
						&& sonIguales(p.getGrueso(), producto.getGrueso()))
				.collect(Collectors.toList());
	}

	/**
	 * Filtra proveedores por tipo de espuma y medidas dentro de una tolerancia del
	 * 10%.
	 */
	public List<Proveedor> getProveedoresPorTipoEspumaConTolerancia(String tipoEspuma, Producto producto) {
		final String tipoNormalizado = normalizarTipoEspuma(tipoEspuma);
		return proveedores.stream()
				.filter(p -> normalizarTipoEspuma(p.getTipoEspuma()).equals(tipoNormalizado)
						&& estaDentroDeTolerancia(p, producto))
				.collect(Collectors.toList());
	}

	/**
	 * Filtra proveedores de cualquier tipo de espuma con medidas dentro de
	 * tolerancia.
	 */
	public List<Proveedor> getProveedoresCualquierTipoConTolerancia(Producto producto) {
		return proveedores.stream()
				.filter(p -> estaDentroDeTolerancia(p, producto))
				.collect(Collectors.toList());
	}

	/**
	 * Verifica si las medidas del proveedor están dentro de la tolerancia del
	 * producto.
	 */
	private boolean estaDentroDeTolerancia(Proveedor proveedor, Producto producto) {
		float tolerancia = 0.10f;
		return estaDentroDelRango(proveedor.getLargo(), producto.getLargo(), tolerancia)
				&& estaDentroDelRango(proveedor.getAncho(), producto.getAncho(), tolerancia)
				&& estaDentroDelRango(proveedor.getGrueso(), producto.getGrueso(), tolerancia);
	}

	/**
	 * Verifica si una medida está dentro del rango de tolerancia.
	 */
	private boolean estaDentroDelRango(float medidaProveedor, float medidaProducto, float tolerancia) {
		float rangoInferior = medidaProducto - (medidaProducto * tolerancia);
		float rangoSuperior = medidaProducto + (medidaProducto * tolerancia);
		return medidaProveedor >= rangoInferior && medidaProveedor <= rangoSuperior;
	}

	/**
	 * Compara dos floats con una tolerancia muy pequeña (epsilon) para evitar
	 * errores de precisión.
	 */
	private boolean sonIguales(float a, float b) {
		float epsilon = 0.01f; // Puedes ajustar el epsilon si lo necesitas
		return Math.abs(a - b) < epsilon;
	}
}
