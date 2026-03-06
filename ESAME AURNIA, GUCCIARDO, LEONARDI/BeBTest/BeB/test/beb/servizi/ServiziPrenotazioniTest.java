package beb.servizi;

import beb.dominio.Prenotazione;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

public class ServiziPrenotazioniTest {

    @Test
    public void testSovrapposizioneDate() {
        Map<Integer, Prenotazione> mappa = new HashMap<>();
        ServiziPrenotazioni sp = new ServiziPrenotazioni(mappa);

        // 1. Creiamo una prenotazione esistente: dal 10 al 15 del mese
        LocalDate inizioEsistente = LocalDate.of(2025, 6, 10);
        LocalDate fineEsistente = LocalDate.of(2025, 6, 15);

        // ID Camera = 1, ID Cliente = 1, Prezzo = 50
        sp.richiediPrenotazioni(1, inizioEsistente, fineEsistente, 1, 50.0f);

        // CASO A: Nuova prenotazione che finisce quando l'altra inizia (5-10)
        // NON deve esserci sovrapposizione
        int countA = sp.contaPrenotazioni(1, LocalDate.of(2025, 6, 5), LocalDate.of(2025, 6, 10));
        assertEquals("Non dovrebbe esserci sovrapposizione se una finisce quando l'altra inizia", 0, countA);

        // CASO B: Nuova prenotazione che inizia quando l'altra finisce (15-20)
        // NON deve esserci sovrapposizione
        int countB = sp.contaPrenotazioni(1, LocalDate.of(2025, 6, 15), LocalDate.of(2025, 6, 20));
        assertEquals("Non dovrebbe esserci sovrapposizione se una inizia quando l'altra finisce", 0, countB);

        // CASO C: Nuova prenotazione parzialmente sovrapposta (14-18)
        // DEVE esserci sovrapposizione
        int countC = sp.contaPrenotazioni(1, LocalDate.of(2025, 6, 14), LocalDate.of(2025, 6, 18));
        assertEquals("Dovrebbe rilevare una sovrapposizione parziale", 1, countC);

        // CASO D: Nuova prenotazione totalmente inclusa (11-14)
        // DEVE esserci sovrapposizione
        int countD = sp.contaPrenotazioni(1, LocalDate.of(2025, 6, 11), LocalDate.of(2025, 6, 14));
        assertEquals("Dovrebbe rilevare una sovrapposizione totale interna", 1, countD);
    }
}