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

import java.util.concurrent.TimeUnit;

public class OrchestratorService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ManagedChannel channel;
    private final CatalogServiceGrpc.CatalogServiceBlockingStub stub;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String clientNameJson;

    public OrchestratorService(String clientNameJson) {
        this.clientNameJson = clientNameJson;

        this.channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        this.stub = CatalogServiceGrpc.newBlockingStub(channel);
    }

    public String getClientNameJson() {
        return clientNameJson;
    }

    public void runComparison() {
        warmUp();

        System.out.println("Iniciando comparação REST x gRPC para o cliente: " + clientNameJson);

        int runs = 100;
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
        compareSizes();
    }

    public void shutdown() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }

    public long restTimeCall() {
        long startRest = System.nanoTime();
        String restUrl = "http://localhost:8080/catalogs/" + getClientNameJson();
        String restResponse = restTemplate.getForObject(restUrl, String.class);
        long endRest = System.nanoTime();
        return TimeUnit.NANOSECONDS.toMillis(endRest - startRest);
    }

    public long gRpcTimeCall() {
        long startGrpc = System.nanoTime();
        CatalogRequest request = CatalogRequest.newBuilder().setClient(getClientNameJson()).build();
        stub.getCatalog(request);
        long endGrpc = System.nanoTime();
        return TimeUnit.NANOSECONDS.toMillis(endGrpc - startGrpc);
    }

    public String restCallResponse() {
        String restUrl = "http://localhost:8080/catalogs/" + getClientNameJson();
        return restTemplate.getForObject(restUrl, String.class);
    }

    public CatalogResponse gRpcCallResponse() {
        CatalogRequest request = CatalogRequest.newBuilder().setClient(getClientNameJson()).build();
        return stub.getCatalog(request);
    }

    public void compareResponses() {
        String restResponse = restCallResponse();
        CatalogResponse grpcResponse = gRpcCallResponse();

        System.out.println("=== COMPARANDO CONTEÚDO DAS RESPOSTAS ===");

        try {
            JsonNode restJson = mapper.readTree(restResponse);

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
    public void compareSizes() {
        try {
            String restJson = restCallResponse();
            byte[] restBytes = restJson.getBytes("UTF-8");

            CatalogResponse grpcResp = gRpcCallResponse();
            byte[] grpcBytes = grpcResp.toByteArray();

            System.out.println("=== TAMANHO DOS PAYLOADS ===");
            System.out.println("REST (JSON): " + restBytes.length + " bytes");
            System.out.println("gRPC (Protobuf): " + grpcBytes.length + " bytes");
        } catch (Exception e) {
            System.out.println("Erro ao calcular tamanhos: " + e.getMessage());
        }
    }

    public void warmUp() {
        try {
            restTemplate.getForObject("http://localhost:8080/catalogs/" + getClientNameJson(), String.class);
        } catch (Exception ignored) {}

        ManagedChannel warmupChannel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext()
                .build();
        CatalogServiceGrpc.CatalogServiceBlockingStub warmupStub = CatalogServiceGrpc.newBlockingStub(warmupChannel);
        try {
            CatalogRequest warmupRequest = CatalogRequest.newBuilder().setClient(getClientNameJson()).build();
            warmupStub.getCatalog(warmupRequest);
        } catch (Exception ignored) {}
        warmupChannel.shutdown();
    }
}
