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
public class StatoPrenotata implements StatoPrenotazione {
    @Override
    public void richiediModifica(Prenotazione p) { p.setStatoObj(new StatoModificata()); }

    @Override
    public void cancellaCliente(Prenotazione p) { p.setStatoObj(new StatoCancellata()); }

    @Override
    public void checkIn(Prenotazione p) { p.setStatoObj(new StatoCheckIn()); }

    @Override
    public String getNomeStato() { return "PRENOTATA"; }

    @Override
    public boolean isOccupante() { return true; }
}
