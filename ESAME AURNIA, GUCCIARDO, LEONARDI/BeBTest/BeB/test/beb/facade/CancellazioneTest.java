package beb.facade;

import beb.dominio.Camera;
import beb.dominio.Prenotazione;

import java.lang.reflect.Field;
import java.time.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class CancellazioneTest {

    private BnB bnb;

    @Before
    public void setUp() {
        bnb = BnB.getInstance();
        bnb.aggiungiCameraSistema(new Camera("Matrimoniale", 90.0f, 2, 5));
        bnb.registraCliente("Marco Neri", "marco@email.it", "pass");
    }

    @After
    public void tearDown() throws Exception {
        // Resetta l'istanza Singleton di BnB dopo ogni test
        Field instance = BnB.class.getDeclaredField("istanza");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test(expected = RuntimeException.class)
    public void testClienteNonPuoCancellareDopoCheckIn() {
        int idC = bnb.getIdCliente("marco@email.it");

        // Arriviamo fino allo stato CHECK-IN
        Prenotazione p = bnb.richiediPrenotazione("Matrimoniale", LocalDate.now(), LocalDate.now().plusDays(1), idC);
        bnb.aggiornaRichiesta(p.getIdPrenotazione(), "APPROVATA");
        bnb.pagaRichiesta(p.getIdPrenotazione(), "1234123412341234", 999);
        bnb.confermaCheckIn(p.getIdPrenotazione());

        // Il cliente prova a cancellare una prenotazione dove è già entrato
        // Lo StatoCheckIn non permette la cancellazione cliente (lancia eccezione)
        bnb.cancellaPrenotazioneCliente(p.getIdPrenotazione());
    }

    @Test
    public void testGestorePuoSempreEliminare() {
        int idC = bnb.getIdCliente("marco@email.it");
        Prenotazione p = bnb.richiediPrenotazione("Matrimoniale", LocalDate.now(), LocalDate.now().plusDays(1), idC);

        // Il gestore decide di eliminare la richiesta appena arrivata (Stato: ATTESA)
        bnb.cancellaPrenotazioneGestore(p.getIdPrenotazione());

        assertEquals("Il gestore deve poter forzare lo stato ELIMINATA",
                "ELIMINATA", p.getStatoNome());
        assertFalse("Una prenotazione eliminata non deve occupare la stanza", p.isOccupante());
    }
}