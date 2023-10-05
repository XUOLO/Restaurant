package owlvernyte.springfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.OrderDetail;

import java.util.List;

@Repository
public interface OrderDetailRepository  extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrder(Order order);

}
