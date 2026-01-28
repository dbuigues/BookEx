package org.example.controller;

import org.example.dto.ListaDTO;
import org.example.service.ListaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listas")
public class ListaController {

    @Autowired
    private ListaService listaService;

    @GetMapping
    public ResponseEntity<List<ListaDTO>> getAllListas() {
        List<ListaDTO> listas = listaService.findAll();
        return ResponseEntity.ok(listas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListaDTO> getListaById(@PathVariable Long id) {
        return listaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ListaDTO>> getListasByUsuarioId(@PathVariable Long idUsuario) {
        List<ListaDTO> listas = listaService.findByUsuarioId(idUsuario);
        return ResponseEntity.ok(listas);
    }
    @GetMapping("/usuario/bycorreo/{correoUsuario}")
    public ResponseEntity<List<ListaDTO>> getListasByUsuarioId(@PathVariable String correoUsuario) {
        List<ListaDTO> listas = listaService.findByUsuarioCorreo(correoUsuario);
        return ResponseEntity.ok(listas);
    }


    @PostMapping
    public ResponseEntity<ListaDTO> createLista(@RequestBody ListaDTO listaDTO) {
        ListaDTO createdLista = listaService.save(listaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLista);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListaDTO> updateLista(@PathVariable Long id, @RequestBody ListaDTO listaDTO) {
        ListaDTO updatedLista = listaService.update(id, listaDTO);
        if (updatedLista != null) {
            return ResponseEntity.ok(updatedLista);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLista(@PathVariable Long id) {
        listaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
