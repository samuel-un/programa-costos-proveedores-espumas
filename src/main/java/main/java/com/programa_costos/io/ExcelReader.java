package main.java.com.programa_costos.io;

import main.java.com.programa_costos.model.Proveedor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Utilidad para leer proveedores desde un archivo Excel.
 * Normaliza nombres, filtra vacíos y consolida el precio unitario más bajo por
 * proveedor/tipo/medidas.
 */
public class ExcelReader {
	private static final Logger logger = Logger.getLogger(ExcelReader.class.getName());

	/**
	 * Lee proveedores desde un recurso Excel.
	 * 
	 * @param resourcePath Ruta del recurso Excel.
	 * @return Lista de proveedores únicos con su precio mínimo.
	 * @throws IllegalArgumentException si el archivo no se encuentra o faltan
	 *                                  columnas requeridas.
	 */
	public static List<Proveedor> leerProveedoresDesdeExcel(String resourcePath) {
		Map<String, Proveedor> proveedorMap = new LinkedHashMap<>();
		try {
			InputStream is = obtenerInputStream(resourcePath);
			if (is == null)
				throw new IllegalArgumentException("No se encontró el recurso Excel: " + resourcePath);

			try (Workbook workbook = new XSSFWorkbook(is)) {
				Sheet sheet = workbook.getSheetAt(0);
				int headerRowIdx = buscarFilaEncabezado(sheet);
				if (headerRowIdx < 0)
					throw new IllegalStateException("No se encontró la fila de encabezado con 'Prov.' o 'Proveedor'.");

				Row header = sheet.getRow(headerRowIdx);
				Map<String, Integer> idxMap = mapearEncabezados(header);
				validarColumnasNecesarias(idxMap);

				int colProv = idxMap.get("PROV.");
				int colPrecio = idxMap.get("IMPORTE UD.");
				int colTipoEspuma = idxMap.get("TIPO DE ESPUMA");
				int colLargo = idxMap.get("LARGO");
				int colAncho = idxMap.get("ANCHO");
				int colGrueso = idxMap.get("GRUESO");

				int totalFilas = sheet.getLastRowNum();
				int filasProcesadas = 0, filasValidas = 0, filasIgnoradas = 0;

				for (int i = headerRowIdx + 1; i <= totalFilas; i++) {
					Row row = sheet.getRow(i);
					filasProcesadas++;
					if (row == null) {
						filasIgnoradas++;
						continue;
					}
					try {
						Cell cellProv = row.getCell(colProv);
						Cell cellPre = row.getCell(colPrecio);
						Cell cellTipo = row.getCell(colTipoEspuma);
						Cell cellLargo = row.getCell(colLargo);
						Cell cellAncho = row.getCell(colAncho);
						Cell cellGrueso = row.getCell(colGrueso);

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

						float precio = (cellPre != null) ? obtenerValorNumerico(cellPre) : 0;
						if (precio <= 0) {
							filasIgnoradas++;
							continue;
						}

						float largo = (cellLargo != null) ? obtenerValorCelda(cellLargo) : 0;
						float ancho = (cellAncho != null) ? obtenerValorCelda(cellAncho) : 0;
						float grueso = (cellGrueso != null) ? obtenerValorCelda(cellGrueso) : 0;

						// Clave única: nombre|tipo|largo|ancho|grueso (normalizada)
						String key = (rawName + "|" + tipoEspuma + "|" + largo + "|" + ancho + "|" + grueso)
								.toLowerCase().replaceAll("\\s+", " ");

						Proveedor nuevoProveedor = new Proveedor(rawName, precio, tipoEspuma, largo, ancho, grueso);

						// Consolidar por precio mínimo
						if (!proveedorMap.containsKey(key) || proveedorMap.get(key).getPrecioUnitario() > precio) {
							proveedorMap.put(key, nuevoProveedor);
						}
						filasValidas++;
					} catch (Exception e) {
						logger.warning("Error procesando fila " + i + ": " + e.getMessage());
						filasIgnoradas++;
					}
				}
				logger.info(String.format(
						"Excel procesado: %d filas totales, %d válidas, %d ignoradas, %d proveedores únicos",
						totalFilas, filasValidas, filasIgnoradas, proveedorMap.size()));
			}
		} catch (IOException e) {
			throw new RuntimeException("Error leyendo el archivo Excel: " + e.getMessage(), e);
		}
		List<Proveedor> resultado = new ArrayList<>(proveedorMap.values());
		logger.info("Total de proveedores encontrados: " + resultado.size());
		return resultado;
	}

