package model;

import persistence.PersistenciaManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class SistemaModel {

    private final PersistenciaManager persistencia = new PersistenciaManager();
    private final Random random = new Random();


    // Pacientes


    public List<Paciente> obtenerTodosPacientes() {

        return persistencia.leerPacientes();
    }

    public Paciente buscarPaciente(String documento) {

        return persistencia.buscarPaciente(documento);
    }

    public boolean documentoYaRegistrado(String documento) {

        return buscarPaciente(documento) != null;
    }

    public boolean registrarPaciente(Paciente paciente) {

        return persistencia.guardarPaciente(paciente);
    }


    // Asesores / Login


    public String[] loginAsesor(String idAsesor) {

        return persistencia.buscarAsesor(idAsesor);
    }

    public Paciente validarPacienteParaOrden(String documento) throws Exception {
        Paciente p = buscarPaciente(documento);
        if (p == null) throw new Exception("Paciente no encontrado con cédula: " + documento);
        if (!p.isActivo()) throw new Exception("El paciente " + p.getNombre() + " está inactivo en el sistema.");
        return p;
    }


    //Instituciones


    public List<Institucion> obtenerTodasInstituciones() {

        return persistencia.leerInstituciones();
    }

    public List<Institucion> institucionesPorEspecialidad(String especialidad) {
        List<Institucion> filtradas = new ArrayList<>();
        for (Institucion inst : obtenerTodasInstituciones()) {
            if (inst.tieneEspecialidad(especialidad)) filtradas.add(inst);
        }
        return filtradas;
    }

    public Institucion buscarInstitucion(String nombre) {

        return persistencia.buscarInstitucion(nombre);
    }


    //Médicos


    public List<Medico> obtenerTodosMedicos() {

        return persistencia.leerMedicos();
    }

    public List<Medico> medicosPorEspecialidadEInstitucion(String especialidad, String institucion) {
        List<Medico> lista = new ArrayList<>();
        for (Medico m : obtenerTodosMedicos()) {
            boolean coincideEsp = m.getEspecialidad().equalsIgnoreCase(especialidad.trim());
            boolean coincideInst = institucion == null || institucion.isEmpty()
                    || m.getInstitucion().equalsIgnoreCase(institucion.trim());
            if (coincideEsp && coincideInst) lista.add(m);
        }
        return lista;
    }


    //CUPS


    public String[] buscarCups(String codigo) {

        return persistencia.buscarCups(codigo);
    }

    public String descripcionCups(String codigo) {
        String[] d = buscarCups(codigo);
        return (d != null && d.length > 2) ? d[2] : null;
    }

    public double precioCups(String codigo) {
        if (codigo == null || codigo.isEmpty()) return 0;
        String prefijo = codigo.substring(0, Math.min(2, codigo.length()));
        try {
            int base = Integer.parseInt(prefijo);
            double precio = 50000 + (base * 7500.0);
            if (precio > 800000) precio = 800000;
            return Math.round(precio / 1000.0) * 1000.0;
        } catch (Exception e) {
            return 85000;
        }
    }

    public boolean existeCups(String codigo) {

        return buscarCups(codigo) != null;
    }


    //CIE-10


    public String[] buscarCie(String codigo) {
        return persistencia.buscarCie(codigo);
    }

    public String descripcionCie(String codigo) {
        String[] d = buscarCie(codigo);
        return (d != null && d.length > 2) ? d[2] : null;
    }

    public boolean existeCie(String codigo) {
        return buscarCie(codigo) != null;
    }

    public String especialidadDesdeCie(String cie) {
        if (cie == null || cie.trim().isEmpty()) return "Medicina General";
        String letra = cie.substring(0, 1).toUpperCase();
        switch (letra) {
            case "A": case "B": return "Infectología";
            case "C": case "D": return "Oncología";
            case "E":           return "Endocrinología";
            case "F":           return "Psiquiatría";
            case "G":           return "Neurología";
            case "H":           return "Oftalmología";
            case "I":           return "Cardiología";
            case "J":           return "Medicina General";
            case "K":           return "Gastroenterología";
            case "L":           return "Dermatología";
            case "M":           return "Ortopedia";
            case "N":           return "Urología";
            case "O":           return "Ginecología";
            case "P": case "Q": return "Pediatría";
            default:            return "Medicina General";
        }
    }

    public List<String[]> recomendarCupsDesdeCie(String codigoCie) {
        if (codigoCie == null || codigoCie.trim().isEmpty()) return new ArrayList<>();
        String letra = codigoCie.substring(0, 1).toUpperCase();
        String prefijo = obtenerPrefijoCupsDesdeCie(letra);
        if (prefijo == null) return new ArrayList<>();
        return persistencia.leerCupsPorPrefijo(prefijo, 10);
    }

    private String obtenerPrefijoCupsDesdeCie(String letraCie) {
        switch (letraCie) {
            case "A": case "B": return "19";
            case "C": case "D": return "30";
            case "E":           return "89";
            case "F":           return "90";
            case "G":           return "87";
            case "H":           return "20";
            case "I":           return "88";
            case "J":           return "27";
            case "K":           return "17";
            case "L":           return "23";
            case "M":           return "31";
            case "N":           return "29";
            case "O":           return "35";
            case "P":           return "46";
            default:            return "89";
        }
    }


    //Órdenes


    public Orden crearOrden(String asesorId, String asesorNombre,
                             Paciente paciente, Medico medico,
                             Institucion institucion, List<Procedimiento> procedimientos) {
        int radicado = 10000 + random.nextInt(90000);
        if(paciente.isConvenio()){
            for(Procedimiento  p : procedimientos) {
                p.setValor(p.getValor() * 0.85);
            }
        }
        return new Orden(radicado, paciente, medico, institucion, procedimientos, asesorId, asesorNombre);
    }

    public void guardarOrden(Orden orden) {
        List<Orden> historial = obtenerHistorialOrdenes();
        historial.add(orden);
        persistencia.guardarOrdenes(historial);
    }

    public List<Orden> obtenerHistorialOrdenes() {
        return persistencia.leerOrdenes();
    }

    public List<Orden> ordenesPorPaciente(String cedula) {
        List<Orden> resultado = new ArrayList<>();
        for (Orden o : obtenerHistorialOrdenes()) {
            if (o.getCedulaPaciente().equalsIgnoreCase(cedula.trim())) resultado.add(o);
        }
        return resultado;
    }

    public boolean modificarOrden(int radicado, Medico nuevoMedico,
                                   Institucion nuevaInstitucion, List<Procedimiento> nuevosProcedimientos,
                                   String asesorId, String asesorNombre) {
        List<Orden> historial = obtenerHistorialOrdenes();
        boolean encontrada = false;
        for (Orden o : historial) {
            if (o.getRadicado() == radicado) {
                o.setMedicoId(nuevoMedico.getId());
                o.setMedicoNombre(nuevoMedico.getNombre());
                o.setEspecialidad(nuevoMedico.getEspecialidad());
                o.setInstitucion(nuevaInstitucion.getNombre());
                o.setCiudadInstitucion(nuevaInstitucion.getCiudad());
                o.setProcedimientos(nuevosProcedimientos);
                o.setAsesorUltimaAccion(asesorId);
                o.setNombreUltimaAccion(asesorNombre);
                encontrada = true;
                break;
            }
        }
        if (!encontrada) return false;
        return persistencia.guardarOrdenes(historial);
    }

    public boolean cancelarOrden(int radicado, String asesorId, String asesorNombre) {
        List<Orden> historial = obtenerHistorialOrdenes();
        boolean encontrada = false;
        for (Orden o : historial) {
            if (o.getRadicado() == radicado) {
                o.setEstadoOrden("CANCELADA");
                o.setAsesorUltimaAccion(asesorId);
                o.setNombreUltimaAccion(asesorNombre);
                encontrada = true;
                break;
            }
        }
        if (!encontrada) return false;
        return persistencia.guardarOrdenes(historial);
    }


    //Pdf


    public void generarPdf(Orden orden) {
        GeneradorPdf generador = new GeneradorPdf();
        generador.crearPdf(orden);
    }
}
