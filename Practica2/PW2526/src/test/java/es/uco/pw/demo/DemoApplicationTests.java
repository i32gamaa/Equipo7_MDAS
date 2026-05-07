package es.uco.pw.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de integridad para verificar el arranque del sistema.
 */
@SpringBootTest
class DemoApplicationTests {

    // REFACTORIZACIÓN SEMANA 4: Se renombra el método para seguir estándares y se añade DisplayName
    @Test
    @DisplayName("Carga del contexto de Spring")
    void contextLoads() {
        // Método vacío para verificar que el contexto se levanta sin excepciones [cite: 295]
    }

}