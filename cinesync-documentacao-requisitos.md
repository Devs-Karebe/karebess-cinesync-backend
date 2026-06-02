# CineSync — Documentação de Requisitos do Sistema
**Versão**: 1.0  
**Data**: Junho de 2026  
**Autor**: Software Document Architect  
**Status**: Draft  

---

## 1. Visão Geral do Sistema

### 1.1 Descrição
CineSync é uma plataforma web colaborativa de catálogo e gerenciamento de filmes. O sistema permite que usuários cadastrados pesquisem filmes a partir de uma API externa (TMDB), montem listas de filmes para assistir, e criem grupos onde membros podem construir watchlists coletivas. O diferencial central é o mecanismo de sorteio: com base nos filmes salvos no banco de dados, o sistema sorteia uma recomendação do que assistir — individualmente ou dentro de um grupo.

### 1.2 Problema que Resolve
Sem persistência em servidor, preferências e listas de filmes se perdem ao trocar de navegador ou dispositivo. Grupos de amigos também não conseguem colaborar em uma lista comum sem uma camada central de dados. O CineSync resolve isso ao manter o estado do usuário independente do cliente utilizado.

### 1.3 Escopo do MVP
- Autenticação de usuários
- Pesquisa de filmes via TMDB (consumo direto pelo frontend)
- Salvamento de filmes no banco (watchlist individual)
- Criação e participação em grupos via link de convite
- Watchlist por grupo
- Sorteio de filme (individual e por grupo)
- Deploy em Oracle Cloud (instâncias Always Free + PostgreSQL)

### 1.4 Stack Definida

| Camada | Tecnologia |
|---|---|
| Frontend | React + Vite |
| Backend | Java 21 + Spring Boot 3 + Spring Security |
| Autenticação | JWT (stateless) |
| ORM | Spring Data JPA + Hibernate |
| Banco de Dados | PostgreSQL (Oracle Cloud Compute) |
| API de Filmes | TMDB (themoviedb.org) |
| Deploy | Oracle Cloud Compute (Always Free) |

### 1.5 Stakeholders

| Papel | Descrição |
|---|---|
| Usuário Comum | Pessoa cadastrada que usa o sistema para gerenciar filmes |
| Dono de Grupo | Usuário que criou um grupo; tem controle sobre ele |
| Membro de Grupo | Usuário que entrou em um grupo via link de convite |
| Sistema (TMDB) | API externa que fornece o catálogo de filmes |

---

## 2. Requisitos Funcionais (RF)

### Módulo: Autenticação e Autorização (AUTH)

| ID | Prioridade | Descrição | Critérios de Aceitação |
|---|---|---|---|
| RF-AUTH-001 | Must Have | O usuário deve conseguir se cadastrar com nome, e-mail e senha | Cadastro aceita nome, e-mail único e senha. Senha é armazenada com hash (bcrypt). E-mail duplicado retorna erro 409. |
| RF-AUTH-002 | Must Have | O usuário deve conseguir fazer login com e-mail e senha | Credenciais válidas retornam JWT. Credenciais inválidas retornam erro genérico (sem enumeração de usuário). Máximo 5 tentativas antes de bloqueio temporário (15 min). |
| RF-AUTH-003 | Must Have | O sistema deve emitir e validar tokens JWT para sessões autenticadas | Token contém userId, email e roles. Expiração de 24h para access token. Endpoints protegidos rejeitam requisições sem token válido com 401. |
| RF-AUTH-004 | Must Have | Rotas protegidas devem ser acessíveis apenas para usuários autenticados | Qualquer rota não-pública sem token válido retorna 401. Token expirado retorna 401 com mensagem específica. |
| RF-AUTH-005 | Should Have | O usuário deve conseguir recuperar a senha via e-mail | Link de recuperação enviado para e-mail cadastrado. Link expira em 1 hora e é de uso único. Nova senha invalidada imediatamente após reset. |
| RF-AUTH-006 | Should Have | O usuário deve conseguir fazer logout | Token é invalidado (via blacklist em memória ou expiração imediata). Chamadas subsequentes com o token devolvem 401. |

---

### Módulo: Usuários (USER)

