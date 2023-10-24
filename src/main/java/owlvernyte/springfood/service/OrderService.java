package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.repository.OrderRepository;

import java.util.ArrayList;
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

    public List<Order> searchOrders(Long userId, String keyword) {
        List<Order> orders = orderRepository.findByUserId(userId); // Lấy danh sách tất cả các orders của user
        List<Order> matchedOrders = new ArrayList<>(); // Tạo danh sách rỗng để lưu các orders khớp với từ khóa tìm kiếm

         for (Order order : orders) {
            String keywordLowerCase = keyword.toLowerCase();
            String subjectLowerCase = order.getCode().toLowerCase();
            String phoneLowerCase = order.getPhone().toLowerCase();
             if (subjectLowerCase.contains(keywordLowerCase) || phoneLowerCase.contains(keywordLowerCase)) {
                matchedOrders.add(order);
            }
        }

        return matchedOrders;
    }


    public List<Order> searchOrderAdmin(String keyword) {

        if(keyword!=null){
            return orderRepository.findAll(keyword);
        }
        return orderRepository.findAll();
    }


    public Long CountOrder() {
        return orderRepository.count();
    }

    public Long countHandlingOrder() {
        return orderRepository.countHandlingOrder();
    }

    public Long countConfirmedOrder() {
        return orderRepository.countConfirmedOrder();
    }

    public Long countCancelOrder() {
        return orderRepository.countCancelOrder();
    }
    public Page<Order> findPaginatedOrder(int pageNo, int pageSize){
        Pageable pageable= PageRequest.of(pageNo - 1,pageSize);
        return this.orderRepository.findAll(pageable);
    }

}
