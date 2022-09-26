package cumiskey.ciaran.DockerATM;

import cumiskey.ciaran.DockerATM.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseInitialiser {

  private static final Logger logger = LoggerFactory.getLogger(DatabaseInitialiser.class);

  @Bean
  CommandLineRunner initialiseDatabase(CustomerRepository repo) {
    return args -> {
      //Preload the customers into the database
      repo.save(new Customer(123456789L, 1234, 800, 200));
      logger.info("Customer #123456789 created");
      repo.save(new Customer(987654321L, 4321, 1230, 150));
      logger.info("Customer #987654321 created");
    };
  }
}
