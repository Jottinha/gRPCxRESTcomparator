package catalog;

import catalog.services.CatalogGrpcService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

//Just for test
public class CatalogGrpcServer {
    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.forPort(9090)
                .addService(new CatalogGrpcService())
                .build();

        server.start();
        System.out.println("gRPC Server started on port 9090");
        server.awaitTermination();
    }
}
