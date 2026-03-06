/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beb.stati;

import beb.dominio.Prenotazione;

/**
 *
 * @author leona
 */
public interface StatoPrenotazione {

    // Transizioni di stato. Di default bloccano l'operazione.
    default void approva(Prenotazione p) { throw new RuntimeException("Azione non consentita: la prenotazione non è in attesa."); }
    default void paga(Prenotazione p) { throw new RuntimeException("Azione non consentita: la prenotazione non è approvata."); }
    default void richiediModifica(Prenotazione p) { throw new RuntimeException("Azione non consentita: non modificabile in questo stato."); }
    default void esitoModifica(Prenotazione p, boolean approvata) { throw new RuntimeException("Azione non consentita in questo stato."); }
    default void cancellaCliente(Prenotazione p) { throw new RuntimeException("Azione non consentita: impossibile cancellare."); }

    // Il gestore può sempre eliminare, quindi lo rendiamo default
    default void cancellaGestore(Prenotazione p) { p.setStatoObj(new StatoEliminata()); }

    default void checkIn(Prenotazione p) { throw new RuntimeException("Check-in non consentito in questo stato."); }
    default void checkOut(Prenotazione p) { throw new RuntimeException("Check-out non consentito in questo stato."); }

    // Ritorna la stringa per le visualizzazioni
    String getNomeStato();

    //Metodo polimorfico per sapere se la camera è occupata
    default boolean isOccupante() { return false; }
}
