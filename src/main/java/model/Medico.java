package model;

public class Medico {

    private String id;
    private String nombre;
    private String especialidad;
    private String institucion;

    public Medico(String id, String nombre, String especialidad, String institucion) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.institucion = institucion;
    }

    public static Medico desdeCsv(String[] datos) {
        if (datos == null || datos.length < 4) return null;
        return new Medico(datos[0].trim(), datos[1].trim(), datos[2].trim(), datos[3].trim());
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }
    public String getInstitucion() {
        return institucion;
    }

    @Override
    public String toString() {
        return nombre + " - " + especialidad;
    }
}
