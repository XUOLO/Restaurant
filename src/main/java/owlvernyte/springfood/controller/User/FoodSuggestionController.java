package owlvernyte.springfood.controller.User;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import owlvernyte.springfood.entity.*;
import owlvernyte.springfood.repository.OrderDetailRepository;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.repository.RatingRepository;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.ProductService;
import owlvernyte.springfood.service.UserService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Controller
public class FoodSuggestionController {


    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductService productService;
    @Autowired
    OrderDetailRepository orderDetailRepository;
    @Autowired
    UserService userService;

    @GetMapping("/user/recommendation")
    public String getRecommendedProducts(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        String notebookScript = "C:\\Users\\admin\\Desktop\\dacn\\Restaurant\\recommend.py";
        Long parameter = userId;


        Process process;
        try {
            process = Runtime.getRuntime().exec("python " + notebookScript + " " + parameter);


            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();
            boolean startReading = false;
            while ((line = reader.readLine()) != null) {
                if (line.equals("top 5")) {
                    startReading = true;
                } else if (startReading) {
                    result.append(line);
                    result.append(System.lineSeparator());
                }
            }


            String[] recommendations = result.toString().split(System.lineSeparator());
            for (String recommendation : recommendations) {

                System.out.println(recommendation);
            }
            // Truy xuất danh sách Product từ cơ sở dữ liệu
            List<Product> productList = productService.getAllProduct();
            // Tạo danh sách các sản phẩm được gợi ý
            List<Product> recommendedProducts = new ArrayList<>();
            for (String recommendation : recommendations) {
                Long productId = Long.parseLong(recommendation);
                for (Product product : productList) {
                    if (product.getId().equals(productId)) {
                        recommendedProducts.add(product);
                        break;
                    }
                }
            }


            // Gửi danh sách sản phẩm được gợi ý đến view
            model.addAttribute("recommendedProducts", recommendedProducts);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "User/recommendations";
    }

    @Autowired
    private RatingRepository ratingRepository;

    @GetMapping("/user/ratings/csv")
    public ResponseEntity<byte[]> exportRatingsToCSV(HttpServletResponse response) throws IOException {
        List<Rating> ratings = ratingRepository.findAll();

        StringBuilder csvContent = new StringBuilder();
        csvContent.append("userId,productId,rating\n");

        for (Rating rating : ratings) {
            csvContent.append(rating.getUser().getId())
                    .append(",")
                    .append(rating.getProduct().getId())
                    .append(",")
                    .append(rating.getRatingValue())
                    .append("\n");
        }

        byte[] csvBytes = csvContent.toString().getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "ratings.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }
    @GetMapping("/user/order-details/csv")
    public ResponseEntity<byte[]> exportOrderDetailsToCSV(HttpServletResponse response) throws IOException {
        List<OrderDetail> orderDetails = orderDetailRepository.findAll();

        // Tạo danh sách ID sản phẩm từ bảng Product
        List<Long> productIds = new ArrayList<>();
        for (OrderDetail orderDetail : orderDetails) {
            Long productId = orderDetail.getProduct().getId();
            if (!productIds.contains(productId)) {
                productIds.add(productId);
            }
        }

        StringBuilder csvContent = new StringBuilder();

        // Thêm header vào file CSV
        StringBuilder headerRow = new StringBuilder();
        for (Long productId : productIds) {
            headerRow.append(productId).append(",");
        }
        csvContent.append(headerRow.toString().trim()).append("\n");

        // Xây dựng danh sách ID sản phẩm cho từng đơn hàng
        Map<Long, List<Long>> orderProductMap = new HashMap<>();

        for (OrderDetail orderDetail : orderDetails) {
            Long orderId = orderDetail.getOrder().getId();
            Long productId = orderDetail.getProduct().getId();

            List<Long> products = orderProductMap.getOrDefault(orderId, new ArrayList<>());
            products.add(productId);
            orderProductMap.put(orderId, products);
        }

        // Xây dựng các hàng trong file CSV
        for (Long orderId : orderProductMap.keySet()) {
            List<Long> products = orderProductMap.get(orderId);
            StringBuilder dataRow = new StringBuilder();

            // Kiểm tra sự có mặt của sản phẩm trong OrderDetail
            for (Long productId : productIds) {
                if (products.contains(productId)) {
                    dataRow.append("1,");
                } else {
                    dataRow.append("0,");
                }
            }
            if (productIds.isEmpty()) {
                dataRow.append("0");
            }
            csvContent.append(dataRow.toString().trim()).append("\n");
        }

        byte[] csvBytes = csvContent.toString().getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set(HttpHeaders.CONTENT_ENCODING, StandardCharsets.UTF_8.name());
        headers.setContentDispositionFormData("attachment", "order_details.csv");
        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/user/products/csv")
    public ResponseEntity<byte[]> exportProductsToCSV(HttpServletResponse response) throws IOException {
        List<Product> products = productRepository.findAll();

        StringBuilder csvContent = new StringBuilder();
        csvContent.append("productId,title,genres\n");

        for (Product product : products) {
            csvContent.append(product.getId())
                    .append(",")
                    .append(product.getName())
                    .append(",")
                    .append(product.getProductCategory().getName())
                    .append("\n");
        }

        byte[] csvBytes = csvContent.toString().getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "products.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }

}


