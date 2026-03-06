package beb.stati;

import beb.dominio.Prenotazione;
import java.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.*;

public class StatoPrenotazioneTest {


    @Test
    public void testInizializzazioneEApprovazione() {
        Prenotazione p = new Prenotazione(1, LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3), 1, 50.0f);

        assertEquals("ATTESA", p.getStatoNome());
        p.approva();
        assertEquals("APPROVATA", p.getStatoNome());
    }

    @Test(expected = RuntimeException.class)
    public void testPagamentoIllegaleInAttesa() {
        Prenotazione p = new Prenotazione(1, LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3), 1, 50.0f);
        p.paga();
    }


    @Test
    public void testStatoOccupaCamera() {
        // Verifichiamo quali stati "bloccano" la camera e quali no

        // Questi stati devono occupare la camera (restituiscono TRUE)
        assertTrue("In ATTESA la camera deve risultare impegnata", new StatoAttesa().isOccupante());
        assertTrue("Se PRENOTATA la camera è occupata", new StatoPrenotata().isOccupante());
        assertTrue("Durante il CHECK-IN la camera è ovviamente occupata", new StatoCheckIn().isOccupante());

        // Questi stati liberano la camera (restituiscono FALSE)
        assertFalse("Se CANCELLATA la camera deve tornare libera", new StatoCancellata().isOccupante());
        assertFalse("Se ELIMINATA la camera deve tornare libera", new StatoEliminata().isOccupante());
        assertFalse("Dopo il CHECK-OUT la camera torna disponibile", new StatoCheckOut().isOccupante());
    }

    @Test
    public void testRipristinoDateInStatoModificata() {
        // Setup di una prenotazione di test
        LocalDate dataOriginale = LocalDate.of(2025, 6, 1);
        Prenotazione p = new Prenotazione(1, dataOriginale, dataOriginale.plusDays(2), 1, 100.0f);

        // Simuliamo l'inizio della modifica salvando la data vecchia
        p.setDataInizioPrecedente(dataOriginale);
        p.setDataInizio(LocalDate.of(2025, 12, 25)); // Nuova data

        // Creiamo l'oggetto stato e forziamo il rifiuto
        StatoModificata sm = new StatoModificata();
        sm.esitoModifica(p, false); // approvata = false

        // VERIFICA: Il rollback deve aver funzionato
        assertEquals("La data di inizio deve essere tornata quella originale",
                dataOriginale, p.getDataInizio());
    }
}