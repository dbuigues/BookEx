package org.example.dto;

import java.time.LocalDate;

public class LibroListaDTO {
    private Long id;
    private Long idLista;
    private String googleBookId;
    private String resena;
    private Integer puntuacion;
    private LocalDate fechaPublicacion;

    public LibroListaDTO() {}

    public LibroListaDTO(Long id, Long idLista, String googleBookId, String resena, Integer puntuacion, LocalDate fechaPublicacion) {
        this.id = id;
        this.idLista = idLista;
        this.googleBookId = googleBookId;
        this.resena = resena;
        this.puntuacion = puntuacion;
        this.fechaPublicacion = fechaPublicacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdLista() { return idLista; }
    public void setIdLista(Long idLista) { this.idLista = idLista; }
    public String getGoogleBookId() { return googleBookId; }
    public void setGoogleBookId(String googleBookId) { this.googleBookId = googleBookId; }
    public String getResena() { return resena; }
    public void setResena(String resena) { this.resena = resena; }
    public Integer getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Integer puntuacion) { this.puntuacion = puntuacion; }
    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
}
