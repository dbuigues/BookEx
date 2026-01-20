package org.example.service;

import org.example.dto.LibroListaDTO;
import org.example.model.LibroLista;
import org.example.model.Lista;
import org.example.repository.LibroListaRepository;
import org.example.repository.ListaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibroListaService {

    @Autowired
    private LibroListaRepository libroListaRepository;

    @Autowired
    private ListaRepository listaRepository;

    public List<LibroListaDTO> findAll() {
        return libroListaRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<LibroListaDTO> findById(Long id) {
        return libroListaRepository.findById(id).map(this::convertToDTO);
    }

    public List<LibroListaDTO> findByListaId(Long idLista) {
        return libroListaRepository.findByListaIdLista(idLista).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LibroListaDTO> findByGoogleBookId(String googleBookId) {
        return libroListaRepository.findByGoogleBookId(googleBookId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LibroListaDTO> findByPuntuacionMinima(Integer puntuacion) {
        return libroListaRepository.findByPuntuacionGreaterThanEqual(puntuacion).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public LibroListaDTO save(LibroListaDTO dto) {
        LibroLista libroLista = convertToEntity(dto);
        LibroLista savedLibroLista = libroListaRepository.save(libroLista);
        return convertToDTO(savedLibroLista);
    }

    public LibroListaDTO update(Long id, LibroListaDTO dto) {
        return libroListaRepository.findById(id)
                .map(libroLista -> {
                    libroLista.setResena(dto.getResena());
                    libroLista.setPuntuacion(dto.getPuntuacion());
                    libroLista.setFechaPublicacion(dto.getFechaPublicacion());
                    if (dto.getIdLista() != null) {
                        listaRepository.findById(dto.getIdLista())
                                .ifPresent(libroLista::setLista);
                    }
                    if (dto.getGoogleBookId() != null) {
                        libroLista.setGoogleBookId(dto.getGoogleBookId());
                    }
                    return convertToDTO(libroListaRepository.save(libroLista));
                })
                .orElse(null);
    }

    public void deleteById(Long id) {
        libroListaRepository.deleteById(id);
    }

    private LibroListaDTO convertToDTO(LibroLista libroLista) {
        return new LibroListaDTO(
                libroLista.getId(),
                libroLista.getLista() != null ? libroLista.getLista().getIdLista() : null,
                libroLista.getGoogleBookId(),
                libroLista.getResena(),
                libroLista.getPuntuacion(),
                libroLista.getFechaPublicacion()
        );
    }

    private LibroLista convertToEntity(LibroListaDTO dto) {
        LibroLista libroLista = new LibroLista();
        libroLista.setId(dto.getId());
        libroLista.setGoogleBookId(dto.getGoogleBookId());
        libroLista.setResena(dto.getResena());
        libroLista.setPuntuacion(dto.getPuntuacion());
        libroLista.setFechaPublicacion(dto.getFechaPublicacion());

        if (dto.getIdLista() != null) {
            Lista lista = listaRepository.findById(dto.getIdLista())
                    .orElseThrow(() -> new RuntimeException("Lista no encontrada con id: " + dto.getIdLista()));
            libroLista.setLista(lista);
        } else {
            throw new RuntimeException("El idLista es obligatorio");
        }


        return libroLista;
    }
}
