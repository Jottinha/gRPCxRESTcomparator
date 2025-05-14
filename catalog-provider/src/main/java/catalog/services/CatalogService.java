package catalog.services;

import catalog.models.Catalog;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CatalogService {

    private final ObjectMapper mapper = new ObjectMapper();

    public Catalog getCatalogByClient(String client){
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("catalogs/" + client + ".json")) {
            if (is == null) {
                throw new FileNotFoundException("Catálogo não encontrado para o cliente: " + client);
            }
            return mapper.readValue(is, Catalog.class);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao carregar catálogo", e);
        }
    }
}
