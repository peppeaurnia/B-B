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
public class StatoCheckIn implements StatoPrenotazione {
    @Override
    public void checkOut(Prenotazione p) { p.setStatoObj(new StatoCheckOut()); }

    @Override
    public String getNomeStato() { return "CHECK-IN"; }

    @Override
    public boolean isOccupante() { return true; }
}