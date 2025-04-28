package main.java.com.programa_costos.io;

import main.java.com.programa_costos.model.Proveedor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.io.IOException;
import java.util.*;

public class ExcelReader {

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
        try (InputStream is = ExcelReader.class.getClassLoader().getResourceAsStream(resourcePath);
             Workbook workbook = new XSSFWorkbook(is)) {

            if (is == null) {
                throw new IllegalArgumentException("No se encontró el recurso Excel: " + resourcePath);
            }

            Sheet sheet = workbook.getSheetAt(0);
            int headerRowIdx = -1;

            // Buscar encabezado que contenga 'Prov.' o 'Proveedor'
            for (Row row : sheet) {
                for (Cell cell : row) {
                    if (cell.getCellType() == CellType.STRING) {
                        String txt = cell.getStringCellValue().trim();
                        if (txt.equalsIgnoreCase("Prov.") || txt.equalsIgnoreCase("Proveedor")) {
                            headerRowIdx = row.getRowNum();
                            break;
                        }
                    }
                }
                if (headerRowIdx >= 0)
                    break;
            }
            if (headerRowIdx < 0) {
                throw new IllegalStateException("No se encontró la fila de encabezado con 'Prov.' o 'Proveedor'.");
            }

            // Mapear nombre de columna a índice (normalizado)
            Row header = sheet.getRow(headerRowIdx);
            Map<String, Integer> idxMap = new HashMap<>();
            for (Cell cell : header) {
                if (cell.getCellType() == CellType.STRING) {
                    String normalized = cell.getStringCellValue().trim().replaceAll("\\s+", " ").toUpperCase();
                    idxMap.put(normalized, cell.getColumnIndex());
                }
            }

            // Validar que existan columnas necesarias
            if (!idxMap.containsKey("PROV.") || !idxMap.containsKey("IMPORTE UD.") || !idxMap.containsKey("TIPO DE ESPUMA")) {
                throw new IllegalStateException("Faltan columnas 'Prov.', 'IMPORTE UD.' o 'TIPO DE ESPUMA' en encabezado.");
            }

            int colProv = idxMap.get("PROV.");
            int colPrecio = idxMap.get("IMPORTE UD.");
            int colTipoEspuma = idxMap.get("TIPO DE ESPUMA");

            // Leer datos
            for (int i = headerRowIdx + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                Cell cellProv = row.getCell(colProv);
                Cell cellPre = row.getCell(colPrecio);
                Cell cellTipo = row.getCell(colTipoEspuma);

                if (cellProv == null || cellPre == null || cellTipo == null)
                    continue;

                String rawName = cellProv.toString().trim();
                String tipoEspuma = cellTipo.toString().trim();

                if (rawName.isEmpty() || tipoEspuma.isEmpty())
                    continue; // sin nombre o sin tipo de espuma, ignorar

                // Normalizar clave
                String key = (rawName + "|" + tipoEspuma).toLowerCase().replaceAll("\\s+", " ");

                // Parsear precio
                double precio;
                try {
                    precio = Double.parseDouble(cellPre.toString().trim());
                } catch (NumberFormatException e) {
                    continue; // valor inválido
                }

                // Guardar o actualizar precio mínimo
                Proveedor exist = proveedorMap.get(key);
                if (exist == null || precio < exist.getPrecioUnitario()) {
                    proveedorMap.put(key, new Proveedor(rawName, precio, tipoEspuma));  // Aquí pasamos 'tipoEspuma'
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo el archivo Excel: " + e.getMessage(), e);
        }
        return new ArrayList<>(proveedorMap.values());
    }
}
