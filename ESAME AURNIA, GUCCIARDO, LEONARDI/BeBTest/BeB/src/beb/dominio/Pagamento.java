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
import beb.dominio.Cliente;
import java.time.LocalDate;

public class Pagamento {

    private static int contatoreId = 1;

    private int idPagamento;
    private LocalDate dataPagamento;
    private int cvv;
    private String numeroCarta;
    private float totale;
    private Cliente cliente;
    private Prenotazione prenotazione;

    public Pagamento(int cvv, String numeroCarta, float totale,
                     Cliente cliente, Prenotazione prenotazione) {

        this.idPagamento = contatoreId++;
        this.dataPagamento = LocalDate.now();
        this.cvv = cvv;
        this.numeroCarta = numeroCarta;
        this.totale = totale;
        this.cliente = cliente;
        this.prenotazione = prenotazione;
    }

    public int getIdPagamento() {
        return idPagamento;
    }

    public Prenotazione getPrenotazione() {
        return prenotazione;
    }


}
