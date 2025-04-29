package main.java.com.programa_costos.service;

import main.java.com.programa_costos.model.Producto;
import main.java.com.programa_costos.model.Proveedor;

import java.util.List;

public class CostCalculator {

	public static float calcularCostoTotal(Producto producto, Proveedor proveedor) {
		float volumen = producto.getVolumen(); // En cm3
		return volumen * proveedor.getPrecioUnitario();
	}

	public static Proveedor encontrarProveedorMasEconomico(Producto producto, List<Proveedor> proveedores) {
		return encontrarProveedorMasEconomico(proveedores);
	}

	// Método sobrecargado sin necesidad del producto
	public static Proveedor encontrarProveedorMasEconomico(List<Proveedor> proveedores) {
		if (proveedores == null || proveedores.isEmpty()) {
			throw new IllegalArgumentException("La lista de proveedores no puede estar vacía.");
		}

		Proveedor mejorProveedor = proveedores.get(0);
		float menorPrecio = mejorProveedor.getPrecioUnitario();

		for (int i = 1; i < proveedores.size(); i++) {
			Proveedor proveedor = proveedores.get(i);
			float precio = proveedor.getPrecioUnitario();

			if (precio < menorPrecio) {
				menorPrecio = precio;
				mejorProveedor = proveedor;
			}
		}

		return mejorProveedor;
	}
}