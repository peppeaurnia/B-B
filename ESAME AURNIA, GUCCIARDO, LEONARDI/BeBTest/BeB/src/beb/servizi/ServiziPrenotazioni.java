/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beb.servizi;

import beb.dominio.Prenotazione;
import java.time.LocalDate;
import java.util.*;
/**
 *
 * @author leona
 *
 * Classe di servizio che gestisce la logica legata alle prenotazioni.
 * Agisce come Information Expert per la mappa delle prenotazioni, occupandosi
 * di filtraggio, creazione e instradamento delle richieste di cambio stato,
 * mantenendo così alta la coesione del Controller principale (BnB).
 */
public class ServiziPrenotazioni {

    private Map<Integer, Prenotazione> prenotazioni;

    public ServiziPrenotazioni(Map<Integer, Prenotazione> prenotazioni) {
        this.prenotazioni = prenotazioni;
    }

    // CREAZIONE E RICERCA BASE (Information Expert & Creator)

    public Prenotazione richiediPrenotazioni(int idCamera, LocalDate dataInizio, LocalDate dataFine,
                                             int idCliente, float prezzoPerNotte) {
        Prenotazione p = new Prenotazione(idCamera, dataInizio, dataFine, idCliente, prezzoPerNotte);
        prenotazioni.put(p.getIdPrenotazione(), p);
        return p;
    }

    public Prenotazione trovaPrenotazione(int idPrenotazione) {
        Prenotazione p = prenotazioni.get(idPrenotazione);
        if (p == null) {
            throw new RuntimeException("Prenotazione non trovata");
        }
        return p;
    }

    public int contaPrenotazioni(int idCamera, LocalDate dataInizio, LocalDate dataFine) {
        int count = 0;
        for (Prenotazione p : prenotazioni.values()) {
            // La sovrapposizione esiste solo se l'inizio della nuova è PRIMA della fine della vecchia
            // e la fine della nuova è DOPO l'inizio della vecchia.
            // Il polimorfismo (isOccupante) discrimina le prenotazioni cancellate/rifiutate.
            if (p.getIdCamera() == idCamera &&
                    dataInizio.isBefore(p.getDataFine()) &&
                    dataFine.isAfter(p.getDataInizio()) &&
                    p.isOccupante()) {

                count++;
            }
        }
        return count;
    }

    // INTERROGAZIONI E FILTRI (Information Expert)

    public List<Prenotazione> tutteLePrenotazioni() {
        return new ArrayList<>(prenotazioni.values());
    }

    public List<Prenotazione> trovaTuttePrenotazioniCliente(int idCliente) {
        List<Prenotazione> lista = new ArrayList<>();
        for (Prenotazione p : prenotazioni.values()) {
            if (p.getIdCliente() == idCliente) {
                lista.add(p);
            }
        }
        return lista;
    }

    public List<Prenotazione> cercaPrenotazioniInAttesa() {
        List<Prenotazione> lista = new ArrayList<>();
        for (Prenotazione p : prenotazioni.values()) {
            if (p.getStatoNome().equals("ATTESA")) {
                lista.add(p);
            }
        }
        return lista;
    }

    public List<Prenotazione> cercaPrenotazioniApprovateCliente(int idCliente) {
        List<Prenotazione> lista = new ArrayList<>();
        for (Prenotazione p : prenotazioni.values()) {
            if (p.getIdCliente() == idCliente && p.getStatoNome().equals("APPROVATA")) {
                lista.add(p);
            }
        }
        return lista;
    }

    public List<Prenotazione> trovaPrenotazioniPagateCliente(int idCliente) {
        List<Prenotazione> lista = new ArrayList<>();
        for (Prenotazione p : prenotazioni.values()) {
            if (p.getIdCliente() == idCliente && p.getStatoNome().equals("PRENOTATA")) {
                lista.add(p);
            }
        }
        return lista;
    }

