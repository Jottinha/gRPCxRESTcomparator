## 📊 Projeto: gRPCxRESTcomparator

Este projeto tem como objetivo comparar, de forma automática e agendada, o desempenho entre chamadas REST e gRPC para um mesmo provedor de dados. Ele simula um cenário real de empresas, onde a performance na comunicação entre serviços impacta diretamente nos resultados operacionais.

---

### 🚀 Objetivo

Fornecer uma base prática e comparativa entre REST e gRPC, demonstrando:

- Diferença no tempo de resposta
- Eficiência na troca de dados (payload)
- Arquitetura moderna com foco em mensuração

---

### 🔁 Como funciona o processo

1. O **Sync Scheduler Service** inicia o processo.
2. O **Orchestrator Service** executa, em paralelo:
   - Uma requisição REST
   - Uma requisição gRPC  
     Ambas direcionadas ao mesmo **Catalog Provider**.
3. O **Orchestrator Service** coleta os dados de resposta e os tempos de execução.
4. O resultado é exibido diretamente no terminal.

---

### 📊 Resultados Médios das Execuções (100 runs)

| Métrica                 | REST (JSON)             | gRPC (Protobuf)          | Observação                                                |
|-------------------------|-------------------------|---------------------------|------------------------------------------------------------|
| Tempo médio de resposta | 275 ms                  | 263 ms                    | gRPC foi ~4,5% mais rápido.                                |
| Tamanho do payload      | 5.467.145 bytes (~5,2MB) | 3.377.812 bytes (~3,2MB)  | gRPC usa ~38% menos banda, gerando economia significativa. |
| Estrutura da resposta   | Estruturalmente igual    | Estruturalmente igual     | Mesma informação retornada, formatos diferentes.           |

---
