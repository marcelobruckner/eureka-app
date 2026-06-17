# eureka-app

Sistema web para gestao de tarefas escolares de alunos.

## O que o sistema faz

- Cadastro e login de alunos.
- Cadastro e consulta de anos letivos.
- Cadastro e consulta de disciplinas por ano letivo.
- Cadastro, consulta e conclusao de tarefas por disciplina.
- Identificacao automatica de tarefa entregue com atraso.
- Calculo de tarefas vencidas sem gravar `VENCIDA` no banco.
- Dashboard com tarefas exibidas dentro dos cards das disciplinas, com filtro de situacao e sem duplicacao de informacoes.

## Stack

- Java 21
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Security
- Thymeleaf
- Bootstrap via WebJar
- H2 para desenvolvimento local
- PostgreSQL para a evolucao futura

## Regras principais

- Login por `usuario` e senha.
- Senhas com BCrypt.
- E-mail e usuario unicos.
- Todo dado deve ser filtrado pelo aluno autenticado.
- Exclusao logica com `excluido`, `excluidoEm` e `excluidoPor`.
- `VENCIDA` e apenas uma situacao calculada.
- Timezone da aplicacao: `America/Sao_Paulo`.
- Scripts de banco sao versionados manualmente em `src/main/resources/db/scripts`.

## Como rodar

### Testes

```bash
./mvnw test
```

### Aplicacao

```bash
./mvnw spring-boot:run
```

## Estrutura

- `src/main/java/br/com/eureka/controller`: rotas web
- `src/main/java/br/com/eureka/service`: regras de negocio
- `src/main/java/br/com/eureka/repository`: acesso a dados
- `src/main/java/br/com/eureka/model`: entidades e enums
- `src/main/java/br/com/eureka/form`: objetos de entrada das telas
- `src/main/resources/templates`: telas Thymeleaf
- `src/main/resources/static`: recursos estaticos
- `src/main/resources/db/scripts`: scripts SQL versionados

## Status das fases

- Fase 1: Concluida
- Fase 2: Concluida
- Fase 3: Concluida
- Fase 4: Concluida
- Fase 5: Concluida
- Fase 6: Concluida
- Fase 7: Pendente
- Fase 8: Pendente

## Observacao

O arquivo de referencia principal do projeto e `AGENTS.md`, que contem as decisoes e o acompanhamento detalhado das fases.
