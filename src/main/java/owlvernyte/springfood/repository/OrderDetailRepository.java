package owlvernyte.springfood.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.OrderDetail;
import owlvernyte.springfood.entity.Product;

import java.util.List;

@Repository
public interface OrderDetailRepository  extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrder(Order order);
    List<OrderDetail> findByProduct(Product product);
    List<OrderDetail> findByOrderIdAndProductIdNot(Long orderId, Long productId);

    @Query(value = "SELECT od.product_id, p.name, p.price\n" +
            "FROM order_detail od\n" +
            "JOIN product p ON od.product_id = p.id\n" +
            "GROUP BY od.product_id, p.name, p.price\n" +
            "ORDER BY SUM(od.quantity) DESC\n" +
            "LIMIT 4", nativeQuery = true)
    List<OrderDetail> findTop4ProductsByProductIdCount();
    @Query(value = "SELECT od.product.id, SUM(od.quantity) AS totalQuantity " +
            "FROM OrderDetail od " +
            "GROUP BY od.product.id " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findTopFourProductIdsByQuantity(Pageable pageable);
}