	// Métodos utilitarios y validaciones

	private static InputStream obtenerInputStream(String resourcePath) throws IOException {
		File file = new File(resourcePath);
		if (file.exists() && file.canRead()) {
			return new FileInputStream(file);
		}
		InputStream is = ExcelReader.class.getClassLoader().getResourceAsStream(resourcePath);
		if (is == null) {
			String resourcesPath = "resources/" + resourcePath;
			is = ExcelReader.class.getClassLoader().getResourceAsStream(resourcesPath);
			if (is == null) {
				String absolutePath = "src/main/resources/" + resourcePath;
				File resourceFile = new File(absolutePath);
				if (resourceFile.exists() && resourceFile.canRead()) {
					return new FileInputStream(resourceFile);
				}
			}
		}
		return is;
	}

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

	private static String getCellValueAsString(Cell cell) {
		if (cell == null)
			return "";
		switch (cell.getCellType()) {
			case STRING:
				return cell.getStringCellValue().trim();
			case NUMERIC:
				double val = cell.getNumericCellValue();
				if (val == (long) val)
					return String.valueOf((long) val);
				else
					return String.valueOf(val);
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case FORMULA:
				try {
					return cell.getStringCellValue();
				} catch (Exception e1) {
					try {
						return String.valueOf(cell.getNumericCellValue());
					} catch (Exception e2) {
						return cell.getCellFormula();
					}
				}
			case BLANK:
			default:
				return "";
		}
	}

	private static float obtenerValorNumerico(Cell cell) {
		if (cell == null)
			return 0;
		try {
			switch (cell.getCellType()) {
				case NUMERIC:
					return (float) cell.getNumericCellValue();
				case STRING:
					String valor = cell.getStringCellValue().trim().replace("€", "").replace(",", ".").trim();
					if (valor.isEmpty())
						return 0;
					return Float.parseFloat(valor);
				case FORMULA:
					try {
						return (float) cell.getNumericCellValue();
					} catch (Exception e) {
						String valorStr = cell.getStringCellValue().trim().replace("€", "").replace(",", ".").trim();
						if (valorStr.isEmpty())
							return 0;
						return Float.parseFloat(valorStr);
					}
				default:
					return 0;
			}
		} catch (Exception e) {
			return 0;
		}
	}

	private static String normalizarTipoEspuma(String tipo) {
		if (tipo == null)
			return "";
		return tipo.trim().toLowerCase().replaceAll("\\s+", " ");
	}

	private static float obtenerValorCelda(Cell cell) {
		if (cell == null)
			return 0;
		try {
			switch (cell.getCellType()) {
				case NUMERIC:
					return (float) cell.getNumericCellValue();
				case STRING:
					String valorStr = cell.getStringCellValue().trim().replace(",", ".").trim();
					if (valorStr.isEmpty())
						return 0;
					return Float.parseFloat(valorStr);
				case FORMULA:
					try {
						return (float) cell.getNumericCellValue();
					} catch (Exception e) {
						String valor = cell.getStringCellValue().trim().replace(",", ".").trim();
						if (valor.isEmpty())
							return 0;
						return Float.parseFloat(valor);
					}
				default:
					return 0;
			}
		} catch (Exception e) {
			return 0;
		}
	}
}
