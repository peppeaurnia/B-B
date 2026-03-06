package beb.servizi;

import beb.dominio.Cliente;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServiziClientiTest {

    @Test(expected = RuntimeException.class)
    public void testRicercaClienteInesistente() {
        // Setup manuale del servizio senza passare dalla Facade
        Map<Integer, Cliente> mappa = new HashMap<>();
        ServiziClienti sc = new ServiziClienti(mappa);

        // Deve lanciare RuntimeException perché la mappa è vuota
        sc.cercaCliente("fantasma@test.it");
    }

    @Test
    public void testAutenticazioneCorretta() {
        Map<Integer, Cliente> mappa = new HashMap<>();
        ServiziClienti sc = new ServiziClienti(mappa);

        // Registriamo un cliente
        sc.registraCliente("Test", "test@email.it", "1234");

        // Verifichiamo il login
        assertTrue("L'autenticazione dovrebbe avere successo con credenziali corrette",
                sc.autenticaCliente("test@email.it", "1234"));

        assertFalse("L'autenticazione deve fallire con password errata",
                sc.autenticaCliente("test@email.it", "sbagliata"));
    }
}