# GerenciaSalas

API REST para cadastro e gerenciamento de reservas de salas desenvolvida como parte de um desafio técnico. O sistema centraliza o processo de reservas de salas de reunião e auditórios, eliminando conflitos de agenda e inconsistências causadas por controles manuais.

---

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.14 |
| Spring Data JPA | — |
| Spring Validation | — |
| H2 Database | — |
| Lombok | — |
| Maven | Wrapper incluso |

---

## Pré-requisitos

- Java 17 ou superior

Verifique sua instalação:
```bash
java -version
```

O projeto utiliza o **Maven Wrapper** (`mvnw`) — não é necessário instalar o Maven separadamente.

---

## Como executar

```bash
git clone https://github.com/victorhhugo128/processo-seletivo-project
cd gerencia-salas
./mvnw spring-boot:run
```

No Windows:
```bash
mvnw.cmd spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

---

## Acesso ao banco H2

O projeto utiliza o banco H2 em memória. Os dados são resetados a cada reinicialização da aplicação.

**Console:** `http://localhost:8080/h2-console`

| Campo | Valor |
|---|---|
| Driver Class | `org.h2.Driver` |
| JDBC URL | `jdbc:h2:mem:coworkingdb` |
| User Name | `sa` |
| Password | *(deixar em branco)* |

---

## Endpoints disponíveis

**Base URL:** `http://localhost:8080`

### Salas — `/api/v1/sala`

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/sala` | Cadastrar sala |
| `GET` | `/api/v1/sala` | Listar todas as salas |
| `GET` | `/api/v1/sala/livres?data=` | Listar salas livres em um dia |
| `GET` | `/api/v1/sala/livres?data=&horaInicio=&horaFim=` | Listar salas livres em um intervalo |
| `GET` | `/api/v1/sala/{id}/reservas?data=` | Listar reservas de uma sala em um dia |

### Reservas — `/api/v1/reserva`

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/reserva` | Criar reserva |
| `GET` | `/api/v1/reserva` | Listar todas as reservas |
| `DELETE` | `/api/v1/reserva/{id}` | Cancelar reserva |

---

## Exemplos de uso

### 1. Cadastrar Salas

Crie as três salas abaixo antes de testar os demais endpoints.

**POST** `/api/v1/sala`

```json
{
  "nome": "Sala Executiva",
  "tipoSala": "INDIVIDUAL"
}
```

```json
{
  "nome": "Sala de Reuniões",
  "tipoSala": "COLETIVA"
}
```

```json
{
  "nome": "Auditório Principal",
  "tipoSala": "AUDITORIO"
}
```

---

### 2. Criar Reservas

**POST** `/api/v1/reserva`

**Reserva 1** — Sala Executiva (id: 1), das 09:00 às 11:00
```json
{
  "salaId": 1,
  "data": "2025-06-10",
  "horaInicio": "09:00:00",
  "horaFim": "11:00:00"
}
```

**Reserva 2** — Sala de Reuniões (id: 2), das 14:00 às 16:00
```json
{
  "salaId": 2,
  "data": "2025-06-10",
  "horaInicio": "14:00:00",
  "horaFim": "16:00:00"
}
```

**Reserva 3** — Auditório Principal (id: 3), das 09:00 às 12:00
```json
{
  "salaId": 3,
  "data": "2025-06-10",
  "horaInicio": "09:00:00",
  "horaFim": "12:00:00"
}
```

**Reserva 4** — Sala de Reuniões (id: 2), das 10:00 às 12:00 no dia seguinte
```json
{
  "salaId": 2,
  "data": "2025-06-11",
  "horaInicio": "10:00:00",
  "horaFim": "12:00:00"
}
```

**Reserva 5 — ⚠️ Conflito esperado**

Tentativa de reservar a Sala Executiva (id: 1) das 10:00 às 12:00 no mesmo dia da Reserva 1. O horário 10:00–12:00 sobrepõe o intervalo já reservado 09:00–11:00.

Resposta esperada: **409 Conflict**
```json
{
  "salaId": 1,
  "data": "2025-06-10",
  "horaInicio": "10:00:00",
  "horaFim": "12:00:00"
}
```

```json
{
  "message": "Sala já reservada para o horário solicitado",
  "status": 409,
  "timestamp": "2025-06-10T08:00:00"
}
```

