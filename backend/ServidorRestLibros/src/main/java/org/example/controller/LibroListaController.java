package org.example.controller;

import org.example.dto.LibroListaDTO;
import org.example.service.LibroListaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros-listas")
public class LibroListaController {

    @Autowired
    private LibroListaService libroListaService;

    @GetMapping
    public ResponseEntity<List<LibroListaDTO>> getAllLibrosListas() {
        List<LibroListaDTO> librosListas = libroListaService.findAll();
        return ResponseEntity.ok(librosListas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibroListaDTO> getLibroListaById(@PathVariable Long id) {
        return libroListaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/lista/{idLista}")
    public ResponseEntity<List<LibroListaDTO>> getLibrosListasByListaId(@PathVariable Long idLista) {
        List<LibroListaDTO> librosListas = libroListaService.findByListaId(idLista);
        return ResponseEntity.ok(librosListas);
    }

    @GetMapping("/puntuacion/{puntuacion}")
    public ResponseEntity<List<LibroListaDTO>> getLibrosListasByPuntuacionMinima(@PathVariable Integer puntuacion) {
        List<LibroListaDTO> librosListas = libroListaService.findByPuntuacionMinima(puntuacion);
        return ResponseEntity.ok(librosListas);
    }

    @PostMapping
    public ResponseEntity<LibroListaDTO> createLibroLista(@RequestBody LibroListaDTO libroListaDTO) {
        LibroListaDTO createdLibroLista = libroListaService.save(libroListaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLibroLista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LibroListaDTO> updateLibroLista(@PathVariable Long id, @RequestBody LibroListaDTO libroListaDTO) {
        LibroListaDTO updatedLibroLista = libroListaService.update(id, libroListaDTO);
        if (updatedLibroLista != null) {
            return ResponseEntity.ok(updatedLibroLista);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibroLista(@PathVariable Long id) {
        libroListaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
