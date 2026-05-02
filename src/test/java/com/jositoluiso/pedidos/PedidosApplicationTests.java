package com.jositoluiso.pedidos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test") //TODO: Corregir error perfil test
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:pedidos_test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=PostgreSQL",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.show-sql=false"
})
class PedidosApplicationTests {

    @Test
    void contextLoads() {
    }
}