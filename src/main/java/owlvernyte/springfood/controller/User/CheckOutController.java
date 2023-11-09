package owlvernyte.springfood.controller.User;


import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;

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
    @Autowired
    private JavaMailSender mailSender;


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


          String address = (String) session.getAttribute("address");
        String email = (String) session.getAttribute("email");
        String phone = (String) session.getAttribute("phone");
        model.addAttribute("username", username);
        model.addAttribute("name", name);
        model.addAttribute("address", address);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("userId", userId);
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
                        String errorMessage = "Sản phẩm '" + product.getName() + "' không đủ số lượng.";
                        errorMessages.add(errorMessage);

                    }
                }
            }
        }
        if (!errorMessages.isEmpty()) {
            // Có ít nhất một sản phẩm không đủ số lượng, thực hiện xử lý thông báo lỗi, ví dụ: đẩy danh sách thông báo lỗi vào model và trả về trang lỗi
            model.addAttribute("errorMessages", errorMessages);
            return  "User/ErrorPage" ;
        }
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
                         String errorMessage = "Sản phầm '" + product.getName() + "' không đủ số lượng.";
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

        NumberFormat formatterTotal = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPriceTotal = formatterTotal.format(order.getTotal());
//        String subject = "Thông tin đơn hàng #" + order.getCode();
         MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Đặt các thuộc tính của email
            helper.setTo(email);
            helper.setSubject("Order info #" + order.getCode());

            // Đặt nội dung email dưới dạng HTML
            String tableContent = "<table style=\"border-collapse: collapse;\">";
            tableContent += "<tr style=\"background-color: #f8f8f8;\"><th style=\"padding: 10px; border: 1px solid #ddd;\">Dishes Name</th><th style=\"padding: 10px; border: 1px solid #ddd;\">Quantity</th><th style=\"padding: 10px; border: 1px solid #ddd;\">Price</th></tr>";

            for (CartItem cartItem : cartItems) {
                if (cartItem.getProductId() != null) {
                    Product product = productRepository.findById(cartItem.getProductId()).orElse(null);
                    if (product != null) {
                        tableContent += "<tr>";
                        tableContent += "<td style=\"padding: 10px; border: 1px solid #ddd;\">" + product.getName() + "</td>";
                        tableContent += "<td style=\"padding: 10px; border: 1px solid #ddd;\">" + cartItem.getQuantity() + "</td>";
                        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                        String formattedPrice = formatter.format(product.getPrice());
                        tableContent += "<td style=\"padding: 10px; border: 1px solid #ddd;\">" + formattedPrice + "</td>";
                        tableContent += "</tr>";
                    }
                }
            }

            tableContent += "</table>";

            String htmlContent = "<html><body>";
            htmlContent += "<h2>Order info #" + order.getCode() + "</h2>";
            htmlContent += "<p>Hello " + order.getName() + ",</p>";
            htmlContent += "<p>Thank you for your order. Here are the details about your order:</p>";
            htmlContent += "<p>Order code " + order.getCode() + "</p>";
            htmlContent += "<p>Order date: " + order.getOrderDate() + "</p>";
            htmlContent += "<p>Address: " + order.getAddress() + "</p>";
            htmlContent += "<p>Total: " + formattedPriceTotal + "</p>";
            htmlContent += tableContent;
            htmlContent += "</body></html>";

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "email fail.");
            return "User/ErrorPage";
        }
        shoppingCartService.clear();

        return "redirect:/user/checkOutSuccess";
    }

    @GetMapping("/user/checkOutSuccess")
    public String getLastOrder(Model model,HttpSession session,Principal principal) {
        String name = (String) session.getAttribute("name");

        model.addAttribute("name", name);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());


        return "User/checkOutSuccess";
    }
    @GetMapping("user/vnpay_return")
    public String returnPay(){
        return "User/vnpay_return";
    }

}
