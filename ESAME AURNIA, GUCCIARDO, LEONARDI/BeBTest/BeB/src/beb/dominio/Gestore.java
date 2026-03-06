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
public class Gestore {

    private static int contatoreId = 1;

    private int idGestore;
    private String nomeCompleto;
    private String email;
    private String password;

    public Gestore(String nomeCompleto, String email, String password) {
        this.idGestore = contatoreId++;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.password = password;
    }

    public boolean autenticaGestore(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }
}
