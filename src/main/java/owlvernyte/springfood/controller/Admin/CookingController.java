package owlvernyte.springfood.controller.Admin;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import owlvernyte.springfood.entity.*;
import owlvernyte.springfood.repository.BookingRepository;
import owlvernyte.springfood.repository.CookingRepository;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.CookingService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CookingController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CookingRepository cookingRepository;
    @Autowired
    private CookingService cookingService;
    @Autowired
    private BookingRepository bookingRepository;


    @GetMapping("/admin/list_cooking")
    public String showListCooking(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        List<Cooking> cookingList = cookingRepository.findAll();

        List<String> uniqueOrderCodes = cookingList.stream()
                .map(Cooking::getOrderCode)
                .distinct()
                .collect(Collectors.toList());

        List<Cooking> filteredCookingList = new ArrayList<>();

        for (Cooking cooking : cookingList) {
            String orderCode = cooking.getOrderCode();
            Booking booking = bookingRepository.findByCode(orderCode);
            if (booking != null && (booking.getStatus().equals("1") || booking.getStatus().equals("2"))) {
                filteredCookingList.add(cooking);
            }
        }

        model.addAttribute("uniqueOrderCodes", uniqueOrderCodes);
        model.addAttribute("cookingList", filteredCookingList);

        List<OrderDTO> orderDTOList = new ArrayList<>();

        for (String orderCode : uniqueOrderCodes) {
            Booking booking = bookingRepository.findByCode(orderCode);
            if (booking != null && (booking.getStatus().equals("1") || booking.getStatus().equals("2"))) {
                OrderDTO orderDTO = new OrderDTO();
                orderDTO.setOrderCode(orderCode);
                orderDTO.setStatus(booking.getStatus());
                orderDTOList.add(orderDTO);
            }
        }

        model.addAttribute("orderDTOList", orderDTOList);


        return "Admin/chef_order";
    }

}