    public List<Prenotazione> cercaPrenotazioniModificate() {
        List<Prenotazione> lista = new ArrayList<>();
        for (Prenotazione p : prenotazioni.values()) {
            if (p.getStatoNome().equals("MODIFICATA")) {
                lista.add(p);
            }
        }
        return lista;
    }

    public List<Prenotazione> visualizzaPrenotazioniPagate() {
        List<Prenotazione> lista = new ArrayList<>();
        for (Prenotazione p : prenotazioni.values()) {
            if (p.getStatoNome().equals("PRENOTATA")) {
                lista.add(p);
            }
        }
        return lista;
    }

    public List<Prenotazione> trovaCheckInDisponibili(int idCliente) {
        List<Prenotazione> lista = new ArrayList<>();
        LocalDate oggi = LocalDate.now();

        for (Prenotazione p : prenotazioni.values()) {
            if (p.getIdCliente() == idCliente && verificaCheckIn(p, oggi)) {
                lista.add(p);
            }
        }
        return lista;
    }

    public List<Prenotazione> trovaCheckOutDisponibili(int idCliente) {
        List<Prenotazione> lista = new ArrayList<>();
        LocalDate oggi = LocalDate.now();

        for (Prenotazione p : prenotazioni.values()) {
            if (p.getIdCliente() == idCliente && verificaCheckOut(p, oggi)) {
                lista.add(p);
            }
        }
        return lista;
    }

    // TRANSIZIONI DI STATO (Delegation / Low Coupling)
    // Questi metodi smistano le richieste operative verso l'entità Prenotazione,
    // nascondendo i dettagli implementativi del pattern State al livello superiore.

    public void aggiornaRichiesta(int idPrenotazione, String stato) {
        Prenotazione p = trovaPrenotazione(idPrenotazione);

        if (stato.equalsIgnoreCase("APPROVATA")) {
            p.approva();
        } else if (stato.equalsIgnoreCase("CANCELLATA") || stato.equalsIgnoreCase("RIFIUTATA")) {
            p.cancellaGestore();
        } else {
            throw new RuntimeException("Azione non supportata tramite stringa");
        }
    }

    public void richiestaModificaPrenotazione(int idPrenotazione, LocalDate nuovaDataInizio, LocalDate nuovaDataFine) {
        Prenotazione p = trovaPrenotazione(idPrenotazione);
        p.richiediModifica();

        // Salvataggio storico date per eventuale rollback
        p.setDataInizioPrecedente(p.getDataInizio());
        p.setDataFinePrecedente(p.getDataFine());
        p.setDataInizio(nuovaDataInizio);
        p.setDataFine(nuovaDataFine);
    }

    public void cancellaPrenotazioneCliente(int idPrenotazione) {
        Prenotazione p = trovaPrenotazione(idPrenotazione);
        p.cancellaCliente();
        prenotazioni.put(idPrenotazione, p);
    }

    public void cancellaPrenotazioneGestore(int idPrenotazione) {
        Prenotazione p = trovaPrenotazione(idPrenotazione);
        p.cancellaGestore();
        prenotazioni.put(idPrenotazione, p);
    }

    public void confermaCheckIn(int idPrenotazione) {
        Prenotazione p = trovaPrenotazione(idPrenotazione);
        p.checkIn();
        prenotazioni.put(idPrenotazione, p);
    }

    public void confermaCheckOut(int idPrenotazione) {
        Prenotazione p = trovaPrenotazione(idPrenotazione);
        p.checkOut();
        prenotazioni.put(idPrenotazione, p);
    }

    // METODI HELPER PRIVATI

    private boolean verificaCheckIn(Prenotazione p, LocalDate oggi) {
        LocalDate limite = oggi.plusDays(1); // Finestra di 24 ore
        return p.getStatoNome().equals("PRENOTATA") &&
                !p.getDataInizio().isBefore(oggi) &&
                !p.getDataInizio().isAfter(limite);
    }

    private boolean verificaCheckOut(Prenotazione p, LocalDate oggi) {
        return p.getStatoNome().equals("CHECK-IN") &&
                p.getDataFine().isEqual(oggi);
    }
}