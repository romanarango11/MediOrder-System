package model;

public class Especialidad {

    private String nombre;

    public Especialidad(String nombre) {

        this.nombre = (nombre == null) ? "" : nombre.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = (nombre != null) ? nombre.trim() : "";
    }

    @Override
    public String toString() {
        return nombre; }
}
