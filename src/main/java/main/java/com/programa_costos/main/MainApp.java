// Archivo: src/main/java/com/programa_costos/main/MainApp.java
package main.java.com.programa_costos.main;

import main.java.com.programa_costos.model.Producto;
import main.java.com.programa_costos.model.Proveedor;
import main.java.com.programa_costos.service.CostCalculator;
import main.java.com.programa_costos.service.ProveedorService;

import java.util.List;

public class MainApp {
	public static void main(String[] args) {
		// Obtener ruta del Excel desde argumentos o usar valor por defecto
		String excelPath = (args.length > 0) ? args[0] : "Espumas_Ciegas.xlsx";

		// Mensaje de uso si no se proporciona ruta explícita
		if (args.length == 0) {
			System.out.println("No se proporcionó ruta de Excel, usando la de por defecto (src/main/resources): " + excelPath);
		}

		// Producto de prueba (podrías parametrizarlo también en el futuro)
		Producto producto = new Producto(10.0, 20.0, 30.0, "ciego", "ecs");

		// Cargar proveedores dinámicamente
		ProveedorService proveedorService = new ProveedorService(excelPath);
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