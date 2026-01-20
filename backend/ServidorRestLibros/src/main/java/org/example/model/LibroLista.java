package org.example.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "libros_listas")
public class LibroLista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_lista", nullable = false)
    private Lista lista;

    @Column(name = "google_book_id", nullable = false)
    private String googleBookId;

    @Column(length = 2000)
    private String resena;

    private Integer puntuacion;

    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    public LibroLista() {}

    public LibroLista(Long id, Lista lista, String googleBookId, String resena, Integer puntuacion, LocalDate fechaPublicacion) {
        this.id = id;
        this.lista = lista;
        this.googleBookId = googleBookId;
        this.resena = resena;
        this.puntuacion = puntuacion;
        this.fechaPublicacion = fechaPublicacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Lista getLista() { return lista; }
    public void setLista(Lista lista) { this.lista = lista; }
    public String getGoogleBookId() { return googleBookId; }
    public void setGoogleBookId(String googleBookId) { this.googleBookId = googleBookId; }
    public String getResena() { return resena; }
    public void setResena(String resena) { this.resena = resena; }
    public Integer getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Integer puntuacion) { this.puntuacion = puntuacion; }
    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
}