| ID | Prioridade | Descrição | Critérios de Aceitação |
|---|---|---|---|
| RF-USER-001 | Must Have | O usuário deve conseguir visualizar e editar seu perfil | Campos editáveis: nome e avatar (URL). E-mail não pode ser alterado pelo usuário após cadastro. |
| RF-USER-002 | Must Have | O usuário deve conseguir visualizar seu histórico de filmes salvos | Retorna lista paginada de filmes com status (QUERO_ASSISTIR / ASSISTIDO). |
| RF-USER-003 | Should Have | O usuário deve conseguir solicitar exclusão da sua conta | Conta marcada como inativa. Dados pessoais anonimizados em até 30 dias conforme LGPD. |
| RF-USER-004 | Should Have | O usuário deve conseguir exportar seus dados pessoais | Sistema gera arquivo JSON com todos os dados do usuário disponível para download. |

---

### Módulo: Filmes e Catálogo (MOVIE)

| ID | Prioridade | Descrição | Critérios de Aceitação |
|---|---|---|---|
| RF-MOVIE-001 | Must Have | O frontend deve consumir a API do TMDB diretamente para pesquisa e listagem de filmes | Resultados exibidos em até 2 segundos. Exibe título, poster, ano e sinopse. Pesquisa por título com mínimo 2 caracteres. |
| RF-MOVIE-002 | Must Have | O usuário deve conseguir salvar um filme na sua watchlist individual | Ao salvar, os dados do filme (tmdb_id, título, poster_url, sinopse, ano, gêneros) são persistidos no banco. Status inicial: QUERO_ASSISTIR. |
| RF-MOVIE-003 | Must Have | O usuário deve conseguir marcar um filme como assistido | Atualiza o status do filme na watchlist de QUERO_ASSISTIR para ASSISTIDO. Data de conclusão é registrada. |
| RF-MOVIE-004 | Must Have | O usuário deve conseguir remover um filme da sua watchlist | Filme removido da watchlist. Registro excluído do banco. |
| RF-MOVIE-005 | Should Have | O usuário deve conseguir visualizar os detalhes de um filme salvo | Exibe todos os dados persistidos no banco. Se necessário, frontend pode complementar com chamada ao TMDB. |
| RF-MOVIE-006 | Should Have | O sistema deve evitar salvar filmes duplicados na watchlist do mesmo usuário | Tentativa de salvar tmdb_id já presente na watchlist retorna erro 409. |

---

### Módulo: Sorteio e Recomendação (RAFFLE)

| ID | Prioridade | Descrição | Critérios de Aceitação |
|---|---|---|---|
| RF-RAFFLE-001 | Must Have | O sistema deve sortear um filme da watchlist individual do usuário com status QUERO_ASSISTIR | Retorna 1 filme aleatório. Se não houver filmes com status QUERO_ASSISTIR, retorna mensagem informativa. Sorteio é baseado exclusivamente no banco. |
| RF-RAFFLE-002 | Must Have | O sistema deve sortear um filme da watchlist de um grupo com status QUERO_ASSISTIR | Retorna 1 filme aleatório da lista do grupo. Usuário deve ser membro do grupo para solicitar o sorteio. |
| RF-RAFFLE-003 | Should Have | O resultado do sorteio deve ser registrado com data e hora | Log de sorteios armazena: qual filme foi sorteado, quem solicitou, data/hora e contexto (individual ou grupo). |
| RF-RAFFLE-004 | Could Have | O usuário pode optar por não ser sorteado com filmes já sorteados recentemente | Filtro opcional que exclui filmes sorteados nos últimos N dias (configurável). |

---

### Módulo: Grupos (GROUP)

