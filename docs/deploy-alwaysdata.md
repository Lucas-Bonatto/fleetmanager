# Deploy no alwaysdata

Este documento descreve o ambiente público do Fleet Manager sem incluir credenciais.

## Arquitetura de produção

- Java 21 executado por um site do tipo **Programa de usuário**;
- aplicação escutando em IPv6 (`::`) e na porta dinâmica fornecida por `$PORT`;
- tempo de inatividade do site configurado como `0`;
- PostgreSQL 17 gerenciado pelo alwaysdata;
- Flyway aplicado antes da validação do Hibernate;
- HTTPS obrigatório no site;
- healthcheck público em `/actuator/health` sem detalhes internos;
- credenciais fornecidas exclusivamente por variáveis de ambiente.

Esta configuração é compatível com o plano gratuito. O tempo de inatividade `0` evita que o processo seja encerrado apenas por falta de acessos, mas o plano gratuito não oferece garantia de disponibilidade contínua ou SLA.

Referências oficiais: [Java no alwaysdata](https://help.alwaysdata.com/en/docs/web-hosting/languages/java/configuration/), [Programa de usuário](https://help.alwaysdata.com/en/docs/web-hosting/sites/http-servers/user-program/) e [tempo de inatividade](https://help.alwaysdata.com/en/docs/web-hosting/sites/misc/).

## Preparar o artefato

Antes da publicação, valide e empacote a aplicação:

```bash
mvn --batch-mode --no-transfer-progress clean verify
```

Envie `target/fleetmanager-0.0.1-SNAPSHOT.jar` por SSH/SFTP para:

```text
/home/<conta>/fleetmanager.jar
```

## Configurar o site

Em **Web > Sites**, crie ou edite o domínio público com estas definições:

- tipo **Programa de usuário**;
- diretório de trabalho `/home/<conta>`;
- tempo de inatividade `0`;
- **Force HTTPS** ativado.

Use o comando abaixo, substituindo apenas `<conta>`. Mantenha `$PORT` literalmente: essa variável é preenchida pelo alwaysdata quando o processo inicia.

```text
java -Xms8m -Xmx56m -Xss256k -XX:MaxMetaspaceSize=104m -XX:CompressedClassSpaceSize=16m -XX:ReservedCodeCacheSize=8m -XX:MaxDirectMemorySize=4m -XX:+UseSerialGC -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -XX:CICompilerCount=2 -XX:+ExitOnOutOfMemoryError -XX:-UsePerfData -XX:ActiveProcessorCount=1 -Djava.awt.headless=true -jar /home/<conta>/fleetmanager.jar --server.address=:: --server.port=$PORT --spring.main.lazy-initialization=true --spring.data.jpa.repositories.bootstrap-mode=lazy --server.tomcat.threads.max=6 --server.tomcat.threads.min-spare=1 --spring.datasource.hikari.maximum-pool-size=2 --spring.datasource.hikari.minimum-idle=1 --server.forward-headers-strategy=framework
```

Os limites de memória e de concorrência foram medidos para caber nos 256 MB do plano gratuito. Alterações nesses valores devem ser validadas com carga e pelos logs antes de serem publicadas.

## Variáveis de ambiente

```text
JAVA_VERSION=21
SPRING_PROFILES_ACTIVE=prod
SPRING_JMX_ENABLED=false
PGHOST=<host-postgresql>
PGPORT=5432
PGDATABASE=<banco>
PGUSER=<usuario>
PGPASSWORD=<segredo>
APP_SEED_ENABLED=false
FLYWAY_BASELINE_ON_MIGRATE=false
ADMIN_USERNAME=<usuario-administrativo>
ADMIN_PASSWORD=<segredo>
DEMO_ACCESS_ENABLED=true
```

Use `APP_SEED_ENABLED=true` apenas quando o ambiente público for destinado à demonstração com dados de exemplo. Em uma instalação de produção real, mantenha `false`.

`DEMO_ACCESS_ENABLED=true` habilita o botão público de demonstração. O visitante recebe uma sessão autenticada com permissão apenas para consultar o dashboard e as listagens; formulários e operações de escrita continuam restritos ao administrador. Mantenha a opção desativada em ambientes que contenham dados reais ou confidenciais.

Não inclua senhas em commits, logs, capturas de tela ou solicitações de suporte.

## Verificação

Depois da inicialização, confirme:

```bash
curl --fail --show-error https://<dominio>/actuator/health
```

O retorno esperado contém `"status":"UP"`. Verifique também que:

1. `http://<dominio>` responde com redirecionamento permanente para HTTPS;
2. a raiz HTTPS redireciona para `/login` mantendo o protocolo HTTPS;
3. `/login` responde com HTTP 200;
4. quando `DEMO_ACCESS_ENABLED=true`, o botão **Acessar demonstração** abre o dashboard e as tentativas de acessar formulários ou enviar alterações são recusadas;
5. o site não apresenta um ciclo de reinicializações e permanece disponível após acessos sucessivos;
6. os logs registram o schema Flyway atualizado e não apresentam encerramento por falta de memória.

Os logs do processo ficam disponíveis no painel do alwaysdata e em:

```text
/home/<conta>/admin/logs/sites/
```

## Rollback

Mantenha uma cópia do último JAR estável fora do caminho executado. Em caso de falha:

1. restaure o JAR anterior em `/home/<conta>/fleetmanager.jar`;
2. preserve as variáveis e o banco de dados;
3. salve novamente a configuração do site para reiniciar o processo;
4. repita o healthcheck e a validação dos redirecionamentos.

Migrações destrutivas exigem backup e um plano de reversão próprio. Nunca execute `flyway clean` em produção.
