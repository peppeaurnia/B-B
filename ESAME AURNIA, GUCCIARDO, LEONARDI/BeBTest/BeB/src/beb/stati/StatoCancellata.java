/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beb.stati;


/**
 *
 * @author leona
 */
public class StatoCancellata implements StatoPrenotazione {

    @Override
    public String getNomeStato() {
        return "CANCELLATA";
    }
}
