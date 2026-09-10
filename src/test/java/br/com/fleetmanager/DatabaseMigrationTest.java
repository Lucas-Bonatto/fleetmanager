package br.com.fleetmanager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DatabaseMigrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void flywayCreatesAndVersionsTheCompleteSchema() {
        Integer applicationTableCount = jdbcTemplate.queryForObject("""
            select count(*)
            from information_schema.tables
            where lower(table_schema) = 'public'
              and lower(table_name) in ('drivers', 'vehicles', 'taxes', 'maintenances')
            """, Integer.class);

        String migrationVersion = jdbcTemplate.queryForObject("""
            select "version"
            from "flyway_schema_history"
            where "success" = true
            order by "installed_rank" desc
            fetch first 1 row only
            """, String.class);

        assertThat(applicationTableCount).isEqualTo(4);
        assertThat(migrationVersion).isEqualTo("1");
    }
}