| ID | Prioridade | Descrição | Critérios de Aceitação |
|---|---|---|---|
| RF-GROUP-001 | Must Have | Um usuário autenticado deve conseguir criar um grupo | Grupo criado com nome, código de convite único (UUID) e dono (owner). Dono é automaticamente adicionado como membro. |
| RF-GROUP-002 | Must Have | Um usuário autenticado deve conseguir entrar em um grupo via link de convite | Link no formato `/join/{invite_code}`. Usuário não pode entrar em grupo do qual já é membro (retorna 409). |
| RF-GROUP-003 | Must Have | O usuário deve conseguir visualizar os grupos dos quais participa | Lista retorna id, nome do grupo, quantidade de membros e data de entrada do usuário. |
| RF-GROUP-004 | Must Have | Os membros de um grupo devem conseguir adicionar filmes à watchlist do grupo | Ao adicionar, registra qual membro adicionou (added_by). Sem duplicatas do mesmo tmdb_id no mesmo grupo. |
| RF-GROUP-005 | Must Have | Os membros do grupo devem conseguir visualizar a watchlist coletiva | Lista paginada de filmes com status e quem adicionou cada um. |
| RF-GROUP-006 | Must Have | Os membros do grupo devem conseguir marcar filmes da watchlist coletiva como assistidos | Atualiza status do filme no grupo para ASSISTIDO. Qualquer membro pode atualizar. |
| RF-GROUP-007 | Should Have | O dono do grupo deve conseguir remover membros | Dono não pode remover a si mesmo. Membro removido perde acesso à watchlist do grupo. |
| RF-GROUP-008 | Should Have | O dono do grupo deve conseguir renomear o grupo | Novo nome validado (3-100 caracteres). |
| RF-GROUP-009 | Should Have | O dono deve conseguir excluir o grupo | Exclusão lógica (soft delete). Watchlist do grupo é preservada por 30 dias antes da purga. |
| RF-GROUP-010 | Could Have | O dono deve conseguir regenerar o link de convite | Código antigo invalidado imediatamente. Novo UUID gerado. |

---

## 3. Requisitos Não Funcionais (RNF)

### 3.1 Desempenho

| ID | Categoria | Descrição | Métrica | Meta | Forma de Medição |
|---|---|---|---|---|---|
| RNF-PERF-001 | Desempenho | Tempo de resposta da API em carga normal | Latência p95 (todos os endpoints) | < 500ms | Teste de carga com k6 |
| RNF-PERF-002 | Desempenho | Tempo de resposta da API em carga de pico | Latência p99 | < 2.000ms | Teste de carga com k6 |
| RNF-PERF-003 | Desempenho | Tempo de carregamento inicial do frontend | First Contentful Paint (FCP) | < 2s em conexão 4G | Lighthouse |
| RNF-PERF-004 | Desempenho | Tempo de execução de queries no banco | p95 de queries | < 100ms | Monitoramento de queries no PostgreSQL |
| RNF-PERF-005 | Desempenho | Tempo de resposta do endpoint de sorteio | Latência p95 | < 300ms | Teste unitário + integração |

### 3.2 Disponibilidade

| ID | Categoria | Descrição | Métrica | Meta | Forma de Medição |
|---|---|---|---|---|---|
| RNF-AVAIL-001 | Disponibilidade | Uptime do sistema | Uptime mensal | ≥ 99% (MVP) | Monitoramento sintético (UptimeRobot) |
| RNF-AVAIL-002 | Disponibilidade | Tempo de recuperação após falha (RTO) | Tempo até sistema voltar ao ar | < 2 horas | Simulação de falha |
| RNF-AVAIL-003 | Disponibilidade | Ponto de recuperação de dados (RPO) | Perda máxima aceitável de dados | < 24 horas | Validação de backup |

### 3.3 Escalabilidade

| ID | Categoria | Descrição | Métrica | Meta | Forma de Medição |
|---|---|---|---|---|---|
| RNF-SCAL-001 | Escalabilidade | Usuários simultâneos sem degradação | Sessões concorrentes | 100 usuários (MVP) | Teste de carga |
| RNF-SCAL-002 | Escalabilidade | Filmes por watchlist sem degradação de performance | Registros por usuário | Até 500 filmes | Teste com massa de dados |
| RNF-SCAL-003 | Escalabilidade | Membros por grupo sem degradação | Membros por grupo | Até 50 membros | Teste com massa de dados |

### 3.4 Segurança

