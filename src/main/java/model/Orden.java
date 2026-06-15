package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Orden {

    private int radicado;
    private String tipoDocumento;
    private String documento;
    private String nombrePaciente;
    private int edad;
    private String sexo;
    private String plan;
    private String afiliado;
    private String convenio;
    private String medicoId;
    private String medicoNombre;
    private String especialidad;
    private String institucion;
    private String ciudadInstitucion;
    private List<Procedimiento> procedimientos;
    private double total;
    private String estadoOrden;
    private String fechaEmision;
    private String fechaVencimiento;
    private String asesorId;
    private String asesorNombre;
    private String asesorUltimaAccion;
    private String nombreUltimaAccion;

    public Orden(
            int radicado,
            Paciente paciente,
            Medico medico,
            Institucion institucion,
            List<Procedimiento> procedimientos,
            String asesorId,
            String asesorNombre) {

        this.radicado = radicado;
        this.tipoDocumento = paciente.getTipoDocumento();
        this.documento = paciente.getDocumento();
        this.nombrePaciente = paciente.getNombre();
        this.edad = paciente.getEdad();
        this.sexo = paciente.getSexo();
        this.plan = paciente.getPlan();
        this.afiliado = paciente.isAfiliado() ? "SI" : "NO";
        this.convenio = paciente.isConvenio() ? "SI" : "NO";
        this.medicoId = medico.getId();
        this.medicoNombre = medico.getNombre();
        this.especialidad = medico.getEspecialidad();
        this.institucion = institucion.getNombre();
        this.ciudadInstitucion = institucion.getCiudad();
        this.procedimientos = new ArrayList<>(procedimientos);
        this.asesorId = asesorId;
        this.asesorNombre = asesorNombre;
        this.estadoOrden = "ACTIVA";

        this.total = 0;
        for (Procedimiento p : procedimientos) {
            this.total += p.getSubtotal();
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.fechaEmision = LocalDate.now().format(fmt);
        this.fechaVencimiento = LocalDate.now().plusMonths(4).format(fmt);
    }

    public int getRadicado() {
        return radicado;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public String getCedulaPaciente() {
        return documento;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
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

    public String getAfiliado() {
        return afiliado;
    }

    public String getConvenio() {
        return convenio;
    }

    public String getMedicoId() {
        return medicoId;
    }

    public String getMedicoNombre() {
        return medicoNombre;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public String getInstitucion() {
        return institucion;
    }

    public String getCiudadInstitucion() {
        return ciudadInstitucion;
    }

    public List<Procedimiento> getProcedimientos() {
        return procedimientos;
    }

    public double getTotal() {
        return total;
    }

    public String getEstadoOrden() {
        return estadoOrden;
    }

    public void setEstadoOrden(String estadoOrden) {
        this.estadoOrden = estadoOrden;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }
    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public String getAsesorId() {
        return asesorId;
    }

    public String getAsesorNombre() {
        return asesorNombre;
    }

    // Setters para modificación de orden
    public void setMedicoId(String medicoId) {
        this.medicoId = medicoId;
    }

    public void setMedicoNombre(String medicoNombre) {
        this.medicoNombre = medicoNombre;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public void setInstitucion(String institucion) {
        this.institucion = institucion;
    }

    public void setCiudadInstitucion(String ciudadInstitucion) {
        this.ciudadInstitucion = ciudadInstitucion;
    }

    public void setProcedimientos(List<Procedimiento> procedimientos) {
        this.procedimientos = new ArrayList<>(procedimientos);
        this.total = 0;
        for (Procedimiento p : this.procedimientos) {
            this.total += p.getSubtotal();
        }
    }

    public String getAsesorUltimaAccion() {
        return asesorUltimaAccion;
    }

    public String getNombreUltimaAccion() {
    return nombreUltimaAccion;
    }

    public void setAsesorUltimaAccion(String id) {
        this.asesorUltimaAccion = id;
    }

    public void setNombreUltimaAccion(String nombre) {
        this.nombreUltimaAccion = nombre;
    }

    public String getAsesorAccion() {
        if (asesorUltimaAccion == null || asesorUltimaAccion.isEmpty()) {
            return asesorNombre != null ? asesorNombre : "";
        }
        return asesorNombre + " → " + nombreUltimaAccion;
    }

    @Override
    public String toString() {

        return "Orden #" + radicado + " - " + nombrePaciente;
    }
}
