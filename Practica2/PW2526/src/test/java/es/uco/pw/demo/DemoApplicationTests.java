package es.uco.pw.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

    // REGLA CLEAN CODE APLICADA: Claridad e intencion. "contextLoads" es el nombre por defecto de Spring. 
    // Lo cambiamos para que un desarrollador nuevo entienda que este test verifica que la app arranca sin que explote la configuracion.
    @Test
    void debeArrancarContextoDeSpringSinErrores() {
    }

}