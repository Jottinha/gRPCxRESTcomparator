package com.compare.restxgrpc.services;

public class SyncSchedulerService {
    public static void main(String[] args) {
        OrchestratorService orchestratorService = new OrchestratorService("client2");
        orchestratorService.runComparison();
        orchestratorService.shutdown();
    }
}
