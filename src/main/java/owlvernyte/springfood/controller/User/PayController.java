package owlvernyte.springfood.controller.User;

import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import owlvernyte.springfood.common.Config;
import owlvernyte.springfood.entity.*;
import owlvernyte.springfood.repository.OrderDetailRepository;
import owlvernyte.springfood.repository.OrderRepository;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.CategoryService;
import owlvernyte.springfood.service.OrderService;
import owlvernyte.springfood.service.ShoppingCartService;
import owlvernyte.springfood.service.UserService;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@RestController
public class PayController {
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
    @GetMapping("/user/pay")
    public RedirectView  payment() throws UnsupportedEncodingException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount =   shoppingCartService.getAmount()*100;
        String bankCode = "NCB";

        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";

        String vnp_TmnCode = Config.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;



        return  new RedirectView (paymentUrl);
    }
    @GetMapping("/user/payment-infor")
    public ModelAndView transaction(@RequestParam(value = "vnp_Amount") String amount,
                                    @RequestParam(value = "vnp_TxnRef") String vnp_TxnRef,
                                    @RequestParam(value = "vnp_BankCode") String bankCode,
                                    @RequestParam(value = "vnp_ResponseCode") String responseCode,
                                    HttpSession session,
                                    @ModelAttribute("order") Order order) {
        ModelAndView modelAndView = new ModelAndView();

        if (responseCode.equals("00")) {
            String username = (String) session.getAttribute("username");
            Long userId = (Long) session.getAttribute("userId");
            User user = userService.viewById(userId);
            String name = (String) session.getAttribute("name");
            String address = (String) session.getAttribute("address");
            String email = (String) session.getAttribute("email");
            String phone = (String) session.getAttribute("phone");
            Blob userImage = (Blob) session.getAttribute("userImage");

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
                            String errorMessage = "Sản phẩm '" + product.getName() + "' không đủ số lượng.";
                            errorMessages.add(errorMessage);

                        }
                    }
                }
            }
            if (!errorMessages.isEmpty()) {
                // Có ít nhất một sản phẩm không đủ số lượng, thực hiện xử lý thông báo lỗi, ví dụ: đẩy danh sách thông báo lỗi vào model và trả về trang lỗi
                modelAndView.addObject("errorMessages", errorMessages);
                modelAndView.setViewName("User/ErrorPage");
            }
            Random random = new Random();
            int randomNumber = random.nextInt(900000) + 100000;
            String code = "XL" + String.valueOf(randomNumber);
            order.setCode("XL"+vnp_TxnRef);
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
                helper.setSubject("Thông tin đơn #" + order.getCode());

                // Đặt nội dung email dưới dạng HTML
                String tableContent = "<table style=\"border-collapse: collapse;\">";
                tableContent += "<tr style=\"background-color: #f8f8f8;\"><th style=\"padding: 10px; border: 1px solid #ddd;\">Tên món</th><th style=\"padding: 10px; border: 1px solid #ddd;\">Số lượng</th><th style=\"padding: 10px; border: 1px solid #ddd;\">Đơn giá</th></tr>";

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
                htmlContent += "<h2>Thông tin  đơn hàng #" + order.getCode() + "</h2>";
                htmlContent += "<p>Xin chào " + order.getName() + ",</p>";
                htmlContent += "<p>Cảm ơn bạn đã đặt hàng. Dưới đây là chi tiết về đơn đặt hàng của bạn:</p>";
                htmlContent += "<p>Mã đơn hàng " + order.getCode() + "</p>";
                htmlContent += "<p>Ngày đặt hàng: " + order.getOrderDate() + "</p>";
                htmlContent += "<p>Địa chỉ: " + order.getAddress() + "</p>";
                htmlContent += "<p>Tổng cộng: " + formattedPriceTotal + "</p>";
                htmlContent += tableContent;
                htmlContent += "</body></html>";

                helper.setText(htmlContent, true);

                mailSender.send(message);

            } catch (Exception e) {
                e.printStackTrace();
                modelAndView.addObject("errorMessage", "email fail.");
                modelAndView.setViewName("User/ErrorPage");
            }
            shoppingCartService.clear();

            modelAndView.addObject("listCategory", categoryService.getAllCategory());
            modelAndView.setViewName("User/checkOutSuccess");
        } else {
            modelAndView.addObject("listCategory", categoryService.getAllCategory());
            modelAndView.setViewName("User/checkOutFail");
        }

        return modelAndView;
    }
    @GetMapping("/user/save-meal")
    public String saveMeal(@RequestParam("id") String mealId,
                           @RequestParam("name") String mealName,
                           @RequestParam("image") String mealImage) throws UnsupportedEncodingException {

        String decodedName = URLDecoder.decode(mealName, "UTF-8");
        String decodedImage = URLDecoder.decode(mealImage, "UTF-8");

        // Sử dụng các giá trị đã giải mã
        System.out.println(decodedName);
        System.out.println(decodedImage);

        return "Meal saved successfully!";
    }


}
