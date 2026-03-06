/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beb.dominio;

/**
 *
 * @author leona
 */
import beb.stati.StatoAttesa;
import beb.stati.StatoPrenotazione;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Entità di dominio che rappresenta una prenotazione nel sistema.
 * Implementa il ruolo di "Context" nel pattern GoF State, mantenendo
 * un riferimento allo stato corrente e delegando ad esso il comportamento.
 */
public class Prenotazione {

    private static int contatoreId = 1;

    private int idPrenotazione;
    private int idCamera;
    private int idCliente;

    private LocalDate dataInizio;
    private LocalDate dataFine;
    private LocalDate dataInizioPrecedente; // Storico per rollback modifiche
    private LocalDate dataFinePrecedente;

    private float prezzoTotale;

    // Riferimento polimorfico allo stato attuale
    private StatoPrenotazione statoCorrente;

    public Prenotazione(int idCamera, LocalDate dataInizio, LocalDate dataFine, int idCliente, float prezzoPerNotte) {

        if (dataFine.isBefore(dataInizio) || dataFine.isEqual(dataInizio)) {
            throw new RuntimeException("Errore: la data di fine deve essere successiva alla data di inizio.");
        }
        this.idPrenotazione = contatoreId++;
        this.idCamera = idCamera;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.idCliente = idCliente;

        // Lo stato iniziale di default
        this.statoCorrente = new StatoAttesa();

        // Information Expert: la classe calcola il prezzo avendo tutti i dati necessari
        this.prezzoTotale = calcolaPrezzoTotale(dataInizio, dataFine, prezzoPerNotte);
    }

    /**
     * Metodi delegati allo State Pattern.
     * La prenotazione non esegue logica condizionale (if/else), ma demanda
     * l'esecuzione o il blocco dell'azione al suo stato corrente.
     */
    public void approva() { statoCorrente.approva(this); }
    public void paga() { statoCorrente.paga(this); }
    public void richiediModifica() { statoCorrente.richiediModifica(this); }
    public void esitoModifica(boolean approvata) { statoCorrente.esitoModifica(this, approvata); }
    public void cancellaCliente() { statoCorrente.cancellaCliente(this); }
    public void cancellaGestore() { statoCorrente.cancellaGestore(this); }
    public void checkIn() { statoCorrente.checkIn(this); }
    public void checkOut() { statoCorrente.checkOut(this); }

    // Metodi di supporto e transizione di stato

    private float calcolaPrezzoTotale(LocalDate dataInizio, LocalDate dataFine, float prezzoPerNotte) {
        long notti = ChronoUnit.DAYS.between(dataInizio, dataFine);
        return notti * prezzoPerNotte;
    }

    // Usato dalle classi concrete di Stato per effettuare la transizione
    public void setStatoObj(StatoPrenotazione nuovoStato) {
        this.statoCorrente = nuovoStato;
    }

    public String getStatoNome() {
        return statoCorrente.getNomeStato();
    }

    public boolean isOccupante() {
        return statoCorrente.isOccupante();
    }

    // Getters e Setters

    public int getIdPrenotazione() { return idPrenotazione; }
    public int getIdCliente() { return idCliente; }
    public int getIdCamera() { return idCamera; }
    public float getPrezzoTotale() { return prezzoTotale; }

    public LocalDate getDataInizio() { return dataInizio; }
    public LocalDate getDataFine() { return dataFine; }
    public LocalDate getDataInizioPrecedente() { return dataInizioPrecedente; }
    public LocalDate getDataFinePrecedente() { return dataFinePrecedente; }

    public void setDataInizio(LocalDate dataInizio) { this.dataInizio = dataInizio; }
    public void setDataFine(LocalDate dataFine) { this.dataFine = dataFine; }
    public void setDataInizioPrecedente(LocalDate dataInizioPrecedente) { this.dataInizioPrecedente = dataInizioPrecedente; }
    public void setDataFinePrecedente(LocalDate dataFinePrecedente) { this.dataFinePrecedente = dataFinePrecedente; }
}