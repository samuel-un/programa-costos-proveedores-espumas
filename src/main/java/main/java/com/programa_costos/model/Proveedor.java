package main.java.com.programa_costos.model;

public class Proveedor {
    private String nombre;
    private double precioUnitario;
    private String tipoEspuma; // NUEVO

    // Constructor actualizado con tipoEspuma
    public Proveedor(String nombre, double precioUnitario, String tipoEspuma) {
        this.nombre = nombre;
        this.precioUnitario = precioUnitario;
        this.tipoEspuma = tipoEspuma;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getTipoEspuma() {
        return tipoEspuma;
    }

    public void setTipoEspuma(String tipoEspuma) {
        this.tipoEspuma = tipoEspuma;
    }

    @Override
    public String toString() {
        return "Proveedor{" + "nombre='" + nombre + '\'' +
                ", precioUnitario=" + precioUnitario +
                ", tipoEspuma='" + tipoEspuma + '\'' +
                '}';
    }
}
