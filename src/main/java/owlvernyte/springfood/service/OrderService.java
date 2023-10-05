package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order getOrderById(long id) {
        Optional<Order> optional = orderRepository.findById(id);
        Order order = null;
        if (optional.isPresent()) {
            order = optional.get();
        }
        else
        {
            throw new RuntimeException(" Cant find product id : " + id);
        }
        return order;
    }

    public List<Order> getOrdersByUserId(long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Long getLastOrderId() {
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).get(0).getId();
    }
}
