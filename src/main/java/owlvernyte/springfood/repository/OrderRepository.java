package owlvernyte.springfood.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import owlvernyte.springfood.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT MAX(o.id) FROM Order o")
    Long findLastOrderId();

    @Query("SELECT o FROM Order o WHERE CONCAT(o.name, o.code, o.phone, o.email, o.address) LIKE %?1%")
    List<Order> findAll(String keyword);
    List<Order> findByUserId(long userId);

}
