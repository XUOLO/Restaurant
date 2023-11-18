package owlvernyte.springfood.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import owlvernyte.springfood.entity.Order;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    long countByOrderDate(LocalDate orderDate);
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



    List<Order> findByOrderDate(LocalDate date);

    @Query("SELECT o FROM Order o WHERE MONTH(o.orderDate) = :month AND YEAR(o.orderDate) = :year")
    List<Order> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT o FROM Order o WHERE YEAR(o.orderDate) = :year")
    List<Order> findByYear(@Param("year") int year);

    public default List<Order> findByOrderDateBetween(LocalDate startDate, LocalDate endDate) {

        List<Order> allOrders = findAll();
        List<Order> ordersInRange = new ArrayList<>();

        for (Order order : allOrders) {
            LocalDate orderDate = order.getOrderDate();
            if (orderDate.isEqual(startDate) || orderDate.isEqual(endDate) || (orderDate.isAfter(startDate) && orderDate.isBefore(endDate))) {
                ordersInRange.add(order);
            }
        }

        return ordersInRange;
    }

}
