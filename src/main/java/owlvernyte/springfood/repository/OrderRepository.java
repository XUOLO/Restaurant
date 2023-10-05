package owlvernyte.springfood.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import owlvernyte.springfood.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT MAX(o.id) FROM Order o")
    Long findLastOrderId();


    List<Order> findByUserId(long userId);

}
