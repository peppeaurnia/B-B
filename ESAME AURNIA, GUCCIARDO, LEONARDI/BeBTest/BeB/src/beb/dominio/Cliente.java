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
public class Cliente {

    private static int contatoreId = 1;

    private int idCliente;
    private String nomeCompleto;
    private String email;
    private String password;

    public Cliente(String nomeCompleto, String email, String password) {
        this.idCliente = contatoreId++;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.password = password;
    }

    public boolean autenticaCliente(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public int getIdCliente() {
        return idCliente;
    }

    public String getEmail() {
        return email;
    }
}