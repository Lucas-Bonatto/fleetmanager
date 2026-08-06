# Controle de Frotas 🚙

Sistema web Fullstack desenvolvido para centralizar a gestão operacional e financeira de frotas automotivas.

O projeto substitui planilhas dispersas por uma aplicação estruturada, permitindo o cadastro de veículos e motoristas, controle de tributos, acompanhamento de manutenções preventivas e visualização de alertas em um dashboard responsivo.

## 🌐 Aplicação publicada

A aplicação está hospedada no Railway e utiliza conexão segura por HTTPS:

### [Acessar o Controle de Frotas](https://controle-de-frotas.up.railway.app)

> O ambiente público possui acesso protegido por autenticação.  
> As credenciais administrativas não são divulgadas no repositório.

## 📋 Funcionalidades

- Dashboard com indicadores gerais da frota.
- Cadastro, edição, consulta e exclusão de veículos.
- Cadastro e gerenciamento de motoristas.
- Associação entre veículos e motoristas.
- Controle de IPVA, licenciamento, seguros, multas e outros tributos.
- Registro e acompanhamento de manutenções preventivas.
- Controle de manutenções por data e quilometragem.
- Alertas automáticos de tributos e manutenções.
- Validação para impedir a redução acidental do hodômetro.
- Login administrativo com Spring Security.
- Logout seguro com encerramento da sessão.
- Interface responsiva para computadores e dispositivos móveis.
- Banco PostgreSQL no ambiente público.
- Banco H2 para desenvolvimento local.
- Tratamento global de exceções.
- Testes automatizados das regras de negócio.

## 🚦 Status dos tributos

O sistema calcula automaticamente o status de cada tributo:

- **Pago:** pagamento registrado.
- **Atrasado:** vencimento ultrapassado e pagamento pendente.
- **Vencimento próximo:** faltam até 15 dias para o vencimento.
- **No prazo:** tributo pendente fora da janela de alerta.

## 🔧 Status das manutenções

As manutenções são avaliadas por data e quilometragem:

- **Vencida:** prazo ou quilometragem limite ultrapassados.
- **Próxima:** faltam até 30 dias ou até 1.000 km.
- **Em dia:** nenhuma condição de alerta foi atingida.

## 🛠️ Tecnologias utilizadas

### Backend

- Java 21
- Spring Boot
- Spring MVC
- Spring Security
- Spring Data JPA
- Hibernate
- Bean Validation
- Maven

### Frontend

- HTML5
- CSS3
- Thymeleaf
- Layout responsivo

### Banco de dados

- PostgreSQL no ambiente de produção
- H2 no ambiente local

### Infraestrutura

- Git e GitHub
- Railway
- GitHub Actions
- Docker Compose

## 🏗️ Arquitetura

O projeto utiliza o padrão arquitetural MVC, com separação de responsabilidades entre controllers, services, repositories, entidades e DTOs.

```mermaid
flowchart LR
    UI[Thymeleaf / HTML / CSS] --> SEC[Spring Security]
    SEC --> C[Controllers]
    C --> S[Services e regras de negócio]
    S --> R[Spring Data Repositories]
    R --> DB[(H2 / PostgreSQL)]
    S --> DTO[DTOs e View Models]
```

Estrutura principal:

```text
src/main/java/br/com/fleetmanager
├── config          # configurações, segurança e dados de desenvolvimento
├── domain          # entidades e enums
├── exception       # exceções de domínio
├── repository      # persistência com Spring Data JPA
├── service         # regras de negócio
└── web             # controllers, DTOs e view models
```

## 🔐 Segurança

Todas as páginas do sistema, exceto a tela de login e os arquivos estáticos, exigem autenticação.

O sistema utiliza:

- Spring Security;
- senhas processadas com BCrypt;
- proteção CSRF;
- sessão autenticada;
- logout por requisição POST;
- credenciais de produção armazenadas em variáveis de ambiente;
- conexão HTTPS no ambiente público.

Nenhuma senha do ambiente público é armazenada no código-fonte ou no GitHub.

## 💻 Executando localmente

### Pré-requisitos

- JDK 21
- Maven 3.9 ou superior
- Git

Clone o repositório:

```bash
git clone https://github.com/Lucas-Bonatto/fleetmanager.git
cd fleetmanager
```

Execute os testes:

```bash
mvn clean test
```

Inicie a aplicação:

```bash
mvn spring-boot:run
```

Acesse:

```text
http://localhost:8080
```

A aplicação redirecionará automaticamente para:

```text
http://localhost:8080/login
```

As credenciais locais podem ser alteradas por meio das variáveis:

```bash
ADMIN_USERNAME=seu_usuario
ADMIN_PASSWORD=sua_senha
```

## 🗄️ PostgreSQL

No ambiente de produção, a aplicação utiliza as seguintes variáveis:

```bash
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://servidor:porta/banco
DATABASE_USERNAME=usuario
DATABASE_PASSWORD=senha
ADMIN_USERNAME=usuario_administrativo
ADMIN_PASSWORD=senha_administrativa
```

As informações reais são configuradas diretamente na plataforma de hospedagem e não são versionadas.

## 🐳 PostgreSQL com Docker

Para executar um PostgreSQL local:

```bash
docker compose up -d
```

Depois, inicie o sistema com o perfil de produção:

```bash
mvn -Dspring-boot.run.profiles=prod spring-boot:run
```

## 🧪 Testes

Execute:

```bash
mvn clean test
```

O projeto possui testes para as principais regras de status de tributos e manutenções.

O GitHub Actions também executa automaticamente os testes quando alterações são enviadas ao repositório.

## 🚀 Deploy

O deploy da aplicação é realizado automaticamente pelo Railway a partir da branch `main` do GitHub.

Fluxo de publicação:

```text
Código local
    ↓
GitHub
    ↓
Railway
    ↓
Spring Boot
    ↓
PostgreSQL
```

Cada novo `push` para a branch principal inicia uma nova compilação e publicação.

## 📌 Possíveis evoluções

- Cadastro de diferentes usuários.
- Perfis de administrador, gestor e motorista.
- Recuperação de senha.
- Upload de documentos e comprovantes.
- Controle de abastecimentos.
- Cálculo de consumo médio.
- Relatórios financeiros por veículo.
- Exportação de relatórios em PDF.
- Notificações por e-mail ou WhatsApp.
- Migrações de banco com Flyway.
- Testes de integração com Testcontainers.

## 👨‍💻 Autor

Desenvolvido por **Lucas Bonatto**.

Projeto criado para estudo e portfólio, aplicando conceitos de desenvolvimento Fullstack, orientação a objetos, arquitetura MVC, persistência de dados, segurança e publicação em nuvem.

## 📄 Licença

Distribuído sob a licença MIT.