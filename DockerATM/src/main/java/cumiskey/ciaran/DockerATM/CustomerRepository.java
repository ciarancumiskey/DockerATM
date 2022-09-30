package cumiskey.ciaran.DockerATM;

import cumiskey.ciaran.DockerATM.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

interface CustomerRepository extends JpaRepository<Customer, String> {
}
