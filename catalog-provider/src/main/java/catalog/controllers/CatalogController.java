package catalog.controllers;

import catalog.models.Catalog;
import catalog.services.CatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalogs")
public class CatalogController {

    @GetMapping("/{client}")
    public Catalog getCatalog(@PathVariable("client") String client){
        return new CatalogService().getCatalogByClient(client);
    }
}
