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
public class StatoModificata implements StatoPrenotazione {
    @Override
    public void esitoModifica(Prenotazione p, boolean approvata) {
        if (!approvata) {
            p.setDataInizio(p.getDataInizioPrecedente());
            p.setDataFine(p.getDataFinePrecedente());
        }
        p.setStatoObj(new StatoPrenotata());
    }

    @Override
    public String getNomeStato() { return "MODIFICATA"; }

    @Override
    public boolean isOccupante() { return true; }
}