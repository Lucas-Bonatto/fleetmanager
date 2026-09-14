# Deploy no alwaysdata

Este documento descreve o ambiente público do Fleet Manager sem incluir credenciais.

## Arquitetura de produção

- Java 21 executado como **serviço ativo continuamente**;
- site do tipo **Reverse proxy** encaminhando o tráfego ao serviço;
- monitoramento do processo com reinício automático em caso de falha;
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

## Configurar o serviço 24/7

Em **Advanced > Services**, crie um serviço com estas definições:

- usuário SSH da conta;
- diretório de trabalho `.`;
- uma porta fixa livre entre `8300` e `8499`, representada abaixo por `<porta-servico>`;
- serviço ativo, sem marcar a opção de pausa.

Use o comando abaixo, substituindo `<conta>` e `<porta-servico>` pelos valores do ambiente:

```text
java -Xms8m -Xmx32m -Xss256k -XX:MaxMetaspaceSize=104m -XX:CompressedClassSpaceSize=16m -XX:ReservedCodeCacheSize=8m -XX:MaxDirectMemorySize=4m -XX:+UseSerialGC -XX:-TieredCompilation -XX:CICompilerCount=2 -XX:+ExitOnOutOfMemoryError -XX:-UsePerfData -XX:ActiveProcessorCount=1 -Djava.awt.headless=true -jar /home/<conta>/fleetmanager.jar --server.address=:: --server.port=<porta-servico> --spring.main.lazy-initialization=true --spring.data.jpa.repositories.bootstrap-mode=lazy --server.tomcat.threads.max=6 --server.tomcat.threads.min-spare=1 --spring.datasource.hikari.maximum-pool-size=2 --spring.datasource.hikari.minimum-idle=1 --server.forward-headers-strategy=framework
```

Configure o comando de monitoramento, usando a mesma porta:

```text
nc -z services-<conta>.alwaysdata.net <porta-servico>
```

As variáveis de ambiente da seção seguinte devem ser cadastradas no serviço.

## Configurar o proxy reverso

Crie ou edite o site público com estas definições:

- tipo **Reverse proxy**;
- URL remota `http://services-<conta>.alwaysdata.net:<porta-servico>`;
- **Force HTTPS** ativado.

O serviço do Public Cloud escuta em IPv6; o hostname `services-<conta>.alwaysdata.net` resolve o endereço apropriado para a comunicação interna.

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
4. o serviço está ativo, sem pausa, e o monitor não apresenta um ciclo de reinicializações;
5. os logs registram o schema Flyway atualizado e não apresentam encerramento por falta de memória.

Os logs do processo ficam disponíveis no painel do alwaysdata e em:

```text
/home/<conta>/admin/logs/services/
```

## Rollback

Mantenha uma cópia do último JAR estável fora do caminho executado. Em caso de falha:

1. restaure o JAR anterior em `/home/<conta>/fleetmanager.jar`;
2. preserve as variáveis e o banco de dados;
3. reinicie o serviço;
4. repita o healthcheck e a validação dos redirecionamentos.

Migrações destrutivas exigem backup e um plano de reversão próprio. Nunca execute `flyway clean` em produção.
