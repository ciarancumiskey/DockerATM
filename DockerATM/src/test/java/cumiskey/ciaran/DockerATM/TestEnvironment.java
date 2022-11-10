package cumiskey.ciaran.DockerATM;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@SuppressWarnings("rawtypes")
public class TestEnvironment {

    private static TestEnvironment testEnvInstance;
    private static AtomicBoolean initialised = new AtomicBoolean(false);
    private static Object mutex = new Object();
    private DockerComposeContainer composedTestContainer;
    private static Logger logger = LoggerFactory.getLogger("TestEnvironment");

    @Synchronized
    public static final TestEnvironment start() {
        if(testEnvInstance == null) {
            synchronized (mutex) {
                if(testEnvInstance == null) {
                    testEnvInstance = new TestEnvironment();
                }
            }
        }
        try {
            if(!initialised.get()) {
                testEnvInstance.initialise();
                initialised.set(true);
            }
        } catch (final Exception ex) {
            logger.error("Failed to start Docker Compose process for testing. {}", ex);
        }
        return testEnvInstance;
    }

    @SuppressWarnings("resource")
    private void initialise() throws Exception {
        composedTestContainer = new DockerComposeContainer(
                new File("src/test/resources/docker-compose.yaml"))
                .waitingFor("postgres", Wait.forHealthcheck().withStartupTimeout(Duration.ofMinutes(1)))
                .withLocalCompose(true);
        composedTestContainer.start();
    }
}
