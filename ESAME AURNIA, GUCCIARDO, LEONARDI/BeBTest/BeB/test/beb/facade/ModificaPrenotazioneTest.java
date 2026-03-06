package beb.facade;

import beb.dominio.Camera;
import beb.dominio.Prenotazione;

import java.lang.reflect.Field;
import java.time.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ModificaPrenotazioneTest {

    private BnB bnb;

    @Before
    public void setUp() {
        bnb = BnB.getInstance();
        // Pulizia e setup
        bnb.aggiungiCameraSistema(new Camera("Doppia", 80.0f, 2, 1));
        bnb.registraCliente("Anna Verdi", "anna@email.it", "password");
    }

    @After
    public void tearDown() throws Exception {
        // Resetta l'istanza Singleton di BnB dopo ogni test
        Field instance = BnB.class.getDeclaredField("istanza");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testModificaRifiutataRipristinoDate() {
        int idCliente = bnb.getIdCliente("anna@email.it");

        // 1. Creiamo una prenotazione pagata (Stato: PRENOTATA)
        LocalDate inizioOrig = LocalDate.now().plusDays(10);
        LocalDate fineOrig = LocalDate.now().plusDays(12);
        Prenotazione p = bnb.richiediPrenotazione("Doppia", inizioOrig, fineOrig, idCliente);
        bnb.aggiornaRichiesta(p.getIdPrenotazione(), "APPROVATA");
        bnb.pagaRichiesta(p.getIdPrenotazione(), "1111222233334444", 555);

        // 2. Il cliente richiede una modifica per nuove date
        LocalDate nuovaInizio = LocalDate.now().plusDays(20);
        LocalDate nuovaFine = LocalDate.now().plusDays(22);
        bnb.richiestaModificaPrenotazione(p.getIdPrenotazione(), nuovaInizio, nuovaFine);

        assertEquals("Lo stato deve essere MODIFICATA", "MODIFICATA", p.getStatoNome());
        assertEquals("La data di inizio dovrebbe essere quella nuova", nuovaInizio, p.getDataInizio());

        // 3. Il gestore RIFIUTA la modifica (approvata = false)
        bnb.aggiornaRichiestaModificata(p.getIdPrenotazione(), false);

        // 4. VERIFICA: Lo stato torna PRENOTATA e le date tornano quelle ORIGINALI
        assertEquals("Lo stato deve tornare PRENOTATA", "PRENOTATA", p.getStatoNome());

        // Verifica che la data di inizio sia tornata quella originale
        assertEquals("La data di inizio non è stata ripristinata", inizioOrig, p.getDataInizio());

        // Verifica che la data di fine sia tornata quella originale
        assertEquals("La data di fine non è stata ripristinata", fineOrig, p.getDataFine());
    }

    @Test(expected = RuntimeException.class)
    public void testModificaNonConsentitaInAttesa() {
        int idCliente = bnb.getIdCliente("anna@email.it");
        Prenotazione p = bnb.richiediPrenotazione("Doppia", LocalDate.now().plusDays(5), LocalDate.now().plusDays(7), idCliente);

        // Tentare di modificare una prenotazione in ATTESA (non ancora pagata) deve fallire
        bnb.richiestaModificaPrenotazione(p.getIdPrenotazione(), LocalDate.now().plusDays(10), LocalDate.now().plusDays(12));
    }
}