package beb.facade;

import beb.dominio.Camera;
import beb.dominio.Prenotazione;
import java.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.After;
import java.lang.reflect.Field;

public class BnBTest {

    private BnB bnb;

    //Ho usato @Before. Questo metodo viene eseguito prima di ogni singolo test della classe. Serve a "pulire"  in modo che ogni test parta da una situazione pulita.
    @Before
    public void setUp() {
        // Otteniamo l'istanza Singleton (mi garantisce l'esistenza di una singola istanza di una classe)
        bnb = BnB.getInstance();

        // Popoliamo il sistema con una camera di test (Singola, 1 disponibile)
        bnb.aggiungiCameraSistema(new Camera("Singola", 50.0f, 1, 1));

        // Registriamo un cliente di test
        bnb.registraCliente("Mario Rossi", "mario@email.it", "password123");
    }

    @After
    public void tearDown() throws Exception {
        // Resetta l'istanza Singleton di BnB dopo ogni test
        Field instance = BnB.class.getDeclaredField("istanza");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testPrenotazioneEVerificaDisponibilita() {
        int idCliente = bnb.getIdCliente("mario@email.it");
        LocalDate inizio = LocalDate.now().plusDays(10);
        LocalDate fine = LocalDate.now().plusDays(15);

        // 1. Verifichiamo che inizialmente la camera sia disponibile
        assertTrue("La camera dovrebbe essere disponibile all'inizio",
                bnb.verificaDisponibilita("Singola", inizio, fine));

        // 2. Effettuiamo una prenotazione
        Prenotazione p = bnb.richiediPrenotazione("Singola", inizio, fine, idCliente);
        assertNotNull("La prenotazione non dovrebbe essere null", p);

        // 3. Ora che la camera è occupata (quantità = 1), la disponibilità deve essere FALSE
        // Lo State Pattern in StatoAttesa restituisce isOccupante = true
        assertFalse("La camera NON dovrebbe più essere disponibile per le stesse date",
                bnb.verificaDisponibilita("Singola", inizio, fine));
    }

    @Test(expected = RuntimeException.class)
    public void testPrenotazioneInSovrapposizione() {
        int idCliente = bnb.getIdCliente("mario@email.it");
        LocalDate inizio = LocalDate.now().plusDays(20);
        LocalDate fine = LocalDate.now().plusDays(25);

        bnb.richiediPrenotazione("Singola", inizio, fine, idCliente);

        // Seconda prenotazione nelle STESSE date deve fallire perché la camera è solo una
        // La Facade lancerà una RuntimeException ("Camera non disponibile")
        bnb.richiediPrenotazione("Singola", inizio, fine, idCliente);
    }


    /* Questi due test finali mi servono solo a capire se l'after dopo ogni test funziona
    @Test
    public void testAggiuntaCamera() {
        bnb.aggiungiCameraSistema(new Camera("Singola", 50.0f, 1, 1));
        // Se vuoi verificare le camere, dovresti avere un metodo bnb.getListaCamere()
        // Per ora, visualizzaRichieste().size() deve essere 0 perché non ci sono prenotazioni
        assertEquals(0, bnb.visualizzaRichieste().size());
    }
    @Test
    public void testSistemaVuoto() {
        // Se @After funziona, questa lista deve essere VUOTA (0),
        // anche se il test sopra ha appena aggiunto una camera
        assertEquals("Il sistema dovrebbe essere pulito!", 0, bnb.visualizzaRichieste().size());
    }
    */
}