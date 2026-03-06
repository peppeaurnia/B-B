/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beb.facade;

import beb.servizi.ServiziCamere;
import beb.servizi.ServiziClienti;
import beb.servizi.ServiziPagamenti;
import beb.servizi.ServiziPrenotazioni;
import beb.dominio.*;
import java.time.LocalDate;
import java.util.*;
/**
 *
 * @author leona
 * Controller di sistema principale.
 * Implementa i pattern GoF Singleton (per garantire un unico punto d'accesso)
 * e Facade (per semplificare l'interfaccia verso i sottosistemi e nascondere
 * la complessità delle mappe e dei servizi).
 * In termini GRASP, agisce come Controller di Facciata delegando le
 * operazioni ai Pure Fabrication (i Servizi).
 */
public class BnB {

    private static BnB istanza;

    // Strutture dati (Information Hiding: nessun getter esposto all'esterno)
    private Map<Integer, Cliente> clienti = new HashMap<>();
    private Map<Integer, Camera> camere = new HashMap<>();
    private Map<Integer, Prenotazione> prenotazioni = new HashMap<>();
    private Map<Integer, Pagamento> pagamenti = new HashMap<>();
    private Map<Integer, Gestore> gestori = new HashMap<>();

    // Sottosistemi (Servizi / Pure Fabrication)
    private ServiziClienti serviziClienti;
    private ServiziCamere serviziCamere;
    private ServiziPrenotazioni serviziPrenotazioni;
    private ServiziPagamenti serviziPagamenti;

    // Costruttore privato (Singleton)
    private BnB() {
        serviziClienti = new ServiziClienti(clienti);
        serviziCamere = new ServiziCamere(camere);
        serviziPrenotazioni = new ServiziPrenotazioni(prenotazioni);
        serviziPagamenti = new ServiziPagamenti(pagamenti);
        // IL GESTORE DI SISTEMA (Pre-registrato)
        gestori.put(1, new Gestore("Admin", "admin@beb.it", "admin"));
    }

    public static BnB getInstance() {
        if (istanza == null) {
            istanza = new BnB();
        }
        return istanza;
    }

    // GESTIONE UTENTI E CAMERE

    public void registraCliente(String nome, String email, String password) {
        serviziClienti.registraCliente(nome, email, password);
    }

    public boolean autenticazioneCliente(String email, String password) {
        return serviziClienti.autenticaCliente(email, password);
    }

    public int getIdCliente(String email) {
        return serviziClienti.cercaCliente(email).getIdCliente();
    }

    public boolean autenticazioneGestore(String email, String password) {
        Gestore g = gestori.get(1); // Modello semplificato per il collaudo
        return g != null && g.autenticaGestore(email, password);
    }

    public void aggiungiCameraSistema(Camera c) {
        camere.put(c.getIdCamera(), c);
    }

    // LOGICA DI DISPONIBILITÀ (Facade verso Camere e Prenotazioni)

    public boolean verificaDisponibilita(String tipoCamera, LocalDate dataInizio, LocalDate dataFine) {
        Camera camera = serviziCamere.cercaCamera(tipoCamera);
        int idCamera = camera.getIdCamera();
        int quantitaTotale = camera.getQuantita();

        int prenotazioniAttive = serviziPrenotazioni.contaPrenotazioni(idCamera, dataInizio, dataFine);
        return prenotazioniAttive < quantitaTotale; //true se c'è ancora posto, la camera è disponibile
    }

    public boolean controllaCameraModificabile(String tipoCamera, LocalDate dataInizio, LocalDate dataFine) {
        return verificaDisponibilita(tipoCamera, dataInizio, dataFine);
    }

    // GESTIONE PRENOTAZIONI E TRANSIZIONI DI STATO

    public Prenotazione richiediPrenotazione(String tipoCamera, LocalDate dataInizio,
                                             LocalDate dataFine, int idCliente) {

        // Validazione temporale e di dominio
        if (dataInizio.isBefore(LocalDate.now()) || dataInizio.isEqual(dataFine) || dataInizio.isAfter(dataFine)) {
            throw new RuntimeException("Date non valide");
        }

        if (!verificaDisponibilita(tipoCamera, dataInizio, dataFine)) {
            throw new RuntimeException("Camera non disponibile per le date selezionate");
        }

        Camera camera = serviziCamere.cercaCamera(tipoCamera);

        return serviziPrenotazioni.richiediPrenotazioni(
                camera.getIdCamera(), dataInizio, dataFine, idCliente, camera.getPrezzoPerNotte()
        );
    }

