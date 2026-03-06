/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beb.servizi;

import beb.dominio.Prenotazione;
import beb.dominio.Pagamento;
import beb.dominio.Cliente;
import java.util.Map;
/**
 *
 * @author leona
 */
public class ServiziPagamenti {

    private Map<Integer, Pagamento> pagamenti;

    public ServiziPagamenti(Map<Integer, Pagamento> pagamenti) {
        this.pagamenti = pagamenti;
    }

    public Pagamento creaPagamento(int cvv,
                                   String numeroCarta,
                                   float totale,
                                   Cliente cliente,
                                   Prenotazione prenotazione) {

        if (numeroCarta.length() != 16 || cvv < 100 || cvv > 999)
            throw new RuntimeException("Dati carta non validi");

        Pagamento pagamento = new Pagamento(
                cvv, numeroCarta, totale, cliente, prenotazione
        );

        pagamenti.put(pagamento.getIdPagamento(), pagamento);

        return pagamento;
    }
}
