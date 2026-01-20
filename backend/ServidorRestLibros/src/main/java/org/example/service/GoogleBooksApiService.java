package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.GoogleBookDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoogleBooksApiService {

    private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GoogleBooksApiService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Buscar libros por término de búsqueda general
     */
    public List<GoogleBookDTO> buscarLibros(String query) {
        return buscarLibros(query, 10);
    }

    /**
     * Buscar libros por término de búsqueda con límite de resultados
     */
    public List<GoogleBookDTO> buscarLibros(String query, int maxResults) {
        String url = GOOGLE_BOOKS_API_URL + "?q=" + query + "&maxResults=" + maxResults;
        return ejecutarBusqueda(url);
    }

    /**
     * Buscar libros por título
     */
    public List<GoogleBookDTO> buscarPorTitulo(String titulo) {
        String url = GOOGLE_BOOKS_API_URL + "?q=intitle:" + titulo + "&maxResults=10";
        return ejecutarBusqueda(url);
    }

    /**
     * Buscar libros por autor
     */
    public List<GoogleBookDTO> buscarPorAutor(String autor) {
        String url = GOOGLE_BOOKS_API_URL + "?q=inauthor:" + autor + "&maxResults=10";
        return ejecutarBusqueda(url);
    }

    /**
     * Buscar libro por ISBN
     */
    public GoogleBookDTO buscarPorIsbn(String isbn) {
        String url = GOOGLE_BOOKS_API_URL + "?q=isbn:" + isbn;
        List<GoogleBookDTO> resultados = ejecutarBusqueda(url);
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    /**
     * Obtener libro por Google Book ID
     */
    public GoogleBookDTO obtenerPorGoogleBookId(String googleBookId) {
        String url = GOOGLE_BOOKS_API_URL + "/" + googleBookId;
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            return mapearLibro(root);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Buscar libros por categoría/género
     */
    public List<GoogleBookDTO> buscarPorCategoria(String categoria) {
        String url = GOOGLE_BOOKS_API_URL + "?q=subject:" + categoria + "&maxResults=10";
        return ejecutarBusqueda(url);
    }

    private List<GoogleBookDTO> ejecutarBusqueda(String url) {
        List<GoogleBookDTO> libros = new ArrayList<>();
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.get("items");

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    GoogleBookDTO libro = mapearLibro(item);
                    if (libro != null) {
                        libros.add(libro);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return libros;
    }

    private GoogleBookDTO mapearLibro(JsonNode item) {
        try {
            GoogleBookDTO libro = new GoogleBookDTO();

            // ID de Google Books
            libro.setGoogleBookId(getTextValue(item, "id"));

            JsonNode volumeInfo = item.get("volumeInfo");
            if (volumeInfo == null) return null;

            // Información básica
            libro.setTitulo(getTextValue(volumeInfo, "title"));
            libro.setEditorial(getTextValue(volumeInfo, "publisher"));
            libro.setFechaPublicacion(getTextValue(volumeInfo, "publishedDate"));
            libro.setDescripcion(getTextValue(volumeInfo, "description"));
            libro.setIdioma(getTextValue(volumeInfo, "language"));
            libro.setLinkPreview(getTextValue(volumeInfo, "previewLink"));

            // Número de páginas
            JsonNode pageCount = volumeInfo.get("pageCount");
            if (pageCount != null && !pageCount.isNull()) {
                libro.setNumeroPaginas(pageCount.asInt());
            }

            // Autores
            JsonNode authors = volumeInfo.get("authors");
            if (authors != null && authors.isArray()) {
                List<String> autores = new ArrayList<>();
                for (JsonNode author : authors) {
                    autores.add(author.asText());
                }
                libro.setAutores(autores);
            }

            // Categorías
            JsonNode categories = volumeInfo.get("categories");
            if (categories != null && categories.isArray()) {
                List<String> categorias = new ArrayList<>();
                for (JsonNode category : categories) {
                    categorias.add(category.asText());
                }
                libro.setCategorias(categorias);
            }

            // ISBN
            JsonNode industryIdentifiers = volumeInfo.get("industryIdentifiers");
            if (industryIdentifiers != null && industryIdentifiers.isArray()) {
                for (JsonNode identifier : industryIdentifiers) {
                    String type = getTextValue(identifier, "type");
                    if ("ISBN_13".equals(type) || "ISBN_10".equals(type)) {
                        libro.setIsbn(getTextValue(identifier, "identifier"));
                        break;
                    }
                }
            }

            // Imágenes
            JsonNode imageLinks = volumeInfo.get("imageLinks");
            if (imageLinks != null) {
                libro.setImagenPortada(getTextValue(imageLinks, "thumbnail"));
                libro.setImagenPequena(getTextValue(imageLinks, "smallThumbnail"));
            }

            // Puntuación
            JsonNode averageRating = volumeInfo.get("averageRating");
            if (averageRating != null && !averageRating.isNull()) {
                libro.setPuntuacionMedia(averageRating.asDouble());
            }

            JsonNode ratingsCount = volumeInfo.get("ratingsCount");
            if (ratingsCount != null && !ratingsCount.isNull()) {
                libro.setCantidadResenas(ratingsCount.asInt());
            }

            return libro;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return (field != null && !field.isNull()) ? field.asText() : null;
    }
}
