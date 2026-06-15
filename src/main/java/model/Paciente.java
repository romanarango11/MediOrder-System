package model;

public class Paciente {

    private String tipoDocumento;
    private String documento;
    private String nombre;
    private int edad;
    private String sexo;
    private String plan;
    private boolean afiliado;
    private boolean activo;
    private boolean convenio;

    public Paciente(
            String tipoDocumento,
            String documento,
            String nombre,
            int edad,
            String sexo,
            String plan,
            boolean afiliado,
            boolean activo,
            boolean convenio) {

        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.nombre = nombre;
        this.edad = edad;
        this.sexo = sexo;
        this.plan = plan;
        this.afiliado = afiliado;
        this.activo = activo;
        this.convenio = convenio;
    }

    // Construir desde arreglo CSV
    public static Paciente desdeCsv(String[] datos) {

        if (datos == null || datos.length < 9) {
            return null;
        }

        return new Paciente(
                datos[0],
                datos[1],
                datos[2],
                Integer.parseInt(datos[3].trim()),
                datos[4],
                datos[5],
                datos[6].equalsIgnoreCase("SI"),
                datos[7].equalsIgnoreCase("Activo"),
                datos[8].equalsIgnoreCase("SI")
        );
    }

    public String toCsvLinea() {

        return tipoDocumento + ","
                + documento + ","
                + nombre + ","
                + edad + ","
                + sexo + ","
                + plan + ","
                + (afiliado ? "SI" : "NO") + ","
                + (activo ? "Activo" : "Inactivo") + ","
                + (convenio ? "SI" : "NO");
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    public String getSexo() {
        return sexo;
    }

    public String getPlan() {
        return plan;
    }

    public boolean isAfiliado() {
        return afiliado;
    }

    public boolean isActivo() {
        return activo;
    }

    public boolean isConvenio() {
        return convenio;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public void setAfiliado(boolean afiliado) {
        this.afiliado = afiliado;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setConvenio(boolean convenio) {
        this.convenio = convenio;
    }
}
