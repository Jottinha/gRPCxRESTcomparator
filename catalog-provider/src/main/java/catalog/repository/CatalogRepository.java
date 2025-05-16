package catalog.repository;

import catalog.models.Catalog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogRepository extends JpaRepository<Catalog, String> {
    Catalog findByClient(String client);
}
