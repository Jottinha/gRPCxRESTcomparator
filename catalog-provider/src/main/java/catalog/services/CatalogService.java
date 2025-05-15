package catalog.services;

import catalog.models.Catalog;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CatalogService {

    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, Catalog> catalogMap = new HashMap<>();

    public CatalogService() {
        loadCatalogs();
    }
    private void loadCatalogs() {
        String[] clients = {"client1", "client2", "client3"};

        for (String client : clients) {
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("catalogs/" + client + ".json")) {
                if (is != null) {
                    Catalog catalog = mapper.readValue(is, new TypeReference<>() {});
                    catalogMap.put(client, catalog);
                } else {
                    System.err.println("Arquivo não encontrado para cliente: " + client);
                }
            } catch (IOException e) {
                throw new RuntimeException("Erro ao carregar catálogo para o cliente: " + client, e);
            }
        }
    }
    public Catalog getCatalogByClient(String client) {
        return catalogMap.getOrDefault(client, new Catalog());
    }
}
