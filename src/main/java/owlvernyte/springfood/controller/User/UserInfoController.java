package owlvernyte.springfood.controller.User;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.OrderDetail;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.OrderRepository;
import owlvernyte.springfood.repository.ProductCategoryRepository;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.service.*;

import java.security.Principal;
import java.util.List;

@Controller
public class UserInfoController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @GetMapping("/user/info")
    public String showInfo(Model model, HttpSession session, Principal principal){

        String name = (String) session.getAttribute("name");
        User user = (User) session.getAttribute("user");
        String username = (String) session.getAttribute("username");
        String address = (String) session.getAttribute("address");
        String email = (String) session.getAttribute("email");
        String phone = (String) session.getAttribute("phone");
        User userImage = (User) session.getAttribute("userImage");

        model.addAttribute("name", name);
        model.addAttribute("username", username);
        model.addAttribute("address", address);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("user", user);
        model.addAttribute("userImage", userImage);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());
         return "User/userInfo";
    }



    @GetMapping("/user/userOrderInfo")
    public String userOrderInfo(Model model, HttpSession session, Principal principal){


        String name = (String) session.getAttribute("name");
        User user = (User) session.getAttribute("user");
        String username = (String) session.getAttribute("username");
        String address = (String) session.getAttribute("address");
        String email = (String) session.getAttribute("email");
        String phone = (String) session.getAttribute("phone");
        User userImage = (User) session.getAttribute("userImage");
        long userId = ((User) session.getAttribute("user")).getId();

        model.addAttribute("name", name);
        model.addAttribute("userId", userId);
        model.addAttribute("username", username);
        model.addAttribute("address", address);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("user", user);
        model.addAttribute("userImage", userImage);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());


        List<Order> userOrders = orderRepository.findByUserId(userId);
        model.addAttribute("userOrders", userOrders);


        return "User/userOrderInfo";
    }



    @GetMapping("/user/orderDetail/{id}")
    public String showUserOrderDetail( HttpSession session, Principal principal,Authentication authentication, @PathVariable(value = "id") long id, Model model) {

        String name = (String) session.getAttribute("name");
        User user = (User) session.getAttribute("user");
        String username = (String) session.getAttribute("username");
        String address = (String) session.getAttribute("address");
        String email = (String) session.getAttribute("email");
        String phone = (String) session.getAttribute("phone");
        User userImage = (User) session.getAttribute("userImage");
        long userId = ((User) session.getAttribute("user")).getId();

        model.addAttribute("name", name);
        model.addAttribute("userId", userId);
        model.addAttribute("username", username);
        model.addAttribute("address", address);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("user", user);
        model.addAttribute("userImage", userImage);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());



        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);

        // Lấy danh sách OrderDetail theo Order
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrder(order);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("totalAmount",order.getTotal());



        return "User/orderDetailUser";
    }
    @PostMapping("/user/{id}/updateOrderStatus")
    public String updateOrderStatus(@PathVariable("id") Long id, @RequestParam("status") String status, Model model, HttpSession session, HttpServletRequest request) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid order id: " + id));
        order.setStatus(status);
        orderRepository.save(order);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

}