| ID | Categoria | Descrição | Meta |
|---|---|---|---|
| RNF-SEC-001 | Segurança | Criptografia em trânsito | TLS 1.2+ em todos os endpoints |
| RNF-SEC-002 | Segurança | Hash de senhas | bcrypt com custo ≥ 12 |
| RNF-SEC-003 | Segurança | Expiração de sessão | Access token expira em 24h. Sessão idle expira em 30 min |
| RNF-SEC-004 | Segurança | Rate limiting | 100 req/min por IP não autenticado; 500 req/min por usuário autenticado |
| RNF-SEC-005 | Segurança | Segredos de aplicação | Nenhum segredo no código-fonte ou arquivos de configuração versionados. Uso de variáveis de ambiente |
| RNF-SEC-006 | Segurança | Chave da API TMDB | Armazenada no backend e nunca exposta ao cliente |
| RNF-SEC-007 | Segurança | Proteção contra ataques comuns | Headers de segurança configurados (CORS, CSP, X-Frame-Options). Proteção contra SQL Injection via ORM parametrizado |
| RNF-SEC-008 | Segurança | Auditoria de tentativas de login | Tentativas com falha registradas com IP, timestamp e user-agent |

### 3.5 Conformidade (LGPD)

| ID | Categoria | Framework | Descrição |
|---|---|---|---|
| RNF-COMP-001 | Conformidade | LGPD | O sistema deve coletar consentimento explícito do usuário no cadastro antes de processar dados pessoais |
| RNF-COMP-002 | Conformidade | LGPD | O sistema deve permitir que o usuário acesse e exporte todos os seus dados pessoais (RF-USER-004) |
| RNF-COMP-003 | Conformidade | LGPD | O sistema deve suportar solicitação de exclusão de conta com anonimização de dados (RF-USER-003) |
| RNF-COMP-004 | Conformidade | LGPD | Dados pessoais (e-mail, nome) devem ser armazenados com criptografia em repouso no banco |
| RNF-COMP-005 | Conformidade | LGPD | Registros de consentimento devem ser armazenados com timestamp, versão do termo e identificador do usuário |

### 3.6 Manutenibilidade

| ID | Categoria | Descrição | Meta |
|---|---|---|---|
| RNF-MAINT-001 | Manutenibilidade | Cobertura de testes (unitário + integração) | ≥ 70% para lógica de negócio no backend |
| RNF-MAINT-002 | Manutenibilidade | Documentação da API | Todos os endpoints documentados via OpenAPI/Swagger |
| RNF-MAINT-003 | Manutenibilidade | Estrutura de pacotes | Backend organizado em camadas claras (controller, service, repository, domain) |
| RNF-MAINT-004 | Manutenibilidade | Versionamento da API | API versionada via prefixo de rota (`/api/v1/`) desde o início |
| RNF-MAINT-005 | Manutenibilidade | Logs estruturados | Logs em formato JSON com nível, timestamp, traceId e contexto do usuário |

### 3.7 Portabilidade e Implantação

| ID | Categoria | Descrição | Meta |
|---|---|---|---|
| RNF-PORT-001 | Portabilidade | O backend deve ser implantável via JAR executável | `java -jar cinesync.jar` funciona com variáveis de ambiente configuradas |
| RNF-PORT-002 | Portabilidade | O frontend deve ser implantável como build estático | `npm run build` gera artefato implantável em qualquer servidor estático |
| RNF-PORT-003 | Implantação | Separação de perfis de configuração | Perfis separados: `dev`, `prod`. Configurações sensíveis via variáveis de ambiente em produção |

---

## 4. Regras de Negócio (RN)

### Domínio: Autenticação (AUTH)

| ID | Nome | Gatilho | Regra | Exceção | Ponto de Execução | Prioridade |
|---|---|---|---|---|---|---|
| RN-AUTH-001 | Unicidade de E-mail | Cadastro de usuário | Um e-mail só pode estar associado a uma conta ativa no sistema | — | `UserService.register()` | Crítica |
| RN-AUTH-002 | Bloqueio por Tentativas | Falha no login | Após 5 tentativas de login malsucedidas consecutivas, a conta é bloqueada por 15 minutos | — | `AuthService.login()` | Alta |
| RN-AUTH-003 | Não Enumeração de Usuário | Qualquer falha de autenticação | O sistema não deve revelar se o e-mail existe ou não. Sempre retornar mensagem genérica de "credenciais inválidas" | — | `AuthService.login()` | Alta |
| RN-AUTH-004 | Expiração de Token | Toda requisição autenticada | O token JWT deve ser rejeitado após 24 horas da sua emissão | — | Filtro JWT no Spring Security | Crítica |
| RN-AUTH-005 | Link de Recuperação Único | Solicitação de recuperação de senha | O link de recuperação só pode ser usado uma vez. Após uso, é invalidado imediatamente | — | `AuthService.resetPassword()` | Alta |

