# AGENTS.md

## Visao do produto

Este projeto e um sistema web para gestao de tarefas escolares de alunos.

O sistema deve permitir que um aluno se cadastre, faca login e acompanhe suas disciplinas por ano letivo. Cada disciplina possui tarefas, e cada tarefa deve ter datas de criacao, previsao de entrega e, quando concluida, data real de entrega. Tarefas entregues apos a data prevista devem ser sinalizadas como `ENTREGUE_COM_ATRASO`. Tarefas pendentes com data prevista ultrapassada devem aparecer como vencidas de forma calculada, sem gravar `VENCIDA` como status no banco.

## Escopo funcional inicial

- Cadastro de alunos.
- Login de alunos.
- Cadastro e consulta de anos letivos.
- Cadastro e consulta de disciplinas por ano letivo.
- Cadastro e consulta de tarefas por disciplina.
- Encerramento/conclusao de tarefas com data de entrega.
- Identificacao automatica de tarefa entregue com atraso quando a data de entrega for posterior a data prevista.
- Dashboard inicial apos login com anos letivos, disciplinas, tarefas e status.

## Decisoes assumidas

- Nome do projeto: `eureka-app`.
- Build tool: Maven.
- Scripts de banco: SQL versionado, sem Flyway inicialmente.
- Login: usuario e senha.
- Senhas devem ser criptografadas com BCrypt.
- E-mail de aluno deve ser unico.
- Todo dado deve ser sempre filtrado pelo aluno autenticado.
- Exclusao logica deve usar `excluido`, `excluidoEm` e `excluidoPor`.
- `VENCIDA` nao deve ser gravado no banco; deve ser calculado quando a tarefa estiver `PENDENTE` e a data prevista ja passou.
- Exclusoes devem ser logicas.
- Bootstrap deve ser usado via WebJar gerenciado pelo Maven.
- Frontend: Thymeleaf com Bootstrap.
- Timezone da aplicacao: `America/Sao_Paulo`.
- Cada fase so deve ser considerada concluida com testes passando.
- Mensagens de commit devem ser escritas em portugues.

## Plano de implementacao

Use este plano como acompanhamento incremental do projeto. Ao concluir uma fase, atualizar seu status de `PENDENTE` para `CONCLUIDA`. Se uma fase estiver parcialmente feita, manter como `EM ANDAMENTO` e registrar o que falta.

### Fase 1: Base do projeto

Status: `CONCLUIDA`

- Criar projeto Java 21 com Spring Boot, Maven, Thymeleaf, Bootstrap, Spring Web, Spring Data JPA, Spring Security e H2.
- Configurar perfil local com H2.
- Criar estrutura inicial de pacotes.
- Adicionar estrutura inicial para scripts SQL versionados.
- Validado com `./mvnw test`.

### Fase 2: Modelo de dominio

Status: `CONCLUIDA`

- Criar entidades `Aluno`, `AnoLetivo`, `Disciplina` e `Tarefa`.
- Criar enum de status da tarefa.
- Criar representacao calculada para situacao de prazo.
- Implementar regras de prazo e entrega.
- Criar scripts SQL versionados para o schema inicial, incluindo constraints e indices.
- Adicionar testes da regra de status.

### Fase 3: Cadastro e login

Status: `CONCLUIDA`

- Implementar cadastro de aluno.
- Implementar login com Spring Security.
- Proteger paginas internas.
- Garantir que cada aluno veja apenas seus proprios dados.

### Fase 4: Ano letivo e disciplinas

Status: `CONCLUIDA`

- Criar telas para cadastrar e listar anos letivos.
- Criar telas para cadastrar e listar disciplinas por ano letivo.
- Validar vinculos entre aluno, ano letivo e disciplina.
- Ajustar dashboard para exibir anos e disciplinas.

### Fase 5: Tarefas

Status: `CONCLUIDA`

- Criar tarefas por disciplina.
- Listar tarefas com status.
- Concluir tarefa informando data de entrega.
- Marcar automaticamente como `ENTREGUE` ou `ENTREGUE_COM_ATRASO`.
- Destacar tarefas pendentes vencidas de forma calculada no dashboard.
- Bloquear reabertura de tarefas entregues no MVP.
- Testar os principais cenarios de prazo.

### Fase 6: Dashboard interativo

Status: `PENDENTE`

- Melhorar a tela inicial apos login.
- Agrupar tarefas por disciplina.
- Adicionar filtro unico na tela para situacao da tarefa, separando internamente `status` persistido e situacao de prazo calculada.
- Destacar visualmente pendentes, vencidas, entregues e entregues com atraso.

