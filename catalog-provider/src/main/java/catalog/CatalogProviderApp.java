package catalog;

import catalog.models.Catalog;
import catalog.services.CatalogService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CatalogProviderApp {
    public static void main(String[] args) {
        SpringApplication.run(CatalogProviderApp.class, args);
    }
}