---

### 3. Listar Salas Livres em um Dia

Retorna salas sem nenhuma reserva ativa no dia informado.

**GET** `/api/v1/sala/livres?data=2025-06-10`

Resposta esperada — todas as salas têm reserva em 2025-06-10, nenhuma livre o dia inteiro:
```json
{
  "data": "2025-06-10",
  "salasLivres": []
}
```

**GET** `/api/v1/sala/livres?data=2025-06-12`

Resposta esperada — nenhuma reserva em 2025-06-12, todas livres:
```json
{
  "data": "2025-06-12",
  "salasLivres": [
    { "id": 1, "nome": "Sala Executiva", "tipo": "INDIVIDUAL" },
    { "id": 2, "nome": "Sala de Reuniões", "tipo": "COLETIVA" },
    { "id": 3, "nome": "Auditório Principal", "tipo": "AUDITORIO" }
  ]
}
```

---

### 4. Listar Salas Livres em um Intervalo

Retorna salas sem conflito no intervalo de horário informado.

**GET** `/api/v1/sala/livres?data=2025-06-10&horaInicio=13:00:00&horaFim=15:00:00`

Resposta esperada — Sala Executiva e Auditório livres nesse intervalo:
```json
{
  "data": "2025-06-10",
  "salasLivres": [
    { "id": 1, "nome": "Sala Executiva", "tipo": "INDIVIDUAL" },
    { "id": 3, "nome": "Auditório Principal", "tipo": "AUDITORIO" }
  ]
}
```

---

### 5. Listar Reservas de uma Sala em um Dia

**GET** `/api/v1/sala/2/reservas?data=2025-06-10`

```json
[
  {
    "id": 2,
    "nomeSala": "Sala de Reuniões",
    "tipo": "COLETIVA",
    "data": "2025-06-10",
    "horaInicio": "14:00:00",
    "horaFim": "16:00:00",
    "status": "ATIVO"
  }
]
```

---

### 6. Cancelar Reserva

**DELETE** `/api/v1/reserva/2`

Resposta esperada: **204 No Content**

Tentativa de cancelar a mesma reserva novamente retorna **400 Bad Request**:
```json
{
  "message": "Reserva já está cancelada.",
  "status": 400,
  "timestamp": "2025-06-10T08:00:00"
}
```

---

## Decisões técnicas

A especificação deixou alguns pontos em aberto que foram resolvidos da seguinte forma:

**Salas livres por intervalo de horário**
A especificação pede consulta de salas livres em um dado dia, porém as reservas são feitas por intervalo de horário. Para cobrir ambos os casos, os parâmetros `horaInicio` e `horaFim` foram tornados opcionais: sem eles o sistema retorna salas sem nenhuma reserva no dia inteiro; com eles retorna salas livres naquele intervalo específico.

**Cancelamento via soft delete**
Ao cancelar uma reserva, o registro não é removido do banco, apenas o status é alterado para `CANCELADO`. Essa decisão preserva o histórico de reservas e mantém a rastreabilidade dos dados, evitando perda de informação operacional.

**Nome padrão de sala**
O nome da sala é opcional no cadastro. Caso não seja informado, o sistema atribui automaticamente o nome `Sala {id}` após a persistência, quando o identificador já está disponível.

---

## Testes

Foram implementados testes unitários na camada de serviço cobrindo as principais regras de negócio. Como o desafio não exige autenticação, os testes focam na lógica de negócio — conflito de horário, cancelamento e consultas.

**ReservaService:**
- Lança exceção ao detectar conflito de horário
- Lança exceção quando a sala não existe
- Cria reserva com sucesso
- Lança exceção ao tentar cancelar reserva inexistente
- Lança exceção ao tentar cancelar reserva já cancelada
- Cancela reserva com sucesso
- Retorna lista vazia quando não há reservas no período

**SalaService:**
- Cria sala com nome fornecido
- Atribui nome padrão "Sala {id}" quando nome não é informado
- Retorna lista vazia quando não há salas cadastradas
- Retorna todas as salas quando nenhuma está ocupada
- Retorna apenas salas livres filtrando por intervalo de horário
- Retorna salas livres sem filtro de horário

Para executar os testes:
```bash
./mvnw test
```
