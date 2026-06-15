package model;

public class Institucion {

    private String nombre;
    private String ciudad;
    private Especialidad[] especialidadesOfrecidas;

    public Institucion(String nombre, String ciudad, String especialidadesRaw) {

        this.nombre = nombre;
        this.ciudad = ciudad;

        if (especialidadesRaw == null || especialidadesRaw.trim().isEmpty()) {
            especialidadesOfrecidas = new Especialidad[0];
            return;
        }

        String[] lista = especialidadesRaw.split(";");
        especialidadesOfrecidas = new Especialidad[lista.length];

        for (int i = 0; i < lista.length; i++) {
            especialidadesOfrecidas[i] = new Especialidad(lista[i].trim());
        }
    }

    public boolean tieneEspecialidad(String nombreEsp) {

        if (nombreEsp == null || nombreEsp.trim().isEmpty()) {
            return false;
        }

        String nombreBuscado = normalizarTexto(nombreEsp.trim());

        for (Especialidad e : especialidadesOfrecidas) {
            String nombreActual = normalizarTexto(e.getNombre());
            if (nombreActual.equals(nombreBuscado)) {
                return true;
            }
        }

        return false;
    }

    // Metodo auxiliar para normalizar texto: eliminar acentos y convertir a minúsculas
    private String normalizarTexto(String texto) {
        if (texto == null) return "";

        String normalizado = texto.toLowerCase();

        // Reemplazar vocales acentuadas
        normalizado = normalizado.replaceAll("á", "a")
                .replaceAll("é", "e")
                .replaceAll("í", "i")
                .replaceAll("ó", "o")
                .replaceAll("ú", "u")
                .replaceAll("ü", "u")
                .replaceAll("ñ", "n");

        return normalizado;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCiudad() {
        return ciudad;
    }

    public Especialidad[] getEspecialidadesOfrecidas() {
        return especialidadesOfrecidas;
    }

    @Override
    public String toString() {
        return nombre + " - " + ciudad;
    }
}