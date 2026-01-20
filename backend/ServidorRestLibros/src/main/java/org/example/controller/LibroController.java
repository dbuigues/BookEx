package org.example.controller;

import org.example.dto.GoogleBookDTO;
import org.example.service.GoogleBooksApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    @Autowired
    private GoogleBooksApiService googleBooksApiService;

    /**
     * Buscar libros en Google Books por término general
     * GET /api/libros/buscar?q=harry potter&maxResults=10
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<GoogleBookDTO>> buscarLibros(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int maxResults) {
        List<GoogleBookDTO> libros = googleBooksApiService.buscarLibros(q, maxResults);
        return ResponseEntity.ok(libros);
    }

    /**
     * Buscar libros por título
     * GET /api/libros/buscar/titulo?titulo=El Quijote
     */
    @GetMapping("/buscar/titulo")
    public ResponseEntity<List<GoogleBookDTO>> buscarPorTitulo(@RequestParam String titulo) {
        List<GoogleBookDTO> libros = googleBooksApiService.buscarPorTitulo(titulo);
        return ResponseEntity.ok(libros);
    }

    /**
     * Buscar libros por autor
     * GET /api/libros/buscar/autor?autor=Gabriel Garcia Marquez
     */
    @GetMapping("/buscar/autor")
    public ResponseEntity<List<GoogleBookDTO>> buscarPorAutor(@RequestParam String autor) {
        List<GoogleBookDTO> libros = googleBooksApiService.buscarPorAutor(autor);
        return ResponseEntity.ok(libros);
    }

    /**
     * Buscar libro por ISBN
     * GET /api/libros/buscar/isbn/9788420412146
     */
    @GetMapping("/buscar/isbn/{isbn}")
    public ResponseEntity<GoogleBookDTO> buscarPorIsbn(@PathVariable String isbn) {
        GoogleBookDTO libro = googleBooksApiService.buscarPorIsbn(isbn);
        if (libro != null) {
            return ResponseEntity.ok(libro);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Obtener libro por Google Book ID
     * GET /api/libros/{googleBookId}
     */
    @GetMapping("/{googleBookId}")
    public ResponseEntity<GoogleBookDTO> obtenerPorGoogleBookId(@PathVariable String googleBookId) {
        GoogleBookDTO libro = googleBooksApiService.obtenerPorGoogleBookId(googleBookId);
        if (libro != null) {
            return ResponseEntity.ok(libro);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Buscar libros por categoría/género
     * GET /api/libros/buscar/categoria?categoria=fiction
     */
    @GetMapping("/buscar/categoria")
    public ResponseEntity<List<GoogleBookDTO>> buscarPorCategoria(@RequestParam String categoria) {
        List<GoogleBookDTO> libros = googleBooksApiService.buscarPorCategoria(categoria);
        return ResponseEntity.ok(libros);
    }
}
