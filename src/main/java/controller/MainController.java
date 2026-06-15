package controller;

import model.*;

import java.util.List;


public class MainController {

    private final SistemaModel modelo = new SistemaModel();

    private String asesorId;
    private String asesorNombre;
    private Paciente pacienteActivo;



    public String loginAsesor(String idAsesor) {

        if (idAsesor == null || idAsesor.trim().isEmpty()) {
            return "ERROR|Ingrese un ID de asesor.";
        }

        String[] asesor = modelo.loginAsesor(idAsesor.trim());

        if (asesor == null) {
            return "ERROR|ID de asesor no encontrado.";
        }

        this.asesorId = idAsesor.trim();
        this.asesorNombre = asesor[1];

        return "OK|Bienvenido " + asesor[1];
    }

    public void cerrarSesion() {
        asesorId = null;
        asesorNombre = null;
        pacienteActivo = null;
    }

    public boolean haySesionActiva() {

        return asesorId != null;
    }

    public String getAsesorNombre() {

        return asesorNombre;
    }

    public String getAsesorId() {

        return asesorId;
    }

   //REGISTRO PACIENTES

    public List<Paciente> obtenerTodosPacientes() {

        return modelo.obtenerTodosPacientes();
    }

    public boolean documentoYaRegistrado(String documento) {

        return modelo.documentoYaRegistrado(documento);
    }

    public boolean registrarPaciente(Paciente paciente) {

        return modelo.registrarPaciente(paciente);
    }


    // BÚSQUEDA DE PACIENTE PARA ORDEN


    public String buscarPacienteParaOrden(String cedula) {

        if (cedula == null || cedula.trim().isEmpty()) {
            return "ERROR|Ingrese una cédula.";
        }

        try {
            pacienteActivo = modelo.validarPacienteParaOrden(cedula.trim());
            return "OK";
        } catch (Exception e) {
            pacienteActivo = null;
            return "ERROR|" + e.getMessage();
        }
    }

    public Paciente getPacienteActivo() {

        return pacienteActivo;
    }

    public void limpiarPacienteActivo() {

        pacienteActivo = null;
    }


    // CUPS y CIE-10


    public boolean existeCups(String codigo) {

        return modelo.existeCups(codigo);
    }

    public boolean existeCie(String codigo) {

        return modelo.existeCie(codigo);
    }

    public String descripcionCups(String codigo) {

        return modelo.descripcionCups(codigo);
    }

    public String descripcionCie(String codigo) {

        return modelo.descripcionCie(codigo);
    }

    public double precioCups(String codigo) {

        return modelo.precioCups(codigo);
    }


    public String especialidadDesdeCie(String cie) {

        return modelo.especialidadDesdeCie(cie);
    }


    public List<String[]> recomendarCupsDesdeCie(String codigoCie) {

        return modelo.recomendarCupsDesdeCie(codigoCie);
    }


    // INSTITUCIONES


    public List<Institucion> institucionesPorEspecialidad(String especialidad) {
        return modelo.institucionesPorEspecialidad(especialidad);
    }

    public Institucion buscarInstitucion(String nombre) {

        return modelo.buscarInstitucion(nombre);
    }


    // MÉDICOS


    public List<Medico> medicosPorEspecialidadEInstitucion(String especialidad, String institucion) {
        return modelo.medicosPorEspecialidadEInstitucion(especialidad, institucion);
    }


    // ORDENES


    public String generarOrden(
            Medico medico,
            Institucion institucion,
            List<Procedimiento> procedimientos) {

        if (!haySesionActiva()) {
            return "ERROR|No hay sesión activa.";
        }

        if (pacienteActivo == null) {
            return "ERROR|Debe buscar un paciente primero.";
        }

        if (procedimientos == null || procedimientos.isEmpty()) {
            return "ERROR|Debe agregar al menos un procedimiento.";
        }

        try {
            Orden orden = modelo.crearOrden(
                    asesorId,
                    asesorNombre,
                    pacienteActivo,
                    medico,
                    institucion,
                    procedimientos);

            modelo.guardarOrden(orden);
            modelo.generarPdf(orden);

            pacienteActivo = null;

            return "OK|" + orden.getRadicado();

        } catch (Exception e) {
            return "ERROR|" + e.getMessage();
        }
    }


    // HISTORIAL


    public List<Orden> obtenerHistorial() {

        return modelo.obtenerHistorialOrdenes();
    }

    public List<Orden> ordenesPorPaciente(String cedula) {

        return modelo.ordenesPorPaciente(cedula);
    }

    public boolean cancelarOrden(int radicado) {

        return modelo.cancelarOrden(radicado, asesorId, asesorNombre);
    }

    public boolean modificarOrden(int radicado, Medico nuevoMedico, Institucion nuevaInstitucion,
                                   List<Procedimiento> nuevosProcedimientos) {
        return modelo.modificarOrden(radicado, nuevoMedico, nuevaInstitucion, nuevosProcedimientos, asesorId, asesorNombre);
    }
}
