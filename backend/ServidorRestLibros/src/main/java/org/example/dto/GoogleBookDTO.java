package org.example.dto;

import java.util.List;

public class GoogleBookDTO {
    private String googleBookId;
    private String isbn;
    private String titulo;
    private List<String> autores;
    private String editorial;
    private String fechaPublicacion;
    private String descripcion;
    private Integer numeroPaginas;
    private List<String> categorias;
    private String idioma;
    private String imagenPortada;
    private String imagenPequena;
    private Double puntuacionMedia;
    private Integer cantidadResenas;
    private String linkPreview;

    public GoogleBookDTO() {}

    public String getGoogleBookId() { return googleBookId; }
    public void setGoogleBookId(String googleBookId) { this.googleBookId = googleBookId; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public List<String> getAutores() { return autores; }
    public void setAutores(List<String> autores) { this.autores = autores; }
    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    public String getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(String fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public Integer getNumeroPaginas() { return numeroPaginas; }
    public void setNumeroPaginas(Integer numeroPaginas) { this.numeroPaginas = numeroPaginas; }
    public List<String> getCategorias() { return categorias; }
    public void setCategorias(List<String> categorias) { this.categorias = categorias; }
    public String getIdioma() { return idioma; }
    public void setIdioma(String idioma) { this.idioma = idioma; }
    public String getImagenPortada() { return imagenPortada; }
    public void setImagenPortada(String imagenPortada) { this.imagenPortada = imagenPortada; }
    public String getImagenPequena() { return imagenPequena; }
    public void setImagenPequena(String imagenPequena) { this.imagenPequena = imagenPequena; }
    public Double getPuntuacionMedia() { return puntuacionMedia; }
    public void setPuntuacionMedia(Double puntuacionMedia) { this.puntuacionMedia = puntuacionMedia; }
    public Integer getCantidadResenas() { return cantidadResenas; }
    public void setCantidadResenas(Integer cantidadResenas) { this.cantidadResenas = cantidadResenas; }
    public String getLinkPreview() { return linkPreview; }
    public void setLinkPreview(String linkPreview) { this.linkPreview = linkPreview; }
}
