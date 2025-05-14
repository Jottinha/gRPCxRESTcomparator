package catalog.services;

import catalog.grpc.CatalogRequest;
import catalog.grpc.CatalogResponse;
import catalog.grpc.CatalogServiceGrpc;
import catalog.grpc.Product;
import catalog.models.Catalog;
import io.grpc.stub.StreamObserver;

public class CatalogGrpcService extends CatalogServiceGrpc.CatalogServiceImplBase {

    private final CatalogService catalogService = new CatalogService();

    @Override
    public void getCatalog(CatalogRequest request, StreamObserver<CatalogResponse> responseObserver) {
        Catalog catalog = catalogService.getCatalogByClient(request.getClient());
        CatalogResponse.Builder responseBuilder = CatalogResponse.newBuilder()
                .setId(catalog.getId()).setClient(catalog.getClient());
        catalog.getProducts().forEach(p -> responseBuilder.addProducts(
                Product.newBuilder().setId(p.getId()).setName(p.getName()).setPrice(p.getPrice()).build()
        ));

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
