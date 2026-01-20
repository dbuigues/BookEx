package org.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "listas")
public class Lista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lista")
    private Long idLista;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "nombre_lista", nullable = false)
    private String nombreLista;

    @OneToMany(mappedBy = "lista", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<LibroLista> librosListas;

    public Lista() {}

    public Lista(Long idLista, Usuario usuario, String nombreLista, List<LibroLista> librosListas) {
        this.idLista = idLista;
        this.usuario = usuario;
        this.nombreLista = nombreLista;
        this.librosListas = librosListas;
    }

    public Long getIdLista() { return idLista; }
    public void setIdLista(Long idLista) { this.idLista = idLista; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getNombreLista() { return nombreLista; }
    public void setNombreLista(String nombreLista) { this.nombreLista = nombreLista; }
    public List<LibroLista> getLibrosListas() { return librosListas; }
    public void setLibrosListas(List<LibroLista> librosListas) { this.librosListas = librosListas; }
}
