package catalog.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "catalog")
public class Catalog {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "client")
    private String client;

    @OneToMany(mappedBy = "catalog", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Product> products;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
