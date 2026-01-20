package org.example.repository;

import org.example.model.LibroLista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroListaRepository extends JpaRepository<LibroLista, Long> {
    List<LibroLista> findByListaIdLista(Long idLista);
    List<LibroLista> findByGoogleBookId(String googleBookId);
    List<LibroLista> findByPuntuacionGreaterThanEqual(Integer puntuacion);
}
