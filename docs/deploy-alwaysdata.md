# Deploy no alwaysdata

Este documento descreve o ambiente público do Fleet Manager sem incluir credenciais.

## Arquitetura de produção

- Java 21 executado como **User program**;
- PostgreSQL 17 gerenciado pelo alwaysdata;
- Flyway aplicado antes da validação do Hibernate;
- HTTPS obrigatório no proxy;
- healthcheck público em `/actuator/health` sem detalhes internos;
- credenciais fornecidas exclusivamente por variáveis de ambiente.

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

Crie ou edite um site do tipo **User program** com diretório de trabalho `.` e use o comando abaixo, substituindo `<conta>` pelo nome da conta:

```text
java -Xms8m -Xmx32m -Xss256k -XX:MaxMetaspaceSize=104m -XX:CompressedClassSpaceSize=16m -XX:ReservedCodeCacheSize=8m -XX:MaxDirectMemorySize=4m -XX:+UseSerialGC -XX:-TieredCompilation -XX:CICompilerCount=2 -XX:+ExitOnOutOfMemoryError -XX:-UsePerfData -XX:ActiveProcessorCount=1 -Djava.awt.headless=true -jar /home/<conta>/fleetmanager.jar --server.address=:: --server.port=$PORT --spring.main.lazy-initialization=true --spring.data.jpa.repositories.bootstrap-mode=lazy --server.tomcat.threads.max=6 --server.tomcat.threads.min-spare=1 --spring.datasource.hikari.maximum-pool-size=2 --spring.datasource.hikari.minimum-idle=1 --server.forward-headers-strategy=framework
```

Ative **Force HTTPS** no site.

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
```

Use `APP_SEED_ENABLED=true` apenas quando o ambiente público for destinado à demonstração com dados de exemplo. Em uma instalação de produção real, mantenha `false`.

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
4. os logs registram o schema Flyway atualizado e não apresentam encerramento por falta de memória.

Os logs do processo ficam disponíveis no painel do alwaysdata e em:

```text
/home/<conta>/admin/logs/sites/
```

## Rollback

Mantenha uma cópia do último JAR estável fora do caminho executado. Em caso de falha:

1. restaure o JAR anterior em `/home/<conta>/fleetmanager.jar`;
2. preserve as variáveis e o banco de dados;
3. reinicie o site;
4. repita o healthcheck e a validação dos redirecionamentos.

Migrações destrutivas exigem backup e um plano de reversão próprio. Nunca execute `flyway clean` em produção.
