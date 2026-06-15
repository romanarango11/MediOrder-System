package model;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;

public class GeneradorPdf {

    public void crearPdf(Orden orden) {

        Document doc = new Document();

        try {
            File carpeta = new File("ordenes_pdf");
            if (!carpeta.exists()) {
                carpeta.mkdirs();
            }

            String ruta = "ordenes_pdf/Orden_" + orden.getRadicado() + ".pdf";
            PdfWriter.getInstance(doc, new FileOutputStream(ruta));

            doc.open();

            Font tituloFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font seccionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont  = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Font boldFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

            // Encabezado
            Paragraph ips = new Paragraph("IPS SISTEMA DE AUTORIZACIONES", tituloFont);
            ips.setAlignment(Element.ALIGN_CENTER);
            doc.add(ips);

            Paragraph subtitulo = new Paragraph("ORDEN DE SERVICIOS MEDICOS", seccionFont);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            doc.add(subtitulo);
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("RADICADO: " + orden.getRadicado(), boldFont));
            doc.add(new Paragraph("ESTADO: " + orden.getEstadoOrden(), boldFont));
            doc.add(new Paragraph("FECHA EMISION: " + orden.getFechaEmision(), normalFont));
            doc.add(new Paragraph("FECHA VENCIMIENTO: " + orden.getFechaVencimiento(), normalFont));
            doc.add(new Paragraph(" "));

            // Datos paciente
            doc.add(new Paragraph("DATOS DEL PACIENTE", seccionFont));
            PdfPTable tablaPac = new PdfPTable(2);
            tablaPac.setWidthPercentage(100);
            tablaPac.addCell("Tipo Documento"); tablaPac.addCell(orden.getTipoDocumento());
            tablaPac.addCell("Documento");      tablaPac.addCell(orden.getDocumento());
            tablaPac.addCell("Nombre");         tablaPac.addCell(orden.getNombrePaciente());
            tablaPac.addCell("Edad");           tablaPac.addCell(String.valueOf(orden.getEdad()));
            tablaPac.addCell("Sexo");           tablaPac.addCell(orden.getSexo());
            tablaPac.addCell("Plan");           tablaPac.addCell(orden.getPlan());
            tablaPac.addCell("Afiliado");       tablaPac.addCell(orden.getAfiliado());
            tablaPac.addCell("Convenio");       tablaPac.addCell(orden.getConvenio());
            doc.add(tablaPac);
            doc.add(new Paragraph(" "));

            // Procedimientos
            doc.add(new Paragraph("PROCEDIMIENTOS AUTORIZADOS", seccionFont));
            PdfPTable tablaProc = new PdfPTable(5);
            tablaProc.setWidthPercentage(100);
            tablaProc.addCell("CUPS");
            tablaProc.addCell("CIE-10");
            tablaProc.addCell("Descripcion");
            tablaProc.addCell("Cantidad");
            tablaProc.addCell("Subtotal");

            for (Procedimiento p : orden.getProcedimientos()) {
                tablaProc.addCell(p.getCups());
                tablaProc.addCell(p.getCie());
                tablaProc.addCell(p.getDescripcionCups());
                tablaProc.addCell(String.valueOf(p.getCantidad()));
                tablaProc.addCell(String.format("$%,.0f", p.getSubtotal()));
            }

            doc.add(tablaProc);
            doc.add(new Paragraph(" "));

            // Asignacion médica
            doc.add(new Paragraph("ASIGNACION MEDICA", seccionFont));
            doc.add(new Paragraph("Institución: " + orden.getInstitucion(), normalFont));
            doc.add(new Paragraph("Médico: " + orden.getMedicoNombre(), normalFont));
            doc.add(new Paragraph("Especialidad: " + orden.getEspecialidad(), normalFont));
            doc.add(new Paragraph("Asesor: " + orden.getAsesorNombre(), normalFont));
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph(
                    "VALOR TOTAL AUTORIZADO: " + String.format("$%,.0f", orden.getTotal()), boldFont));

            doc.add(new Paragraph("\n\n"));

            Paragraph firma = new Paragraph("____________________________\nFirma Responsable", normalFont);
            firma.setAlignment(Element.ALIGN_CENTER);
            doc.add(firma);

            doc.add(new Paragraph("\n"));
            Paragraph pie = new Paragraph("Documento generado automáticamente por el sistema.", normalFont);
            pie.setAlignment(Element.ALIGN_CENTER);
            doc.add(pie);

            doc.close();
            System.out.println("PDF generado: " + ruta);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
