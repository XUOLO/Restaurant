package owlvernyte.springfood.controller.User;


import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import owlvernyte.springfood.entity.CartItem;
import owlvernyte.springfood.entity.CsvReader;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.service.CategoryService;
import owlvernyte.springfood.service.ProductService;
import owlvernyte.springfood.service.ShoppingCartService;
import owlvernyte.springfood.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Controller
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

        @Autowired
        private CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    private CsvReader csvReader;
    @Autowired
    private UserService userService;
    @GetMapping("/user/viewCart")
    public String viewCart(Model model, Principal principal, HttpSession session) {
        Collection<CartItem> allCartItems = shoppingCartService.getAllCartItem();

        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        if(isAuthenticated){
            Long userId = (Long) session.getAttribute("userId");
            //        Long userId = userService.viewById()
            model.addAttribute("userId",userId);
            model.addAttribute("AllCartItem", allCartItems);
            model.addAttribute("listCategory", categoryService.getAllCategory());
            model.addAttribute("totalAmount", shoppingCartService.getAmount());
            String name = (String) session.getAttribute("name");
            model.addAttribute("name", name);
            boolean hasItems = !allCartItems.isEmpty();
            model.addAttribute("hasItems", hasItems);



// recommendadtion for userid
            String csvFilePath = "C:\\Users\\admin\\Downloads\\recommendation.csv";
            boolean isMatchingUser = csvReader.isMatchingUserFromCsv(csvFilePath, userId);
            // Lấy danh sách sản phẩm khớp từ tệp CSV và cơ sở dữ liệu
            if (isMatchingUser) {
                // Lấy danh sách sản phẩm khớp từ tệp CSV và cơ sở dữ liệu
                List<Integer> matchingProductIds = csvReader.getMatchingProductsFromCsv(csvFilePath, userId);

                List<Product> matchingProducts = new ArrayList<>();

                for (Integer productId : matchingProductIds) {
                    Product product = productService.getProductById(productId);
                    if (product != null) {
                        matchingProducts.add(product);
                    }
                }

                model.addAttribute("matchingProducts", matchingProducts);
            } else {
                // Hiển thị thông báo cho người dùng
                String message = "Hiện tại không có sản phẩm gợi ý cho bạn!.";
                model.addAttribute("message", message);
            }
        }else {
            boolean hasItems = !allCartItems.isEmpty();
            model.addAttribute("hasItems", hasItems);
            model.addAttribute("AllCartItem", allCartItems);
            model.addAttribute("listCategory", categoryService.getAllCategory());
            model.addAttribute("totalAmount", shoppingCartService.getAmount());
            String message = "Hiện tại không có sản phẩm gợi ý cho bạn!.";
            model.addAttribute("message", message);
        }


        return "User/ShoppingCart";
    }




    @GetMapping("/user/shoppingCart/clear")
    public String clearCart(Model model){
         shoppingCartService.clear();
        return "redirect:/user/viewCart";
    }


    @GetMapping("/user/shoppingCart/remove/{id}")
    public String removeItemCart(@PathVariable("id") int id){
        shoppingCartService.remove(id);
        return "redirect:/user/viewCart";
    }
    @PostMapping("/user/shoppingCart/updateCart")
    public String updateItemCart(@RequestParam("productId") Integer productId, @RequestParam("quantity") Integer quantity){
        shoppingCartService.update(productId, quantity);
        return "redirect:/user/viewCart";
    }

}
