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
					"No se proporcionó ruta de Excel, usando la de por defecto (src/main/resources): " + excelPath
							+ "\n");
		}

		// Producto con medidas
		Producto producto = new Producto(8.4f, 6.4f, 0.8f, "FOAM PACK", "ciego");

		// Servicio de proveedores
		ProveedorService proveedorService = new ProveedorService(excelPath);

		// Imprimir todos los proveedores para depuración
		imprimirProveedoresDisponibles(proveedorService);

		// 1. Primero intentamos buscar proveedores que coincidan exactamente en tipo y
		// medidas
		System.out.println("Buscando proveedores para tipo de espuma: '" + producto.getTipoEspuma() +
				"' con medidas exactas: " + producto.getLargo() + " x " + producto.getAncho() + " x "
				+ producto.getGrueso());

		List<Proveedor> proveedoresExactos = proveedorService.getProveedoresPorTipoEspumaYMedidas(
				producto.getTipoEspuma(), producto);

		if (!proveedoresExactos.isEmpty()) {
			System.out.println("¡Se encontraron proveedores que coinciden exactamente!");
			mostrarProveedores(proveedoresExactos, producto);
			mostrarMejorProveedor(proveedoresExactos, producto);
			return;
		}

		// 2. Si no hay coincidencias exactas, buscamos por tipo de espuma con
		// tolerancia
		System.out.println(
				"\nNo se encontraron coincidencias exactas. Buscando proveedores por tipo de espuma con tolerancia de ±10%.");
		List<Proveedor> proveedoresConTolerancia = proveedorService
				.getProveedoresPorTipoEspumaConTolerancia(producto.getTipoEspuma(), producto);

		if (!proveedoresConTolerancia.isEmpty()) {
			System.out.println("Proveedores encontrados con tolerancia de medidas:");
			mostrarProveedores(proveedoresConTolerancia, producto);
			mostrarMejorProveedor(proveedoresConTolerancia, producto);
			return;
		}

		// 3. Si no hay con tolerancia, buscamos solo por tipo ignorando medidas
		System.out.println(
				"\nNo se encontraron proveedores con tolerancia. Buscando proveedores solo por tipo de espuma (ignorando medidas).");
		List<Proveedor> proveedoresFiltrados = proveedorService.getProveedoresPorTipoEspuma(producto.getTipoEspuma());

		if (!proveedoresFiltrados.isEmpty()) {
			System.out
					.println("Se encontraron proveedores con el tipo de espuma correcto pero con diferentes medidas:");
			mostrarProveedores(proveedoresFiltrados, producto);
			mostrarMejorProveedor(proveedoresFiltrados, producto);
			return;
		} else {
			System.out.println(
					"No se encontraron proveedores para el tipo de espuma '" + producto.getTipoEspuma() + "'.");
		}
	}

	// Método para mostrar los proveedores y sus costos
	private static void mostrarProveedores(List<Proveedor> proveedores, Producto producto) {
		for (Proveedor proveedor : proveedores) {
			float costo = CostCalculator.calcularCostoTotal(producto, proveedor);
			System.out.printf(" - %s (Tipo: '%s', Medidas: %.2f x %.2f x %.2f, PU: %.6f): Costo total = %.6f%n",
					proveedor.getNombre(), proveedor.getTipoEspuma(),
					proveedor.getLargo(), proveedor.getAncho(), proveedor.getGrueso(),
					proveedor.getPrecioUnitario(), costo);
		}
	}

	// Método para mostrar el mejor proveedor
	private static void mostrarMejorProveedor(List<Proveedor> proveedores, Producto producto) {
		Proveedor mejor = CostCalculator.encontrarProveedorMasEconomico(proveedores);
		System.out.println("\nProveedor más económico:");
		System.out.printf(" - %s (PU: %.6f): Costo total = %.6f%n",
				mejor.getNombre(), mejor.getPrecioUnitario(),
				CostCalculator.calcularCostoTotal(producto, mejor));
	}

	private static void imprimirProveedoresDisponibles(ProveedorService service) {
		System.out.println("\nProveedores disponibles en el Excel:");
		for (Proveedor p : service.getProveedores()) {
			System.out.printf(" - %s (Tipo: '%s', Medidas: %.2f x %.2f x %.2f)%n",
					p.getNombre(), p.getTipoEspuma(), p.getLargo(), p.getAncho(), p.getGrueso());
		}
		System.out.println("");
	}
}