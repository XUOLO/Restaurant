package owlvernyte.springfood.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Contact;
import owlvernyte.springfood.entity.Product;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query("SELECT t FROM Contact t  WHERE CONCAT(t.name, t.phone, t.email, t.message) LIKE %?1%")
    List<Contact> findAll(String keyword);
}
