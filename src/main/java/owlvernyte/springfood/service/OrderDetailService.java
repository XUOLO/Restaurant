package owlvernyte.springfood.service;


import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.OrderDetail;
import owlvernyte.springfood.repository.OrderDetailRepository;

import java.util.List;

@Service
public class OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;

    public OrderDetailService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    public List<OrderDetail> getOrderDetailsByOrder(Order order) {
        return orderDetailRepository.findByOrder(order);
    }
}