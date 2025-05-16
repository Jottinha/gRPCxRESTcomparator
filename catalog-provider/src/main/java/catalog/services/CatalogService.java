package catalog.services;

import catalog.models.Catalog;
import catalog.repository.CatalogRepository;
import org.springframework.stereotype.Service;

@Service
public class CatalogService {

    private final CatalogRepository catalogRepository;

    public CatalogService(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    public Catalog getCatalogByClient(String client) {
        return catalogRepository.findByClient(client);
    }
}
