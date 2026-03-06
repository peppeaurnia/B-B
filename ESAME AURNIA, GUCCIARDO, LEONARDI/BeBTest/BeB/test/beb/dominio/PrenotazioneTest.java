package beb.dominio;

import java.time.LocalDate;
import org.junit.Test;
import static org.junit.Assert.*;

public class PrenotazioneTest {

    @Test
    public void testCalcoloPrezzoSoggiorno() {
        // Definiamo le date: 3 notti (dal 10 al 13)
        LocalDate inizio = LocalDate.of(2025, 6, 10);
        LocalDate fine = LocalDate.of(2025, 6, 13);
        float prezzoNotte = 80.0f;

        // Creiamo la prenotazione
        Prenotazione p = new Prenotazione(1, inizio, fine, 1, prezzoNotte);

        // 3 notti * 80.0€ = 240.0€
        float atteso = 240.0f;

        // Sto usando il delta per dare un margine di errore, il calcolatore potrebbe calcolare un numero del tipo 240.000000004 e quidni confrontandolo con il prezzo totale l'assertequals fallirebbe
        //La prossima volta invece di float potrei usare il tipo BigDecimal per evitare di inserire il delta
        assertEquals("Il calcolo del prezzo totale non è corretto per 3 notti", atteso, p.getPrezzoTotale(), 0.001);
    }

    @Test
    public void testSoggiornoUnaSolaNotte() {
        LocalDate oggi = LocalDate.now();
        LocalDate domani = oggi.plusDays(1);
        float prezzoNotte = 100.0f;

        Prenotazione p = new Prenotazione(2, oggi, domani, 1, prezzoNotte);

        assertEquals("Il prezzo per una singola notte dovrebbe coincidere con il prezzo base",
                100.0f, p.getPrezzoTotale(), 0.001);
    }

    @Test(expected = RuntimeException.class)
    public void testDateInvertite() {
        // Caso: Check-out previsto PRIMA del Check-in
        LocalDate inizio = LocalDate.now().plusDays(10);
        LocalDate fine = LocalDate.now().plusDays(5); // Errore: 5 è prima di 10

        // Il costruttore deve intercettare l'errore e lanciare un'eccezione
        new Prenotazione(1, inizio, fine, 1, 100.0f);
    }

}