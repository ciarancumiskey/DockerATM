package cumiskey.ciaran.DockerATM;

import cumiskey.ciaran.DockerATM.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DatabaseInitialiser {

  private static final Logger logger = LoggerFactory.getLogger(DatabaseInitialiser.class);

  @Bean
  CommandLineRunner initialiseDatabase(CustomerRepository repo) {
    return args -> {
      //Preload the customers into the database
      repo.save(new Customer("123456789", "1234", BigDecimal.valueOf(800), BigDecimal.valueOf(200)));
      repo.save(new Customer("987654321", "4321", BigDecimal.valueOf(1230), BigDecimal.valueOf(150)));
    };
  }
}
