package owlvernyte.springfood.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT t FROM Product t JOIN t.productCategory d WHERE CONCAT(t.name, t.description, d.name, t.price) LIKE %?1%")
    List<Product> findAll(String keyword);

}
