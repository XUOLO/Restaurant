package owlvernyte.springfood.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
}
