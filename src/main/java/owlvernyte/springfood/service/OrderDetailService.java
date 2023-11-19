package owlvernyte.springfood.service;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.OrderDetail;
import owlvernyte.springfood.repository.OrderDetailRepository;

import java.util.ArrayList;
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

    public List<OrderDetail> getTop4ProductsByProductIdCount() {
        // Gọi phương thức tương ứng từ repository để lấy top 4 món có Product ID nhiều nhất
        return orderDetailRepository.findTop4ProductsByProductIdCount();
    }

    public List<Long> getTopFourProductIdsByQuantity() {
        Pageable pageable = PageRequest.of(0, 5); // Lấy 4 sản phẩm đầu tiên
        List<Object[]> results = orderDetailRepository.findTopFourProductIdsByQuantity(pageable);

        List<Long> topFourProductIds = new ArrayList<>();

        for (Object[] result : results) {
            Long productId = (Long) result[0];
            topFourProductIds.add(productId);
        }

        return topFourProductIds;
    }
}