### Fase 7: Preparacao para PostgreSQL

Status: `PENDENTE`

- Revisar scripts SQL versionados para compatibilidade com PostgreSQL.
- Criar perfil/configuracao para PostgreSQL.
- Garantir que o sistema nao dependa de `ddl-auto` fora do H2.
- Testar criacao do banco usando apenas scripts SQL versionados.

### Fase 8: Refinamento

Status: `PENDENTE`

- Melhorar validacoes de formulario.
- Melhorar mensagens de erro.
- Ajustar layout Thymeleaf.
- Adicionar testes de service/controller onde fizer sentido.

## Modelo de dominio esperado

- `Aluno`
  - nome
  - email
  - usuario
  - senha
- `AnoLetivo`
  - ano
  - aluno
- `Disciplina`
  - nome
  - ano letivo
- `Tarefa`
  - nome
  - data de criacao
  - data prevista de entrega
  - data de entrega
  - status
  - disciplina

Entidades com exclusao logica devem possuir:

- `excluido`
- `excluidoEm`
- `excluidoPor`

## Status de tarefa

Use status explicitos e simples:

- `PENDENTE`: tarefa criada, ainda nao entregue.
- `ENTREGUE`: tarefa entregue dentro do prazo.
- `ENTREGUE_COM_ATRASO`: tarefa entregue depois da data prevista.

As regras de prazo e entrega devem ficar no dominio ou na camada de servico, nao apenas na tela. `VENCIDA` deve ser uma situacao calculada para tarefas `PENDENTE` com data prevista anterior a data atual da aplicacao em `America/Sao_Paulo`, sem persistir esse valor como status.

## Regras de datas

- Data de criacao da tarefa deve ser automatica pelo sistema.
- Data prevista de entrega nao pode ser anterior a data de criacao.
- Data de entrega nao pode ser anterior a data de criacao.
- Tarefas entregues nao podem ser reabertas no MVP.
- Usar `America/Sao_Paulo` como timezone da aplicacao.

## Stack tecnica

- Java 21.
- Spring Boot na versao estavel mais recente disponivel no momento da criacao do projeto.
- Spring MVC para as rotas web.
- Thymeleaf para as telas.
- Bootstrap para a interface web, usando WebJar gerenciado pelo Maven.
- Spring Data JPA para persistencia.
- Spring Security para login, cadastro e protecao das paginas internas.
- H2 para validacao inicial da ideia.
- PostgreSQL futuramente, mantendo a camada de persistencia preparada para troca de banco.

## Arquitetura preferida

Organize o codigo por responsabilidade:

- `controller`: rotas web e integracao com templates.
- `service`: regras de negocio e casos de uso.
- `repository`: acesso a dados via Spring Data JPA.
- `model` ou `domain`: entidades e enums do dominio.
- `form`: objetos de entrada das telas Thymeleaf. Nao fazer bind direto de formularios em entidades JPA.

Evite colocar regra de negocio diretamente em controllers ou templates Thymeleaf.

## Regras de implementacao

- Use nomes em portugues no dominio quando isso deixar o codigo mais alinhado ao produto.
- Prefira codigo simples e direto.
- Mantenha alteracoes pequenas e focadas na tarefa solicitada.
- Nao adicione dependencias novas sem uma justificativa clara.
- Nao implemente APIs REST se a tarefa for apenas fluxo web com Thymeleaf, salvo quando solicitado.
- Nao misture logica de autenticacao com regras de tarefas escolares.
- Nao fazer refactors amplos sem necessidade.
- Permitir criar, editar e excluir logicamente anos letivos, disciplinas e tarefas.
- Usar objetos `form` para entrada de dados em telas Thymeleaf.
- Nao fazer bind direto de formularios em entidades JPA.
- Usar Bean Validation para campos obrigatorios e regras simples de formulario.
- Filtrar sempre anos letivos, disciplinas e tarefas pelo aluno autenticado.
- Ocultar registros excluidos logicamente no uso normal do aluno.
- Bloquear exclusao de disciplina se houver tarefas vinculadas.
- Bloquear exclusao de ano letivo se houver disciplinas vinculadas.
- Unicidade deve considerar apenas registros ativos; registros excluidos logicamente nao devem bloquear recriacao.

## Regras de seguranca

