package org.example.repository;

import org.example.model.Lista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListaRepository extends JpaRepository<Lista, Long> {
    List<Lista> findByUsuarioIdUsuario(Long idUsuario);
}
