package com.compare.restxgrpc.services;

import catalog.grpc.CatalogRequest;
import catalog.grpc.CatalogResponse;
import catalog.grpc.CatalogServiceGrpc;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.util.JsonFormat;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.web.client.RestTemplate;

public class OrchestratorService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ManagedChannel channel;
    private final CatalogServiceGrpc.CatalogServiceBlockingStub stub;
    private String clientNameJson;

    public OrchestratorService(String clientNameJson) {
        // Cria o canal apenas uma vez no construtor
        this.channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        this.stub = CatalogServiceGrpc.newBlockingStub(channel);
        this.clientNameJson = "client1";
    }

    public String getClientNameJson() {
        return clientNameJson;
    }

    public void runComparison() {
        warmUp();

        System.out.println("Iniciando comparação REST x gRPC para o cliente: ");

        int runs = 10;
        long totalRestTime = 0;
        long totalGrpcTime = 0;

        for (int i = 0; i < runs; i++) {
            long restDurationMs = restTimeCall();
            long grpcDurationMs = gRpcTimeCall();

            System.out.println("Execução " + (i + 1) + ": REST = " + restDurationMs + " ms | gRPC = " + grpcDurationMs + " ms");

            totalRestTime += restDurationMs;
            totalGrpcTime += grpcDurationMs;
        }

        long avgRest = totalRestTime / runs;
        long avgGrpc = totalGrpcTime / runs;

        System.out.println("=== MÉDIA FINAL EM " + runs + " EXECUÇÕES ===");
        System.out.println("REST média: " + avgRest + " ms");
        System.out.println("gRPC média: " + avgGrpc + " ms");

        compareResponses();
    }

    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public long restTimeCall(){
        long startRest = System.nanoTime();
        String restUrl = "http://localhost:8080/catalogs/" + getClientNameJson();
        Object restResponse = restTemplate.getForObject(restUrl, Object.class);
        long endRest = System.nanoTime();
        return (endRest - startRest) / 1_000_000;
    }

    public long gRpcTimeCall(){
        long startGrpc = System.nanoTime();
        CatalogRequest request = CatalogRequest.newBuilder().setClient(getClientNameJson()).build();
        CatalogResponse grpcResponse = stub.getCatalog(request);
        long endGrpc = System.nanoTime();
        return (endGrpc - startGrpc) / 1_000_000;
    }

    public Object restCallResponse(){
        String restUrl = "http://localhost:8080/catalogs/" + getClientNameJson();
        return restTemplate.getForObject(restUrl, Object.class);
    }

    public CatalogResponse gRpcCallResponse(){
        CatalogRequest request = CatalogRequest.newBuilder().setClient(getClientNameJson()).build();
        return stub.getCatalog(request);
    }

    public void compareResponses() {
        Object restResponse = restCallResponse();
        CatalogResponse grpcResponse = gRpcCallResponse();

        System.out.println("=== COMPARANDO CONTEÚDO DAS RESPOSTAS ===");
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode restJson = mapper.valueToTree(restResponse);

            String grpcJsonString = JsonFormat.printer().print(grpcResponse);
            JsonNode grpcJson = mapper.readTree(grpcJsonString);

            if (restJson.equals(grpcJson)) {
                System.out.println("As respostas são ESTRUTURALMENTE IGUAIS.");
            } else {
                System.out.println("As respostas são DIFERENTES.");
                System.out.println("REST => " + restJson.toPrettyString());
                System.out.println("gRPC => " + grpcJson.toPrettyString());
            }
        } catch (Exception e) {
            System.out.println("Erro ao comparar as respostas: " + e.getMessage());
        }
    }

    public void warmUp(){
        // Warm-up (aquecimento) - chamadas sem medir tempo
        try {
            restTemplate.getForObject("http://localhost:8080/catalogs/" + getClientNameJson(), Object.class);
        } catch (Exception e) {}

        ManagedChannel warmupChannel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        CatalogServiceGrpc.CatalogServiceBlockingStub warmupStub = CatalogServiceGrpc.newBlockingStub(warmupChannel);
        try {
            CatalogRequest warmupRequest = CatalogRequest.newBuilder().setClient("Cliente XPTO").build();
            warmupStub.getCatalog(warmupRequest);
        } catch (Exception e) {}
        warmupChannel.shutdown();
    }
}
