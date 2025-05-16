package com.compare.restxgrpc.services;

public class SyncSchedulerService {
    public static void main(String[] args) {
        OrchestratorService orchestratorService = new OrchestratorService("client1");
        orchestratorService.runComparison();
        orchestratorService.shutdown();
    }
}
