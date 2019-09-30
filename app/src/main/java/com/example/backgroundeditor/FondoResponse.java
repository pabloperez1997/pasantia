package com.example.backgroundeditor;

import java.util.List;

public class FondoResponse {

    private boolean error;
    private List<Fondo> fondos;

    public FondoResponse(boolean error, List<Fondo> fondos) {
        this.error = error;
        this.fondos = fondos;
    }

    public boolean isError() {
        return error;
    }

    public List<Fondo> getFondos() {
        return fondos;
    }
}
