package beb.dominio;

import org.junit.Test;
import static org.junit.Assert.*;

public class CameraTest {

    @Test
    public void testDatiCameraCorretti() {

        Camera c = new Camera("Suite Royal", 250.0f, 4, 2);

        assertEquals("Suite Royal", c.getTipoCamera());
        assertEquals(250.0f, c.getPrezzoPerNotte(), 0.001);
        assertEquals("La capienza dovrebbe essere 4", 4, c.getCapienza());
        assertEquals("La quantità disponibile dovrebbe essere 2", 2, c.getQuantita());
    }

    @Test(expected = RuntimeException.class)
    public void testPrezzoNegativoNonValido() {
        // Il sistema dovrebbe bloccare prezzi assurdi
        new Camera("Economica", -10.0f, 1, 1);
    }
}