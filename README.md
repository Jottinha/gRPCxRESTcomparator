## 📊 Projeto: gRPCxRESTcomparator

Este projeto tem como objetivo comparar, de forma automática e agendada, o desempenho entre chamadas REST e gRPC a um mesmo provedor de dados. Ele simula um cenário real utilizado em empresas, onde a performance de comunicação entre serviços pode impactar diretamente nos resultados operacionais.

---

### 🚀 Objetivo

Fornecer uma base prática e comparativa entre REST e gRPC, demonstrando:

* Diferença de tempo de resposta
* Eficiência na troca de dados
* Arquitetura moderna com foco em mensuração

---

### 🧠 Visão Geral do Fluxo

```text
┌────────────┐
│ Scheduler  │ ← (Dispara diariamente, ex: 00:00)
└────┬───────┘
     ↓
┌─────────────────────────────┐
│ Sync Scheduler Service      │
│ - Inicia o processo         │
└────┬────────────────────────┘
     ↓
┌─────────────────────────────┐
│ Orchestrator Service        │
│ - Coordena as execuções     │
└────┬─────────────┬──────────┘
     ↓             ↓
┌────────────┐ ┌──────────────┐
│ REST Flow  │ │ gRPC Flow     │
│            │ │               │
│→ Chama     │ │→ Chama        │
│  Catalog   │ │  Catalog      │
│  via REST  │ │  via gRPC     │
└────┬───────┘ └────┬──────────┘
     ↓              ↓
┌─────────────────────────────┐
│ Orchestrator recebe os dados│
│ e os tempos de resposta     │
└────┬────────────────────────┘
     ↓
┌─────────────────────────────┐
│ Comparator                  │
│ - Compara tempos            │
│ - Gera resultado final JSON │
└────┬────────────────────────┘
     ↓
┌────────────────────┐
│ Report Storage     │
│ (ex: arquivo .json)│
└────────────────────┘
```

---

### 🔁 Como funciona o processo

1. **Agendamento diário** dispara o fluxo automaticamente.
2. O `Sync Scheduler Service` inicia o processo.
3. O `Orchestrator Service` executa, em paralelo:

    * Uma requisição REST
    * Uma requisição gRPC
      Ambas vão até o mesmo `Catalog Provider`.
4. O `Orchestrator` coleta os dados de resposta e os tempos.
5. Um `Comparator` analisa os tempos e gera um relatório `.json`.
6. O resultado fica salvo no `Report Storage`.

---

### 🛠️ Tecnologias sugeridas

* **Java** (REST e gRPC)
* **Docker** (execução facilitada por terceiros)
* **Scheduler (cron ou Spring Task)**
* **gRPC Java**
* **REST (Spring Boot)**
* **JSON como saída para análise**

---

### 📁 Exemplo de Saída (report.json)

```json
{
  "execution_date": "2025-05-11T00:00:05Z",
  "rest": {
    "duration_ms": 156,
    "status": "OK"
  },
  "grpc": {
    "duration_ms": 34,
    "status": "OK"
  },
  "winner": "gRPC"
}
