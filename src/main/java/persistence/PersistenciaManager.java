package persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class PersistenciaManager {


    //Rutas de archivos

    private static final String PACIENTES_CSV     = "data/pacientes.csv";
    private static final String ASESORES_CSV      = "data/asesores.csv";
    private static final String INSTITUCIONES_CSV = "data/instituciones.csv";
    private static final String MEDICOS_CSV       = "data/medicos.csv";
    private static final String CUPS_CSV          = "data/TablaReferencia_CUPS.csv";
    private static final String CIE10_CSV         = "data/TablaReferencia_CIE10.csv";
    private static final String HISTORIAL_JSON    = "data/historico_ordenes.json";

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();


    //Pacientes

    public List<Paciente> leerPacientes() {
        List<Paciente> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(PACIENTES_CSV), "UTF-8"))) {
            br.readLine(); // encabezado
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = parsearCsv(linea);
                Paciente p = Paciente.desdeCsv(datos);
                if (p != null) lista.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Paciente buscarPaciente(String documento) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(PACIENTES_CSV), "UTF-8"))) {
            br.readLine();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = parsearCsv(linea);
                if (datos.length > 1 && datos[1].equalsIgnoreCase(documento.trim())) {
                    return Paciente.desdeCsv(datos);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean guardarPaciente(Paciente paciente) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(PACIENTES_CSV, true), "UTF-8"))) {
            bw.newLine();
            bw.write(paciente.toCsvLinea());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    //Asesores


    public String[] buscarAsesor(String idAsesor) {

        return buscarEnCsv(ASESORES_CSV, idAsesor, 0);
    }


    //Instituciones


    public List<Institucion> leerInstituciones() {
        List<Institucion> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(INSTITUCIONES_CSV), "UTF-8"))) {
            br.readLine();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = parsearCsv(linea);
                if (datos.length >= 3) {
                    lista.add(new Institucion(datos[0].trim(), datos[1].trim(), datos[2].trim()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Institucion buscarInstitucion(String nombre) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(INSTITUCIONES_CSV), "UTF-8"))) {
            br.readLine();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = parsearCsv(linea);
                if (datos.length >= 3 && datos[0].equalsIgnoreCase(nombre.trim())) {
                    return new Institucion(datos[0].trim(), datos[1].trim(), datos[2].trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    //Médicos


    public List<Medico> leerMedicos() {
        List<Medico> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(MEDICOS_CSV), "UTF-8"))) {
            br.readLine();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = parsearCsv(linea);
                Medico m = Medico.desdeCsv(datos);
                if (m != null) lista.add(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }


    //CUPS


    public String[] buscarCups(String codigo) {
        return buscarEnCsv(CUPS_CSV, codigo, 1);
    }

    public List<String[]> leerCupsPorPrefijo(String prefijo, int limite) {
        List<String[]> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(CUPS_CSV), "UTF-8"))) {
            br.readLine();
            String linea;
            int contador = 0;
            while ((linea = br.readLine()) != null && contador < limite) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = parsearCsv(linea);
                if (datos.length > 1 && datos[1].trim().toUpperCase().startsWith(prefijo)) {
                    lista.add(datos);
                    contador++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }


    //CIE-10


    public String[] buscarCie(String codigo) {
        return buscarEnCsv(CIE10_CSV, codigo, 1);
    }


    //Órdenes(JSON)


    public List<Orden> leerOrdenes() {
        List<Orden> lista = new ArrayList<>();
        try {
            File archivo = new File(HISTORIAL_JSON);
            if (!archivo.exists() || archivo.length() == 0) return lista;
            try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                Type tipo = new TypeToken<ArrayList<Orden>>() {}.getType();
                List<Orden> datos = gson.fromJson(br, tipo);
                if (datos != null) lista.addAll(datos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean guardarOrdenes(List<Orden> ordenes) {
        try {
            File archivo = new File(HISTORIAL_JSON);
            archivo.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(archivo)) {
                gson.toJson(ordenes, fw);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    //Utilidades privadas


    private String[] buscarEnCsv(String archivo, String valor, int columna) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo), "UTF-8"))) {
            br.readLine();
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;
                String[] datos = parsearCsv(linea);
                if (datos.length > columna && datos[columna].equalsIgnoreCase(valor.trim())) {
                    return datos;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Parsea una línea CSV respetando campos entre comillas.

    private String[] parsearCsv(String linea) {
        String[] partes = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (int i = 0; i < partes.length; i++) {
            partes[i] = partes[i].replace("\"", "").trim();
        }
        return partes;
    }
}