---

### Domínio: Filmes e Watchlist (MOVIE)

| ID | Nome | Gatilho | Regra | Exceção | Ponto de Execução | Prioridade |
|---|---|---|---|---|---|---|
| RN-MOVIE-001 | Deduplicação na Watchlist Individual | Adição de filme | Um usuário não pode ter o mesmo filme (mesmo tmdb_id) duas vezes na sua watchlist | — | `WatchlistService.add()` | Alta |
| RN-MOVIE-002 | Persistência Seletiva no Banco | Interação do usuário com filme | Um filme do TMDB só é persistido no banco quando o usuário realiza uma ação (salvar, favoritar). Simples navegação/pesquisa não grava nada | — | `MovieService.save()` | Crítica |
| RN-MOVIE-003 | Propriedade da Watchlist | Operações na watchlist | Um usuário só pode editar ou remover filmes da sua própria watchlist individual | — | `WatchlistService` (validação de ownership) | Crítica |
| RN-MOVIE-004 | Status Inicial de Filme | Adição de filme à watchlist | Todo filme adicionado à watchlist começa com status `QUERO_ASSISTIR` | — | `WatchlistService.add()` | Alta |
| RN-MOVIE-005 | Transição de Status | Marcação de filme como assistido | A transição de status só pode seguir o fluxo: `QUERO_ASSISTIR → ASSISTIDO`. Não existe reversão de status (para MVP) | — | `WatchlistService.updateStatus()` | Média |

---

### Domínio: Sorteio (RAFFLE)

| ID | Nome | Gatilho | Regra | Exceção | Ponto de Execução | Prioridade |
|---|---|---|---|---|---|---|
| RN-RAFFLE-001 | Fonte do Sorteio | Solicitação de sorteio | O sorteio deve ser realizado exclusivamente com base nos filmes salvos no banco de dados com status `QUERO_ASSISTIR`. A API do TMDB não é consultada durante o sorteio | — | `RaffleService.draw()` | Crítica |
| RN-RAFFLE-002 | Sorteio Vazio | Solicitação de sorteio sem filmes elegíveis | Se não houver filmes com status `QUERO_ASSISTIR` na lista, o sistema retorna uma resposta informativa (não um erro) orientando o usuário a adicionar filmes | — | `RaffleService.draw()` | Alta |
| RN-RAFFLE-003 | Autorização de Sorteio em Grupo | Sorteio em contexto de grupo | Apenas membros ativos do grupo podem solicitar um sorteio da watchlist do grupo | — | `RaffleService.drawForGroup()` | Crítica |
| RN-RAFFLE-004 | Registro de Sorteio | Conclusão de qualquer sorteio | Todo sorteio realizado deve ser registrado: filme sorteado, usuário solicitante, data/hora e contexto (individual ou group_id) | — | `RaffleService.draw()` (post-action) | Média |

---

### Domínio: Grupos (GROUP)

