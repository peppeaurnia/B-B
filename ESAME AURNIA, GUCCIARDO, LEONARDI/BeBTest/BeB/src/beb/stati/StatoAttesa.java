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
public class StatoAttesa implements StatoPrenotazione {
    @Override
    public void approva(Prenotazione p) { p.setStatoObj(new StatoApprovata()); }

    @Override
    public String getNomeStato() { return "ATTESA"; }

    @Override
    public boolean isOccupante() {
        return true;
    }
}
