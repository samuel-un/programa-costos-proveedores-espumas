package main.java.com.programa_costos.main;

import main.java.com.programa_costos.model.Producto;
import main.java.com.programa_costos.model.Proveedor;
import main.java.com.programa_costos.service.CostCalculator;
import main.java.com.programa_costos.service.ProveedorService;

import java.util.List;

public class MainApp {
	public static void main(String[] args) {
		// Producto de prueba
		Producto producto = new Producto(10.0, 20.0, 30.0, "ciego", "ecs");

		// Cargar proveedores
		ProveedorService proveedorService = new ProveedorService();
		List<Proveedor> proveedores = proveedorService.getProveedores();

		// Mostrar costos
		System.out.println("Costos para el producto: " + producto);
		for (Proveedor proveedor : proveedores) {
			double costo = CostCalculator.calcularCostoTotal(producto, proveedor);
			System.out.printf(" - %s: %.2f%n", proveedor.getNombre(), costo);
		}

		// Mostrar proveedor más barato
		Proveedor mejor = CostCalculator.encontrarProveedorMasEconomico(producto, proveedores);
		System.out.println("Proveedor más económico: " + mejor.getNombre() + " (costo = "
				+ String.format("%.2f", CostCalculator.calcularCostoTotal(producto, mejor)) + ")");
	}
}
