/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beb.servizi;

import beb.dominio.Camera;
import java.util.Map;
/**
 *
 * @author leona
 */
public class ServiziCamere {

    private Map<Integer, Camera> camere;

    public ServiziCamere(Map<Integer, Camera> camere) {
        this.camere = camere;
    }

    public Camera cercaCamera(String tipoCamera) {

        for (Camera c : camere.values()) {
            if (c.getTipoCamera().equalsIgnoreCase(tipoCamera))
                return c;
        }

        throw new RuntimeException("Camera non trovata");
    }

    public String getTipoCamera(int idCamera) {
        return camere.get(idCamera).getTipoCamera();
    }
}
