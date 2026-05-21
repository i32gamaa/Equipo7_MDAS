package es.uco.pw.demo.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HomeControllerTest {

    /**
     * DECISIÓN DE DISEÑO: Se renombra el método a 'contextLoads' para seguir el estándar 
     * de Spring Boot y se usa @DisplayName para mantener la descripción en español 
     * sin ensuciar el nombre de la función.
     */
    @Test
    @DisplayName("Debe arrancar el contexto de Spring sin errores")
    void contextLoads() {
        // Test de integridad básico para verificar la configuración del proyecto.
    }
}