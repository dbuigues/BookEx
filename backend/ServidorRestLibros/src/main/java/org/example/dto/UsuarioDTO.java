package org.example.dto;

public class UsuarioDTO {
    private Long idUsuario;
    private String nombre;
    private String correo;
    private String contrasena;
    private String fotoPerfil;

    public UsuarioDTO() {}

    public UsuarioDTO(Long idUsuario, String nombre, String correo, String contrasena, String fotoPerfil) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.fotoPerfil = fotoPerfil;
    }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
}
