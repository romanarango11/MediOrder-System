package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProcedimientoTest {

    @Test
    void testCrearProcedimiento() {
        Procedimiento p = new Procedimiento("200801", "H25", "Extracción de catarata", "Catarata senil", 2, 250000);

        assertEquals("200801", p.getCups());
        assertEquals("H25", p.getCie());
        assertEquals("Extracción de catarata", p.getDescripcionCups());
        assertEquals("Catarata senil", p.getDescripcionCie());
        assertEquals(2, p.getCantidad());
        assertEquals(250000, p.getValor());
    }

    @Test
    void testGetSubtotal() {
        Procedimiento p = new Procedimiento("C01", "A01", "Desc", "Desc", 3, 100000);
        assertEquals(300000, p.getSubtotal());
    }

    @Test
    void testSetValor() {
        Procedimiento p = new Procedimiento("C01", "A01", "Desc", "Desc", 1, 1000);
        p.setValor(5000);
        assertEquals(5000, p.getValor());
        assertEquals(5000, p.getSubtotal());
    }
}