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
public class StatoApprovata implements StatoPrenotazione {
    @Override
    public void paga(Prenotazione p) { p.setStatoObj(new StatoPrenotata()); }

    @Override
    public String getNomeStato() { return "APPROVATA"; }

    @Override
    public boolean isOccupante() { return true; }
}
