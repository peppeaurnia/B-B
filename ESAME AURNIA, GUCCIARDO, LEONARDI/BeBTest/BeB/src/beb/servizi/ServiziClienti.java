/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beb.servizi;

import beb.dominio.Cliente;
import java.util.Map;
/**
 *
 * @author leona
 */
public class ServiziClienti {

    private Map<Integer, Cliente> clienti;

    public ServiziClienti(Map<Integer, Cliente> clienti) {
        this.clienti = clienti;
    }

    public void registraCliente(String nome, String email, String password) {
        Cliente c = new Cliente(nome, email, password);
        clienti.put(c.getIdCliente(), c);
    }

    public boolean autenticaCliente(String email, String password) {

        for (Cliente c : clienti.values()) {
            if (c.autenticaCliente(email, password)) {
                return true;
            }
        }

        return false;
    }

    public Cliente cercaCliente(String email) {

        for (Cliente c : clienti.values()) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                return c;
            }
        }

        throw new RuntimeException("Cliente non trovato");
    }

    public Cliente cercaClientePerId(int idCliente) {
        Cliente c = clienti.get(idCliente);
        if (c == null) {
            throw new RuntimeException("Cliente non trovato");
        }
        return c;
    }
}