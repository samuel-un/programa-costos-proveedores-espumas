package main.java.com.programa_costos.service;

import main.java.com.programa_costos.model.Producto;
import main.java.com.programa_costos.model.Proveedor;

import java.util.List;

public class CostCalculator {

	/**
	 * Calcula el costo total de un producto para un proveedor específico.
	 *
	 * @param producto  El producto con dimensiones y tipo de espuma.
	 * @param proveedor El proveedor con su precio unitario.
	 * @return El costo total en base al volumen del producto.
	 */
	public static double calcularCostoTotal(Producto producto, Proveedor proveedor) {
		double volumen = producto.getVolumen(); // En cm3
		return volumen * proveedor.getPrecioUnitario();
	}

	/**
	 * Devuelve el proveedor que ofrece el menor costo para un producto dado.
	 *
	 * @param producto    El producto para el que se busca el mejor proveedor.
	 * @param proveedores Lista de proveedores a comparar.
	 * @return El proveedor más económico.
	 */
	public static Proveedor encontrarProveedorMasEconomico(Producto producto, List<Proveedor> proveedores) {
		if (proveedores == null || proveedores.isEmpty()) {
			throw new IllegalArgumentException("La lista de proveedores no puede estar vacía.");
		}

		Proveedor mejorProveedor = proveedores.get(0);
		double menorCosto = calcularCostoTotal(producto, mejorProveedor);

		for (int i = 1; i < proveedores.size(); i++) {
			Proveedor proveedor = proveedores.get(i);
			double costo = calcularCostoTotal(producto, proveedor);

			if (costo < menorCosto) {
				menorCosto = costo;
				mejorProveedor = proveedor;
			}
		}

		return mejorProveedor;
	}
}
