package beb.servizi;

import beb.dominio.Cliente;
import beb.dominio.Prenotazione;
import java.time.LocalDate;
import java.util.HashMap;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServiziPagamentiTest {

    @Test(expected = RuntimeException.class)
    public void testCartaTroppoCorta() {
        ServiziPagamenti sp = new ServiziPagamenti(new HashMap<>());

        // Tentiamo un pagamento con carta da 15 cifre (deve fallire)
        sp.creaPagamento(123, "123456789012345", 100.0f, null, null);
    }

    @Test(expected = RuntimeException.class)
    public void testCvvNonValidoBasso() {
        ServiziPagamenti sp = new ServiziPagamenti(new HashMap<>());

        // CVV 99 è fuori dal range 100-999 (deve fallire)
        sp.creaPagamento(99, "1234567812345678", 100.0f, null, null);
    }

    @Test(expected = RuntimeException.class)
    public void testCvvNonValidoAlto() {
        ServiziPagamenti sp = new ServiziPagamenti(new HashMap<>());

        // CVV 1000 è troppo alto (deve fallire)
        sp.creaPagamento(1000, "1234567812345678", 100.0f, null, null);
    }

    @Test
    public void testPagamentoValido() {
        ServiziPagamenti sp = new ServiziPagamenti(new HashMap<>());

        // Testiamo una carta valida (16 cifre) e un CVV nel range (100-999)
        // Non dovrebbe lanciare nessuna eccezione
        try {
            sp.creaPagamento(555, "1234567812345878", 150.0f, null, null);
        } catch (Exception e) {
            fail("Il pagamento con dati validi non dovrebbe lanciare eccezioni: " + e.getMessage());
        }
    }

}