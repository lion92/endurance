package fr.endurance.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fr.endurance.TestcontainersConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;

/**
 * Un test d'intégration démarre l'application ENTIÈRE sur un vrai PostgreSQL (Testcontainers)
 * et l'interroge par HTTP simulé. Le contexte Spring et le conteneur sont partagés entre les
 * classes de test : on ne paie le démarrage qu'une fois.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
public @interface IntegrationTest {
}
