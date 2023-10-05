package owlvernyte.springfood.controller.User;


import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import owlvernyte.springfood.entity.*;
import owlvernyte.springfood.repository.OrderDetailRepository;
import owlvernyte.springfood.repository.OrderRepository;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.CategoryService;
import owlvernyte.springfood.service.OrderService;
import owlvernyte.springfood.service.ShoppingCartService;
import owlvernyte.springfood.service.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Controller
public class CheckOutController
{
@Autowired
private OrderRepository orderRepository;
@Autowired
private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductRepository productRepository;

//    @GetMapping("/user/checkOut")
//    public String showCheckOut(Model model){
//
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication == null || !authentication.isAuthenticated()) {
//
//            return "Admin/login";
//        }
//        Collection<CartItem> allCartItems = shoppingCartService.getAllCartItem();
//
//        model.addAttribute("AllCartItem", allCartItems);
//        model.addAttribute("listCategory", categoryService.getAllCategory());
//        model.addAttribute("totalAmount", shoppingCartService.getAmount());
//        return "User/checkOut";
//    }
    @PostMapping("/user/checkOut")
    public String showCheckOut(Model model, Authentication authentication,Principal principal,HttpSession session) {

        if (authentication == null || !authentication.isAuthenticated()) {

            return "Admin/login";
        }
        Collection<CartItem> allCartItems = shoppingCartService.getAllCartItem();
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("AllCartItem", allCartItems);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("totalAmount", shoppingCartService.getAmount());



        String username = (String) session.getAttribute("username");
        String name = (String) session.getAttribute("name");
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("username", username);
        model.addAttribute("name", name);
        model.addAttribute("userId", userId);

        return "User/checkOut";
    }


    @PostMapping("/user/placeOrder")
    public String placeOrder(@RequestParam("name") String name,
                             @RequestParam("address") String address,
                             @RequestParam("phone") String phone,
                             @RequestParam("email") String email,
                             @ModelAttribute("order") Order order, Model model,
                             HttpSession session) {
        String username = (String) session.getAttribute("username");
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.viewById(userId);

        order.setName(name);
        order.setAddress(address);
        order.setPhone(phone);
        order.setEmail(email);
        order.setOrderDate(LocalDate.now());
        order.setUser(user);
        order.setTotal(shoppingCartService.getAmount());
        order.setStatus("1");

        Collection<CartItem> cartItems = shoppingCartService.getAllCartItem();
        List<String> errorMessages = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductId() != null) {
                Long productId = cartItem.getProductId();
                int quantityToOrder = cartItem.getQuantity();

                Product product = productRepository.findById(productId).orElse(null);
                if (product != null) {
                    int availableQuantity = product.getQuantity();
                    if (quantityToOrder > availableQuantity) {
                         String errorMessage = "Product '" + product.getName() + "' not enough quantity.";
                        errorMessages.add(errorMessage);

                    }
                }
            }
        }

        if (!errorMessages.isEmpty()) {
            // Có ít nhất một sản phẩm không đủ số lượng, thực hiện xử lý thông báo lỗi, ví dụ: đẩy danh sách thông báo lỗi vào model và trả về trang lỗi
             model.addAttribute("errorMessages", errorMessages);
            return "User/ErrorPage";
        }
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000;
        String code = "XL" + String.valueOf(randomNumber);
        order.setCode(code);
        orderRepository.save(order);
        Long orderId = order.getId();

        // Trừ số lượng sản phẩm trong cơ sở dữ liệu
        for (CartItem cartItem : cartItems) {
            if (cartItem.getProductId() != null) {
                Long productId = cartItem.getProductId();
                int quantityToOrder = cartItem.getQuantity();

                Product product = productRepository.findById(productId).orElse(null);
                if (product != null) {
                    int availableQuantity = product.getQuantity();
                    int updatedQuantity = availableQuantity - quantityToOrder;
                    product.setQuantity(updatedQuantity);
                    productRepository.save(product);
                }
            }
        }

         for (CartItem cartItem : cartItems) {
            if (cartItem.getProductId() != null) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrder(order);

                Long productId = cartItem.getProductId();
                Product product = productRepository.findById(productId).orElse(null);
                if (product != null) {
                    orderDetail.setProduct(product);
                    orderDetail.setPrice(product.getPrice());
                    orderDetail.setQuantity(cartItem.getQuantity());

                    orderDetailRepository.save(orderDetail);
                }
            }
        }



        shoppingCartService.clear();

        return "User/checkOutSuccess";
    }




}
