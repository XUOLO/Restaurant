package owlvernyte.springfood.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {


}
