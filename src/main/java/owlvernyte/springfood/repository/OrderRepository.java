package owlvernyte.springfood.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import owlvernyte.springfood.entity.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT MAX(o.id) FROM Order o")
    Long findLastOrderId();

    @Query("SELECT o FROM Order o WHERE CONCAT(o.name, o.code, o.phone, o.email, o.address) LIKE %?1%")
    List<Order> findAll(String keyword);
    List<Order> findByUserId(long userId);

    Page<Order> findOrderByUserId(long userId, Pageable pageable);

    @Query("SELECT COUNT(t) FROM Order t WHERE t.status = '1'")
    Long countHandlingOrder();
    @Query("SELECT COUNT(t) FROM Order t WHERE t.status = '2'")
    Long countConfirmedOrder();
    @Query("SELECT COUNT(t) FROM Order t WHERE t.status = '3'")
    Long countCancelOrder();

    List<Order> findByOrderDate(LocalDate orderDate);

}