| ID | Nome | Gatilho | Regra | Exceção | Ponto de Execução | Prioridade |
|---|---|---|---|---|---|---|
| RN-GROUP-001 | Pré-requisito de Conta | Acesso a qualquer grupo | Para entrar em um grupo, o usuário deve ter uma conta ativa no sistema. O link de convite redirecionará para o cadastro/login antes de processar a entrada | — | `GroupService.join()` | Crítica |
| RN-GROUP-002 | Deduplicação de Membro | Entrada em grupo | Um usuário não pode entrar no mesmo grupo mais de uma vez | — | `GroupService.join()` | Alta |
| RN-GROUP-003 | Dono Como Membro | Criação de grupo | Ao criar um grupo, o criador é automaticamente adicionado como membro com papel `OWNER` | — | `GroupService.create()` | Alta |
| RN-GROUP-004 | Proteção do Dono | Remoção de membro | O dono do grupo não pode ser removido por nenhum mecanismo, exceto pela exclusão do próprio grupo | — | `GroupService.removeMember()` | Alta |
| RN-GROUP-005 | Transferência de Propriedade | Exclusão de conta do dono | Se o dono excluir sua conta, o grupo deve ter sua propriedade transferida para o membro mais antigo ou ser excluído caso seja o único membro | — | `UserService.delete()` (cascata) | Média |
| RN-GROUP-006 | Deduplicação na Watchlist do Grupo | Adição de filme ao grupo | Um mesmo filme (tmdb_id) não pode aparecer duas vezes na watchlist do mesmo grupo, independentemente de quem adicionou | — | `GroupWatchlistService.add()` | Alta |
| RN-GROUP-007 | Permissão de Edição de Watchlist do Grupo | Atualização de status de filme no grupo | Qualquer membro ativo do grupo pode marcar um filme da watchlist do grupo como assistido | — | `GroupWatchlistService.updateStatus()` | Alta |
| RN-GROUP-008 | Limite de Grupos por Usuário | Criação de grupo | Um usuário pode criar no máximo 10 grupos (para MVP, evitar abuso de recursos) | — | `GroupService.create()` | Média |

---

### Domínio: Privacidade e Dados (LGPD)

| ID | Nome | Gatilho | Regra | Exceção | Ponto de Execução | Prioridade |
|---|---|---|---|---|---|---|
| RN-LGPD-001 | Consentimento no Cadastro | Registro de novo usuário | O usuário deve aceitar os termos de uso e política de privacidade explicitamente antes de concluir o cadastro. Cadastro sem aceite não pode ser processado | — | `UserService.register()` | Crítica |
| RN-LGPD-002 | Anonimização na Exclusão | Solicitação de exclusão de conta | Ao excluir a conta, dados pessoais (nome, e-mail) são anonimizados no banco. Registros não-pessoais (filmes salvos, histórico de sorteios) podem ser mantidos em forma anonimizada para integridade referencial | Dados podem ser retidos se necessário para obrigação legal | `UserService.delete()` | Crítica |
| RN-LGPD-003 | Retenção de Dados de Consentimento | Qualquer evento de consentimento | Registros de consentimento (aceite de termos) são imutáveis — não podem ser deletados, apenas anotados com revogação e timestamp | — | `ConsentService` | Alta |

---

## 5. Máquinas de Estado

### 5.1 Status do Filme na Watchlist

```
                   ┌─────────────────┐
                   │  QUERO_ASSISTIR │
                   └────────┬────────┘
                            │ Usuário marca como assistido
                            ▼
                   ┌─────────────────┐
                   │    ASSISTIDO    │
                   └─────────────────┘

(Para o MVP, não há reversão de status)
```

| De | Para | Gatilho | Regra | Quem Pode |
|---|---|---|---|---|
| — | QUERO_ASSISTIR | Filme adicionado à watchlist | Status inicial obrigatório | Dono da watchlist / Membro do grupo |
| QUERO_ASSISTIR | ASSISTIDO | Usuário marca como assistido | Filme deve existir na watchlist | Dono (individual) / Qualquer membro (grupo) |

---

### 5.2 Ciclo de Vida do Grupo

```
    ACTIVE ──────────────────────────────────► DELETED
      │                                           ▲
      │ Dono exclui o grupo                       │
      └───────────────────────────────────────────┘
```

| De | Para | Gatilho | Regra | Quem Pode |
|---|---|---|---|---|
| — | ACTIVE | Criação do grupo | Dono definido | Qualquer usuário autenticado |
| ACTIVE | DELETED | Exclusão pelo dono | Soft delete. Purga de dados em 30 dias | Apenas o dono |

---

## 6. Regras de Validação de Campos

