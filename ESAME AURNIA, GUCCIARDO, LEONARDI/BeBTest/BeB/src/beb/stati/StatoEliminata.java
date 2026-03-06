/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beb.stati;

import beb.stati.StatoPrenotazione;

/**
 *
 * @author leona
 */
public class StatoEliminata implements StatoPrenotazione {

    @Override
    public String getNomeStato() {
        return "ELIMINATA";
    }
}
