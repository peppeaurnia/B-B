package beb.facade;

import beb.dominio.Camera;
import beb.dominio.Prenotazione;

import java.lang.reflect.Field;
import java.time.LocalDate;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ValidazioneAvanzataTest {

    private BnB bnb;

    @Before
    public void setUp() {
        bnb = BnB.getInstance();
        bnb.aggiungiCameraSistema(new Camera("Singola", 50.0f, 1, 1));
        bnb.registraCliente("Test User", "test@test.it", "password");
    }


    @After
    public void tearDown() throws Exception {
        // Resetta l'istanza Singleton di BnB dopo ogni test
        Field instance = BnB.class.getDeclaredField("istanza");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test(expected = RuntimeException.class)
    public void testPrenotazioneNelPassato() {
        int idC = bnb.getIdCliente("test@test.it");

        // Tentativo di prenotare per l'anno scorso
        LocalDate inizioPassato = LocalDate.now().minusYears(1);
        LocalDate finePassato = LocalDate.now().minusYears(1).plusDays(5);

        // La logica in ServiziPrenotazioni o nel costruttore dovrebbe bloccare date passate
        bnb.richiediPrenotazione("Singola", inizioPassato, finePassato, idC);
    }

    @Test(expected = RuntimeException.class)
    public void testModificaSuDateGiaOccupate() {
        int idC = bnb.getIdCliente("test@test.it");

        // 1. Prima prenotazione (10-15)
        LocalDate in1 = LocalDate.now().plusDays(10);
        LocalDate fin1 = LocalDate.now().plusDays(15);
        Prenotazione p1 = bnb.richiediPrenotazione("Singola", in1, fin1, idC);
        bnb.aggiornaRichiesta(p1.getIdPrenotazione(), "APPROVATA");

        // CORREZIONE: Usiamo una carta valida da 16 cifre
        bnb.pagaRichiesta(p1.getIdPrenotazione(), "1111222233334444", 123);

        // 2. Seconda prenotazione (20-25)
        LocalDate in2 = LocalDate.now().plusDays(20);
        LocalDate fin2 = LocalDate.now().plusDays(25);
        Prenotazione p2 = bnb.richiediPrenotazione("Singola", in2, fin2, idC);

        // 3. TENTATIVO DI MODIFICA: p2 prova a sovrapporsi a p1
        // Ora l'eccezione sarà lanciata correttamente dalla logica di disponibilità
        bnb.richiestaModificaPrenotazione(p2.getIdPrenotazione(), in1, fin1);
    }
}