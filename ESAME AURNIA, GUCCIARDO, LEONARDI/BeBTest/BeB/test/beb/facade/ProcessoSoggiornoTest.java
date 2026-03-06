package beb.facade;

import beb.dominio.Camera;
import beb.dominio.Prenotazione;

import java.lang.reflect.Field;
import java.time.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProcessoSoggiornoTest {

    private BnB bnb;

    @Before
    public void setUp() {
        bnb = BnB.getInstance();

        // Aggiungiamo una camera per i test
        bnb.aggiungiCameraSistema(new Camera("Suite", 150.0f, 4, 2));
        bnb.registraCliente("Luigi Bianchi", "luigi@email.it", "securePass");
    }

    @After
    public void tearDown() throws Exception {
        // Resetta l'istanza Singleton di BnB dopo ogni test
        Field instance = BnB.class.getDeclaredField("istanza");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testCicloDiVitaCompleto() {
        int idCliente = bnb.getIdCliente("luigi@email.it");
        LocalDate oggi = LocalDate.now();

        // 1. RICHIESTA: Il cliente richiede la Suite
        Prenotazione p = bnb.richiediPrenotazione("Suite", oggi, oggi.plusDays(2), idCliente);
        assertEquals("Lo stato iniziale deve essere ATTESA", "ATTESA", p.getStatoNome());

        // 2. APPROVAZIONE: Il gestore approva la richiesta
        bnb.aggiornaRichiesta(p.getIdPrenotazione(), "APPROVATA");
        assertEquals("Dopo l'approvazione lo stato deve essere APPROVATA", "APPROVATA", p.getStatoNome());

        // 3. PAGAMENTO: Il cliente paga
        // Usiamo dati validi: 16 cifre per la carta e CVV tra 100 e 999
        bnb.pagaRichiesta(p.getIdPrenotazione(), "1234567812345678", 123);
        assertEquals("Dopo il pagamento lo stato deve essere PRENOTATA", "PRENOTATA", p.getStatoNome());

        // 4. CHECK-IN: L'ospite arriva in hotel
        bnb.confermaCheckIn(p.getIdPrenotazione());
        assertEquals("Dopo il check-in lo stato deve essere CHECK-IN", "CHECK-IN", p.getStatoNome());
    }

    @Test(expected = RuntimeException.class)
    public void testCheckInImpossibileSenzaPagamento() {
        int idCliente = bnb.getIdCliente("luigi@email.it");

        // Creiamo una prenotazione e approviamola, ma NON paghiamola
        Prenotazione p = bnb.richiediPrenotazione("Suite", LocalDate.now(), LocalDate.now().plusDays(1), idCliente);
        bnb.aggiornaRichiesta(p.getIdPrenotazione(), "APPROVATA");

        // Tentare il check-in ora deve fallire (StatoApprovata non permette check-in)
        bnb.confermaCheckIn(p.getIdPrenotazione());
    }
}