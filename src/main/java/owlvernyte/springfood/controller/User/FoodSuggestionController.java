package owlvernyte.springfood.controller.User;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.ProductService;
import owlvernyte.springfood.service.UserService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
@Controller
public class FoodSuggestionController {
    private final FoodSuggestion foodSuggestion;
@Autowired
    public FoodSuggestionController(FoodSuggestion foodSuggestion) {
        this.foodSuggestion = foodSuggestion;
    }

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;

    @GetMapping("/user/recommendation")
    public String getRecommendedProducts(Model model,HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        String notebookScript = "C:\\Users\\admin\\Desktop\\dacn\\recommend.py";
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
}
