package main.java.com.programa_costos.io;

import main.java.com.programa_costos.model.Proveedor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class ExcelReader {
	private static final Logger logger = Logger.getLogger(ExcelReader.class.getName());

	/**
	 * Lee proveedores desde un recurso Excel de forma dinámica.
	 * Normaliza nombres (trim, case-insensitive, colapsa espacios),
	 * filtra vacíos y conserva el precio unitario más bajo por proveedor.
	 *
	 * @param resourcePath Ruta del recurso Excel en el classpath.
	 * @return Lista de proveedores únicos con su precio mínimo.
	 */
	public static List<Proveedor> leerProveedoresDesdeExcel(String resourcePath) {
		Map<String, Proveedor> proveedorMap = new LinkedHashMap<>();

		// Intentar primero como archivo local, luego como recurso
		try {
			File file = new File(resourcePath);
			InputStream is;

			if (file.exists() && file.canRead()) {
				// Si el archivo existe como archivo local
				logger.info("Leyendo Excel como archivo local: " + file.getAbsolutePath());
				is = new FileInputStream(file);
			} else {
				// Intentar como recurso en classpath
				logger.info("Intentando leer Excel como recurso: " + resourcePath);
				is = ExcelReader.class.getClassLoader().getResourceAsStream(resourcePath);

				if (is == null) {
					// Si no lo encuentra en el classpath raíz, buscar en resources
					String resourcesPath = "resources/" + resourcePath;
					logger.info("Intentando leer Excel desde resources/: " + resourcesPath);
					is = ExcelReader.class.getClassLoader().getResourceAsStream(resourcesPath);
				}

				if (is == null) {
					// Si aún no lo encuentra, intenta con ruta absoluta o relativa a src/main
					String absolutePath = "src/main/resources/" + resourcePath;
					File resourceFile = new File(absolutePath);
					if (resourceFile.exists() && resourceFile.canRead()) {
						logger.info("Leyendo Excel desde ruta absoluta: " + absolutePath);
						is = new FileInputStream(resourceFile);
					} else {
						throw new IllegalArgumentException("No se encontró el recurso Excel: " + resourcePath);
					}
				}
			}

			// Procesar el Excel
			try (Workbook workbook = new XSSFWorkbook(is)) {
				Sheet sheet = workbook.getSheetAt(0);
				int headerRowIdx = buscarFilaEncabezado(sheet);
				if (headerRowIdx < 0) {
					throw new IllegalStateException("No se encontró la fila de encabezado con 'Prov.' o 'Proveedor'.");
				}

				// Mapear nombre de columna a índice (normalizado)
				Row header = sheet.getRow(headerRowIdx);
				Map<String, Integer> idxMap = mapearEncabezados(header);

				// Validar que existan columnas necesarias
				validarColumnasNecesarias(idxMap);

				// Columnas de proveedor y medidas
				int colProv = idxMap.get("PROV.");
				int colPrecio = idxMap.get("IMPORTE UD.");
				int colTipoEspuma = idxMap.get("TIPO DE ESPUMA");
				int colLargo = idxMap.get("LARGO");
				int colAncho = idxMap.get("ANCHO");
				int colGrueso = idxMap.get("GRUESO");

				int totalFilas = sheet.getLastRowNum();
				int filasProcesadas = 0;
				int filasValidas = 0;
				int filasIgnoradas = 0;

				// Leer datos
				for (int i = headerRowIdx + 1; i <= totalFilas; i++) {
					Row row = sheet.getRow(i);
					filasProcesadas++;

					if (row == null) {
						filasIgnoradas++;
						continue;
					}

					// Intentar procesar aunque falten algunas celdas
					try {
						Cell cellProv = row.getCell(colProv);
						Cell cellPre = row.getCell(colPrecio);
						Cell cellTipo = row.getCell(colTipoEspuma);
						Cell cellLargo = row.getCell(colLargo);
						Cell cellAncho = row.getCell(colAncho);
						Cell cellGrueso = row.getCell(colGrueso);

						// Verificar que al menos tengamos proveedor y tipo
						if (cellProv == null || cellTipo == null) {
							filasIgnoradas++;
							continue;
						}

						String rawName = getCellValueAsString(cellProv);
						String tipoEspuma = normalizarTipoEspuma(getCellValueAsString(cellTipo));

						if (rawName.isEmpty() || tipoEspuma.isEmpty()) {
							filasIgnoradas++;
							continue;
						}

						// Normalizar clave
						String key = (rawName + "|" + tipoEspuma).toLowerCase().replaceAll("\\s+", " ");

						// Parsear precio (con opción de fallback)
						float precio = 0;
						if (cellPre != null) {
							try {
								precio = obtenerValorNumerico(cellPre);
							} catch (Exception e) {
								// Si no se puede parsear, usar 0 e ignorar esta fila para mejor provedor
								filasIgnoradas++;
								continue;
							}
						} else {
							filasIgnoradas++;
							continue; // Sin precio, ignorar
						}

						// Parsear las medidas (con fallbacks por si faltan)
						float largo = (cellLargo != null) ? obtenerValorCelda(cellLargo) : 0;
						float ancho = (cellAncho != null) ? obtenerValorCelda(cellAncho) : 0;
						float grueso = (cellGrueso != null) ? obtenerValorCelda(cellGrueso) : 0;

						// Si quieres guardar TODOS los proveedores sin consolidar por precio mínimo
						// (esto guardará filas duplicadas si hay múltiples entradas para el mismo
						// proveedor/tipo)
						String uniqueKey = key + "|" + largo + "|" + ancho + "|" + grueso + "|" + precio;
						proveedorMap.put(uniqueKey,
								new Proveedor(rawName, precio, getCellValueAsString(cellTipo), largo, ancho, grueso));
						filasValidas++;
					} catch (Exception e) {
						// Capturar cualquier excepción inesperada al procesar una fila y continuar
						logger.warning("Error procesando fila " + i + ": " + e.getMessage());
						filasIgnoradas++;
					}
				}

				logger.info(String.format(
						"Excel procesado: %d filas totales, %d procesadas, %d válidas, %d ignoradas, %d proveedores únicos",
						totalFilas, filasProcesadas, filasValidas, filasIgnoradas, proveedorMap.size()));
			}

			// Cerrar el input stream manualmente una vez terminado con el workbook
			is.close();

		} catch (IOException e) {
			throw new RuntimeException("Error leyendo el archivo Excel: " + e.getMessage(), e);
		}

		List<Proveedor> resultado = new ArrayList<>(proveedorMap.values());
		logger.info("Total de proveedores encontrados: " + resultado.size());
		return resultado;
	}

	// Busca la fila del encabezado
	private static int buscarFilaEncabezado(Sheet sheet) {
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (cell.getCellType() == CellType.STRING) {
					String txt = cell.getStringCellValue().trim();
					if (txt.equalsIgnoreCase("Prov.") || txt.equalsIgnoreCase("Proveedor")) {
						return row.getRowNum();
					}
				}
			}
		}
		return -1;
	}

	// Mapea los encabezados a sus índices de columna
	private static Map<String, Integer> mapearEncabezados(Row header) {
		Map<String, Integer> idxMap = new HashMap<>();
		for (Cell cell : header) {
			if (cell.getCellType() == CellType.STRING) {
				String normalized = cell.getStringCellValue().trim().replaceAll("\\s+", " ").toUpperCase();
				idxMap.put(normalized, cell.getColumnIndex());
			}
		}
		return idxMap;
	}

	// Valida que existan las columnas necesarias
	private static void validarColumnasNecesarias(Map<String, Integer> idxMap) {
		List<String> columnasFaltantes = new ArrayList<>();
		String[] columnasRequeridas = { "PROV.", "IMPORTE UD.", "TIPO DE ESPUMA", "LARGO", "ANCHO", "GRUESO" };

		for (String columna : columnasRequeridas) {
			if (!idxMap.containsKey(columna)) {
				columnasFaltantes.add(columna);
			}
		}

		if (!columnasFaltantes.isEmpty()) {
			throw new IllegalStateException(
					"Faltan columnas necesarias en el encabezado: " + String.join(", ", columnasFaltantes));
		}
	}

	// Obtiene el valor de una celda como String, manejando diferentes tipos
	private static String getCellValueAsString(Cell cell) {
		if (cell == null)
			return "";

		switch (cell.getCellType()) {
			case STRING:
				return cell.getStringCellValue().trim();
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					return cell.getDateCellValue().toString();
				} else {
					// Evitar notación científica y decimales innecesarios
					double val = cell.getNumericCellValue();
					if (val == (long) val) {
						return String.valueOf((long) val);
					} else {
						return String.valueOf(val);
					}
				}
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case FORMULA:
				try {
					// Intentar obtener el valor evaluado como string en lugar de usar
					// getCachedFormulaResultCell
					return cell.getStringCellValue();
				} catch (Exception e1) {
					try {
						// Si falla como string, intentar como numérico
						return String.valueOf(cell.getNumericCellValue());
					} catch (Exception e2) {
						// Como último recurso, devolver la fórmula
						return cell.getCellFormula();
					}
				}
			case BLANK:
				return "";
			default:
				return "";
		}
	}

	// Método para obtener valor numérico de una celda independiente del tipo
	private static float obtenerValorNumerico(Cell cell) {
		if (cell == null)
			return 0;

		switch (cell.getCellType()) {
			case NUMERIC:
				return (float) cell.getNumericCellValue();
			case STRING:
				String valor = cell.getStringCellValue().trim()
						.replace("€", "").replace(",", ".").trim();
				if (valor.isEmpty())
					return 0;
				return Float.parseFloat(valor);
			case FORMULA:
				try {
					// Intentar obtener directamente el valor numérico de la fórmula
					return (float) cell.getNumericCellValue();
				} catch (Exception e) {
					try {
						// Si falla, intentar convertir desde string
						String valorStr = cell.getStringCellValue().trim()
								.replace("€", "").replace(",", ".").trim();
						if (valorStr.isEmpty())
							return 0;
						return Float.parseFloat(valorStr);
					} catch (Exception e2) {
						return 0;
					}
				}
			default:
				return 0;
		}
	}

	// Método para normalizar el tipo de espuma
	private static String normalizarTipoEspuma(String tipo) {
		if (tipo == null)
			return "";
		return tipo.trim().toLowerCase().replaceAll("\\s+", " ");
	}

	// Método para obtener valor de las celdas (maneja posibles nulos y
	// conversiones)
	private static float obtenerValorCelda(Cell cell) {
		if (cell == null) {
			return 0;
		}
		try {
			switch (cell.getCellType()) {
				case NUMERIC:
					return (float) cell.getNumericCellValue();
				case STRING:
					String valorStr = cell.getStringCellValue().trim()
							.replace(",", ".").trim();
					if (valorStr.isEmpty())
						return 0;
					return Float.parseFloat(valorStr);
				case FORMULA:
					try {
						// Intentar obtener directamente como valor numérico
						return (float) cell.getNumericCellValue();
					} catch (Exception e) {
						try {
							// Intentar convertir desde string
							String valor = cell.getStringCellValue().trim()
									.replace(",", ".").trim();
							if (valor.isEmpty())
								return 0;
							return Float.parseFloat(valor);
						} catch (Exception e2) {
							return 0;
						}
					}
				default:
					return 0;
			}
		} catch (Exception e) {
			return 0; // En caso de cualquier error, devolver 0
		}
	}
}