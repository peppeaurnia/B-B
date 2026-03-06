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
public class Camera {

    private static int contatoreId = 1;

    private int idCamera;
    private String tipoCamera;
    private float prezzoPerNotte;
    private int capienza;
    private int quantita;

    public Camera(String tipoCamera, float prezzoPerNotte, int capienza, int quantitaDisponibile) {
        if (prezzoPerNotte <= 0) {
            throw new RuntimeException("Il prezzo deve essere maggiore di zero");
        }
        this.idCamera = contatoreId++;
        this.tipoCamera = tipoCamera;
        this.prezzoPerNotte = prezzoPerNotte;
        this.capienza = capienza;
        this.quantita = quantitaDisponibile;
    }


    public int getIdCamera() {
        return idCamera;
    }

    public String getTipoCamera() {
        return tipoCamera;
    }

    public int getCapienza() { return capienza; }

    public float getPrezzoPerNotte() {
        return prezzoPerNotte;
    }

    public int getQuantita() {
        return quantita;
    }
}