package org.example.dto;

public class ListaDTO {
    private Long idLista;
    private Long idUsuario;
    private String nombreLista;

    public ListaDTO() {}

    public ListaDTO(Long idLista, Long idUsuario, String nombreLista) {
        this.idLista = idLista;
        this.idUsuario = idUsuario;
        this.nombreLista = nombreLista;
    }

    public Long getIdLista() { return idLista; }
    public void setIdLista(Long idLista) { this.idLista = idLista; }
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getNombreLista() { return nombreLista; }
    public void setNombreLista(String nombreLista) { this.nombreLista = nombreLista; }
}
