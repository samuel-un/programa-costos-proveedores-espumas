package main.java.com.programa_costos.io;

import main.java.com.programa_costos.model.Proveedor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

	/**
	 * Lee una lista de proveedores desde un recurso Excel en el classpath. Se busca
	 * la fila de encabezado que contenga "Nombre" (o "Proveedor") en la primera
	 * celda, y se leen todas las filas posteriores interpretando la columna 0 como
	 * nombre y 1 como precio.
	 *
	 * @param resourcePath Ruta del recurso Excel en el classpath (por ejemplo,
	 *                     "Espumas_Ciegas.xlsx").
	 * @return Lista de proveedores.
	 * @throws RuntimeException si no se encuentra el archivo o la fila de
	 *                          encabezado.
	 */
	public static List<Proveedor> leerProveedoresDesdeExcel(String resourcePath) {
		List<Proveedor> proveedores = new ArrayList<>();

		try (InputStream is = ExcelReader.class.getClassLoader().getResourceAsStream(resourcePath);
				Workbook workbook = new XSSFWorkbook(is)) {

			if (is == null) {
				throw new IllegalArgumentException("No se encontró el recurso Excel: " + resourcePath);
			}

			Sheet sheet = workbook.getSheetAt(0);
			int headerRowIdx = -1;

			// Buscar fila de encabezado
			for (Row row : sheet) {
				Cell primera = row.getCell(0);
				if (primera != null && primera.getCellType() == CellType.STRING) {
					String texto = primera.getStringCellValue().trim();
					if (texto.equalsIgnoreCase("nombre") || texto.equalsIgnoreCase("proveedor")) {
						headerRowIdx = row.getRowNum();
						break;
					}
				}
			}

			if (headerRowIdx < 0) {
				throw new IllegalStateException("No se encontró la fila de encabezado con 'Nombre' o 'Proveedor'.");
			}

			// Leer filas posteriores a encabezado
			for (int i = headerRowIdx + 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;

				Cell celdaNombre = row.getCell(0);
				Cell celdaPrecio = row.getCell(1);
				if (celdaNombre == null || celdaPrecio == null)
					continue;

				// Obtener nombre
				String nombre;
				if (celdaNombre.getCellType() == CellType.STRING) {
					nombre = celdaNombre.getStringCellValue().trim();
				} else {
					nombre = celdaNombre.toString().trim();
				}

				// Obtener precio unitario
				double precioUnitario;
				if (celdaPrecio.getCellType() == CellType.NUMERIC) {
					precioUnitario = celdaPrecio.getNumericCellValue();
				} else {
					try {
						precioUnitario = Double.parseDouble(celdaPrecio.toString().trim());
					} catch (NumberFormatException e) {
						// Saltar filas con valores no numéricos
						continue;
					}
				}

				proveedores.add(new Proveedor(nombre, precioUnitario));
			}

		} catch (IOException e) {
			throw new RuntimeException("Error leyendo el archivo Excel: " + e.getMessage(), e);
		}

		return proveedores;
	}
}
