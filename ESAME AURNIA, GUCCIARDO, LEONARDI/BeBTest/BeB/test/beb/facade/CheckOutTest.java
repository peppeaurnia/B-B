package beb.facade;

import beb.dominio.Camera;
import beb.dominio.Prenotazione;

import java.lang.reflect.Field;
import java.time.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class CheckOutTest {

    private BnB bnb;

    @Before
    public void setUp() {
        bnb = BnB.getInstance();
        // Setup camera e cliente
        bnb.aggiungiCameraSistema(new Camera("Singola", 45.0f, 1, 10));
        bnb.registraCliente("Sara Blu", "sara@email.it", "password");
    }

    @After
    public void tearDown() throws Exception {
        // Resetta l'istanza Singleton di BnB dopo ogni test
        Field instance = BnB.class.getDeclaredField("istanza");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void testFlussoCheckOutCorretto() {
        int idC = bnb.getIdCliente("sara@email.it");
        LocalDate oggi = LocalDate.now();

        // 1. Portiamo la prenotazione fino allo stato CHECK-IN
        Prenotazione p = bnb.richiediPrenotazione("Singola", oggi, oggi.plusDays(1), idC);
        bnb.aggiornaRichiesta(p.getIdPrenotazione(), "APPROVATA");
        bnb.pagaRichiesta(p.getIdPrenotazione(), "1111222233334444", 123);
        bnb.confermaCheckIn(p.getIdPrenotazione());

        assertEquals("La prenotazione deve essere in CHECK-IN", "CHECK-IN", p.getStatoNome());

        // 2. Eseguiamo il Check-out
        bnb.confermaCheckOut(p.getIdPrenotazione());

        // 3. VERIFICA: Lo stato finale deve essere CHECK-OUT
        assertEquals("Lo stato finale deve essere CHECK-OUT", "CHECK-OUT", p.getStatoNome());
        assertFalse("Dopo il check-out la camera non deve risultare occupata", p.isOccupante());
    }

    @Test(expected = RuntimeException.class)
    public void testImpossibileCheckOutSenzaCheckIn() {
        int idC = bnb.getIdCliente("sara@email.it");

        // Prenotazione solo pagata (Stato: PRENOTATA), senza aver fatto il check-in
        Prenotazione p = bnb.richiediPrenotazione("Singola", LocalDate.now(), LocalDate.now().plusDays(1), idC);
        bnb.aggiornaRichiesta(p.getIdPrenotazione(), "APPROVATA");
        bnb.pagaRichiesta(p.getIdPrenotazione(), "1111222233334444", 123);

        // Tentare il check-out ora deve fallire (StatoPrenotata non lo permette)
        bnb.confermaCheckOut(p.getIdPrenotazione());
    }
}