package main.java.com.programa_costos.main;

import main.java.com.programa_costos.model.Producto;
import main.java.com.programa_costos.model.Proveedor;
import main.java.com.programa_costos.service.CostCalculator;
import main.java.com.programa_costos.service.ProveedorService;

import java.util.List;

public class MainApp {
	public static void main(String[] args) {
		String excelPath = (args.length > 0) ? args[0] : "Espumas_Ciegas.xlsx";

		if (args.length == 0) {
			System.out.println(
					"No se proporcionó ruta de Excel, usando la de por defecto (src/main/resources): " + excelPath + "\n");
		}

		Producto producto = new Producto(10.0, 10.0, 10.0, "ciego", "polietileno");

		ProveedorService proveedorService = new ProveedorService(excelPath);

		// FILTRAR proveedores por tipo de espuma
		List<Proveedor> proveedoresFiltrados = proveedorService.getProveedoresPorTipoEspuma(producto.getTipoEspuma());

		if (proveedoresFiltrados.isEmpty()) {
			System.out.println("No hay proveedores que trabajen con el tipo de espuma: " + producto.getTipoEspuma());
			return;
		}

		System.out.println("Costos para el producto: " + producto);
		for (Proveedor proveedor : proveedoresFiltrados) {
			double costo = CostCalculator.calcularCostoTotal(producto, proveedor);
			System.out.printf(" - %s (Precio unitario: %.2f): Costo total del volumen = %.2f%n",
					proveedor.getNombre(), proveedor.getPrecioUnitario(), costo);
		}

		Proveedor mejor = CostCalculator.encontrarProveedorMasEconomico(producto, proveedoresFiltrados);
		System.out.println("\nProveedor más económico:");
		System.out.printf(" - %s (Precio unitario: %.2f): Costo total del volumen = %.2f%n",
				mejor.getNombre(), mejor.getPrecioUnitario(), CostCalculator.calcularCostoTotal(producto, mejor));
	}
}
