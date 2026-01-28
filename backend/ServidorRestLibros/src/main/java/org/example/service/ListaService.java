package org.example.service;

import org.example.dto.ListaDTO;
import org.example.model.Lista;
import org.example.model.Usuario;
import org.example.repository.ListaRepository;
import org.example.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ListaService {

    @Autowired
    private ListaRepository listaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<ListaDTO> findAll() {
        return listaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ListaDTO> findById(Long id) {
        return listaRepository.findById(id).map(this::convertToDTO);
    }

    public List<ListaDTO> findByUsuarioId(Long idUsuario) {
        return listaRepository.findByUsuarioIdUsuario(idUsuario).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ListaDTO> findByCorreo(String correo){
        return listaRepository.findByCorreo(correo).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ListaDTO save(ListaDTO listaDTO) {
        Lista lista = convertToEntity(listaDTO);
        Lista savedLista = listaRepository.save(lista);
        return convertToDTO(savedLista);
    }

    public ListaDTO update(Long id, ListaDTO listaDTO) {
        return listaRepository.findById(id)
                .map(lista -> {
                    lista.setNombreLista(listaDTO.getNombreLista());
                    if (listaDTO.getIdUsuario() != null) {
                        usuarioRepository.findById(listaDTO.getIdUsuario())
                                .ifPresent(lista::setUsuario);
                    }
                    return convertToDTO(listaRepository.save(lista));
                })
                .orElse(null);
    }

    public void deleteById(Long id) {
        listaRepository.deleteById(id);
    }

    private ListaDTO convertToDTO(Lista lista) {
        return new ListaDTO(
                lista.getIdLista(),
                lista.getUsuario() != null ? lista.getUsuario().getIdUsuario() : null,
                lista.getNombreLista()
        );
    }

    private Lista convertToEntity(ListaDTO dto) {
        Lista lista = new Lista();
        lista.setIdLista(dto.getIdLista());
        lista.setNombreLista(dto.getNombreLista());
        if (dto.getIdUsuario() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getIdUsuario()).orElse(null);
            lista.setUsuario(usuario);
        }
        return lista;
    }
}