- Login deve ser feito por usuario e senha.
- O campo `usuario` deve ser o identificador de login.
- O campo `email` deve ser usado como dado de cadastro e contato.
- O usuario deve ser unico.
- O e-mail do aluno deve ser unico.
- Senhas devem ser armazenadas com BCrypt.
- Cadastro de aluno deve exigir `nome`, `usuario`, `email` e `senha`.
- `email` deve ter formato valido.
- `senha` deve ter no minimo 6 caracteres.
- Formularios Thymeleaf devem respeitar CSRF do Spring Security.
- Deve existir logout.
- Paginas internas devem exigir autenticacao.
- Um aluno nunca deve conseguir acessar dados de outro aluno.

## Telas esperadas

- Tela de login.
- Tela de cadastro de aluno.
- Dashboard apos login.
- Visualizacao de disciplinas por ano letivo.
- Listagem de tarefas por disciplina.
- Criacao de tarefa.
- Conclusao de tarefa informando a data de entrega.

## Experiencia do dashboard

O dashboard deve ser pratico e interativo:

- Mostrar anos letivos disponiveis.
- Mostrar disciplinas do ano letivo selecionado.
- Mostrar tarefas agrupadas por disciplina.
- Destacar visualmente tarefas pendentes, vencidas, entregues e entregues com atraso.
- Calcular tarefas vencidas com base na data atual da aplicacao em `America/Sao_Paulo`.
- Permitir acesso rapido para criar e concluir tarefas.

## Banco de dados

Inicialmente use H2 para desenvolvimento e validacao:

- Configuracao local simples.
- Console H2 pode ser habilitado em ambiente de desenvolvimento.
- Evite recursos especificos do H2 que dificultem migracao para PostgreSQL.
- No perfil local com H2, a criacao automatica pelo Hibernate pode ser usada para acelerar validacao.
- Alteracoes diretas no banco sao permitidas apenas no H2/local para validacao rapida e devem ser registradas nos scripts SQL versionados.

Ao modelar entidades e scripts/configuracoes, considere que o banco futuro sera PostgreSQL.

Para PostgreSQL:

- Usar somente scripts SQL versionados para criar ou alterar estrutura de banco.
- Configurar Hibernate com `ddl-auto=validate`.
- Nao usar `ddl-auto=update`, `create` ou `create-drop`.

## Scripts de banco

- Toda alteracao de modelo que impacte banco de dados deve ser registrada em script versionado.
- Isso inclui criacao ou alteracao de tabelas, colunas, constraints, indices, sequencias e relacionamentos.
- A criacao automatica de schema pelo Hibernate/JPA deve ser usada apenas para H2 durante validacao local.
- Para PostgreSQL, a estrutura do banco deve ser criada por scripts SQL versionados.
- Nao depender de `ddl-auto=create`, `create-drop` ou `update` para ambientes que representem PostgreSQL.
- Ao adicionar ou alterar entidade JPA, verificar se tambem e necessario criar ou atualizar o script correspondente.
- Preferir scripts compativeis com PostgreSQL, evitando sintaxes exclusivas do H2.
- Usar scripts em `src/main/resources/db/scripts`.
- Nomear scripts com versao e descricao, por exemplo `001_schema_inicial.sql`.
- Criar unicidade para `AnoLetivo(aluno, ano)` considerando apenas registros ativos.
- Criar unicidade para `Disciplina(anoLetivo, nome)` considerando apenas registros ativos.
- Registrar indices necessarios para filtros por aluno, ano letivo, disciplina e status.

## Validacao e testes

- Rode os testes relevantes apos alterar regras de negocio.
- Uma fase so deve ser marcada como `CONCLUIDA` quando os testes relevantes estiverem passando.
- Priorize testes para regras de status de tarefas e situacao de prazo.
- Teste especialmente as regras de prazo e entrega:
  - entrega antes da previsao;
  - entrega na mesma data da previsao;
  - entrega depois da previsao.
- Teste tambem validacoes de acesso para garantir que um aluno nao acesse dados de outro.
- Teste que `VENCIDA` e calculada sem alterar o status persistido.
- Teste bloqueios de exclusao de ano letivo com disciplinas e disciplina com tarefas.
- Teste que registros excluidos logicamente nao aparecem no uso normal do aluno.
- Teste unicidade considerando apenas registros ativos.
- Teste CSRF/logout conforme configuracao do Spring Security.

## Comandos

Quando o projeto Spring Boot estiver criado com Maven:

- Rodar testes: `./mvnw test`
- Rodar aplicacao: `./mvnw spring-boot:run`