    public void aggiornaRichiesta(int idPrenotazione, String stato) {
        serviziPrenotazioni.aggiornaRichiesta(idPrenotazione, stato);
    }

    /**
     * Pattern State (il metodo p.paga() gestisce internamente la transizione o l'errore).
     */
    public void pagaRichiesta(int idPrenotazione, String numCarta, int cvv) {
        Prenotazione p = serviziPrenotazioni.trovaPrenotazione(idPrenotazione);
        Cliente cliente = serviziClienti.cercaClientePerId(p.getIdCliente());

        Pagamento pagamento = serviziPagamenti.creaPagamento(
                cvv, numCarta, p.getPrezzoTotale(), cliente, p
        );

        p.paga();
    }

    public void richiestaModificaPrenotazione(int idPrenotazione, LocalDate nuovaDataInizio, LocalDate nuovaDataFine) {
        Prenotazione p = serviziPrenotazioni.trovaPrenotazione(idPrenotazione);

        if (!verificaDisponibilita(serviziCamere.getTipoCamera(p.getIdCamera()), nuovaDataInizio, nuovaDataFine)) {
            throw new RuntimeException("Nuove date non disponibili");
        }

        serviziPrenotazioni.richiestaModificaPrenotazione(idPrenotazione, nuovaDataInizio, nuovaDataFine);
    }

    public void aggiornaRichiestaModificata(int idPrenotazione, boolean approvata) {
        Prenotazione p = serviziPrenotazioni.trovaPrenotazione(idPrenotazione);
        // La logica di rollback delle date o conferma è delegata allo stato "StatoModificata"
        p.esitoModifica(approvata);
    }

    public void cancellaPrenotazioneCliente(int idPrenotazione) {
        serviziPrenotazioni.cancellaPrenotazioneCliente(idPrenotazione);
    }

    public void cancellaPrenotazioneGestore(int idPrenotazione) {
        serviziPrenotazioni.cancellaPrenotazioneGestore(idPrenotazione);
    }

    public void confermaCheckIn(int idPrenotazione) {
        serviziPrenotazioni.confermaCheckIn(idPrenotazione);
    }

    public void confermaCheckOut(int idPrenotazione) {
        serviziPrenotazioni.confermaCheckOut(idPrenotazione);
    }

    // METODI DI VISUALIZZAZIONE E RICERCA

    public List<Prenotazione> visualizzaRichieste() {
        return serviziPrenotazioni.tutteLePrenotazioni();
    }

    public List<Prenotazione> visualizzaTuttePrenotazioniCliente(String email) {
        int idCliente = getIdCliente(email);
        return serviziPrenotazioni.trovaTuttePrenotazioniCliente(idCliente);
    }

    public List<Prenotazione> cercaPrenotazioniInAttesa() {
        return serviziPrenotazioni.cercaPrenotazioniInAttesa();
    }

    public List<Prenotazione> visualizzaRichiesteApprovate(String email) {
        int idCliente = getIdCliente(email);
        return serviziPrenotazioni.cercaPrenotazioniApprovateCliente(idCliente);
    }

    public List<Prenotazione> visualizzaPrenotazioniPagateCliente(String email) {
        int idCliente = getIdCliente(email);
        return serviziPrenotazioni.trovaPrenotazioniPagateCliente(idCliente);
    }

    public List<Prenotazione> visualizzaRichiesteModificate() {
        return serviziPrenotazioni.cercaPrenotazioniModificate();
    }

    public List<Prenotazione> visualizzaPrenotazioniPagate() {
        return serviziPrenotazioni.visualizzaPrenotazioniPagate();
    }

    public List<Prenotazione> visualizzaCheckInDisponibili(String email) {
        int idCliente = getIdCliente(email);
        return serviziPrenotazioni.trovaCheckInDisponibili(idCliente);
    }

    public List<Prenotazione> visualizzaCheckOutDisponibili(String email) {
        int idCliente = getIdCliente(email);
        return serviziPrenotazioni.trovaCheckOutDisponibili(idCliente);
    }
}