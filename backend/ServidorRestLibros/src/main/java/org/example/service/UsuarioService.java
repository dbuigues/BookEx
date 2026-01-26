package org.example.service;

import org.example.dto.UsuarioDTO;
import org.example.model.Usuario;
import org.example.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    public String encrypt(String txtAEncriptar) throws NoSuchAlgorithmException {
        byte[] bytesTxt = txtAEncriptar.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] txtMD5 = md.digest(bytesTxt);
        return Base64.getEncoder().encodeToString(txtMD5);
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<UsuarioDTO> findAll() {
        return usuarioRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<UsuarioDTO> findById(Long id) {
        return usuarioRepository.findById(id).map(this::convertToDTO);
    }

    public Optional<UsuarioDTO> findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo).map(this::convertToDTO);
    }

    public UsuarioDTO save(UsuarioDTO usuarioDTO) throws NoSuchAlgorithmException {
        usuarioDTO.setContrasena(encrypt(usuarioDTO.getContrasena()));
        Usuario usuario = convertToEntity(usuarioDTO);
        Usuario savedUsuario = usuarioRepository.save(usuario);
        return convertToDTO(savedUsuario);
    }

    public UsuarioDTO update(Long id, UsuarioDTO usuarioDTO) throws NoSuchAlgorithmException {
        usuarioDTO.setContrasena(encrypt(usuarioDTO.getContrasena()));
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuario.setNombre(usuarioDTO.getNombre());
                    usuario.setCorreo(usuarioDTO.getCorreo());
                    usuario.setContrasena(usuarioDTO.getContrasena());
                    usuario.setFotoPerfil(usuarioDTO.getFotoPerfil());
                    return convertToDTO(usuarioRepository.save(usuario));
                })
                .orElse(null);
    }

    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    private UsuarioDTO convertToDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getContrasena(),
                usuario.getFotoPerfil()
        );
    }

    private Usuario convertToEntity(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(dto.getIdUsuario());
        usuario.setNombre(dto.getNombre());
        usuario.setCorreo(dto.getCorreo());
        usuario.setContrasena(dto.getContrasena());
        usuario.setFotoPerfil(dto.getFotoPerfil());
        return usuario;
    }
}
