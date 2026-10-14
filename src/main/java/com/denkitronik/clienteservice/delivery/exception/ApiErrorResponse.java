package com.denkitronik.clienteservice.delivery.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ApiErrorResponse {

    private int status;
    private String error;
    private String mensaje;
    private String timestamp; // Cambiado a String para cumplir exactamente con el contrato

    public ApiErrorResponse(int status, String error, String mensaje) {
        this.status = status;
        this.error = error;
        this.mensaje = mensaje;
        // Formatea la fecha y hora actual al estándar ISO-8601 requerido
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"));
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getTimestamp() {
        return timestamp;
    }
}