| Campo | Entidade | Regra de Validação | Mensagem de Erro |
|---|---|---|---|
| email | User | Formato RFC 5322. Único no sistema | "E-mail inválido" / "E-mail já cadastrado" |
| password | User | Mínimo 8 caracteres, pelo menos 1 letra e 1 número | "Senha deve ter ao menos 8 caracteres com letras e números" |
| name (user) | User | Entre 2 e 100 caracteres. Não pode ser vazio | "Nome inválido" |
| name (group) | Group | Entre 3 e 100 caracteres. Não pode ser vazio | "Nome do grupo inválido" |
| invite_code | Group | UUID v4. Gerado pelo sistema, não informado pelo usuário | — |
| tmdb_id | Movie | Inteiro positivo. Obrigatório ao salvar filme | "Filme inválido" |
| title | Movie | Máximo 500 caracteres. Obrigatório | "Título do filme inválido" |
| status | Watchlist | Enum: `QUERO_ASSISTIR`, `ASSISTIDO` | "Status inválido" |

---

## 7. Controle de Acesso

| Recurso | Ação | Permissão Necessária | Condição Adicional |
|---|---|---|---|
| Watchlist individual | Criar / Editar / Deletar | Usuário autenticado | Somente o próprio usuário |
| Watchlist individual | Sorteio | Usuário autenticado | Somente o próprio usuário |
| Grupos | Criar | Usuário autenticado | Limite de 10 grupos por usuário |
| Grupos | Entrar | Usuário autenticado + link válido | Não ser membro já |
| Grupo | Renomear / Excluir / Remover membro | Usuário autenticado + OWNER | Somente o dono do grupo |
| Watchlist do grupo | Adicionar / Atualizar status | Usuário autenticado + membro ativo | Deve ser membro do grupo |
| Sorteio do grupo | Solicitar | Usuário autenticado + membro ativo | Deve ser membro do grupo |
| Perfil do usuário | Ver / Editar | Usuário autenticado | Somente o próprio usuário |
| Dados pessoais | Exportar / Deletar | Usuário autenticado | Somente o próprio usuário |

---

## 8. Fora do Escopo (MVP)

Os itens abaixo são explicitamente excluídos do MVP, podendo ser considerados em versões futuras:

| Item | Justificativa |
|---|---|
| Aprovação de membros em grupos | Adiciona complexidade de painel de gerenciamento; convite por link é suficiente para o MVP |
| Avaliação/rating de filmes | Funcionalidade adicional; o foco do MVP é watchlist e sorteio |
| Notificações por e-mail (exceto recuperação de senha) | Requer serviço de e-mail transacional; complexidade fora do escopo inicial |
| Sistema de comentários em filmes | Fora do escopo; pode ser adicionado posteriormente |
| Integração com outras APIs de catálogo | TMDB é suficiente e gratuito para o MVP |
| Aplicativo mobile | MVP é web-first |
| Chat entre membros do grupo | Complexidade de websocket/polling fora do escopo |
| Recomendações por algoritmo (machine learning) | Sorteio aleatório cobre o MVP; algoritmos inteligentes são evoluções futuras |
| Múltiplas watchlists por usuário | Uma watchlist por usuário e uma por grupo é suficiente para o MVP |

---

## 9. Premissas e Dependências

| # | Premissa / Dependência | Impacto se Falha ou Indisponível |
|---|---|---|
| 1 | A API do TMDB permanece gratuita e acessível para o volume do MVP | Pesquisa e exibição de catálogo ficam bloqueadas; funcionalidades de watchlist e sorteio (banco) não são afetadas |
| 2 | O desenvolvedor possui chave de API ativa no TMDB | Sem chave, nenhum dado de catálogo pode ser exibido |
| 3 | As instâncias Always Free da Oracle Cloud são suficientes para o volume do MVP | Limitação de performance; pode exigir upgrade ou migração de provedor |
| 4 | PostgreSQL é instalado e configurado manualmente na instância Oracle | Banco indisponível; sistema não funciona |
| 5 | O desenvolvedor configura HTTPS (via reverse proxy como Nginx + Let's Encrypt) | Requisito RNF-SEC-001 não atendido; acesso inseguro |
| 6 | O frontend é deployado em serviço estático (Vercel, Netlify ou OCI Object Storage) | Frontend inacessível |

---

*Documento gerado para o projeto CineSync — MVP v1.0*  
*Próximo passo: Modelagem final do banco de dados*
