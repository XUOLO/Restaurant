package owlvernyte.springfood.controller.User;


import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.repository.ProductCategoryRepository;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.service.CategoryService;
import owlvernyte.springfood.service.ProductCategoryService;
import owlvernyte.springfood.service.ProductService;
import owlvernyte.springfood.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MenuController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;
    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @GetMapping("/user/menu")
    public String viewMenu(Model model, Principal principal, HttpSession session){

        List<Product> productList = productService.getAllProduct();
        List<Product> sellingProducts = new ArrayList<>();

        for (Product product : productList) {
            if ("1".equals(product.getStatus())) {
                sellingProducts.add(product);
            }
        }
        String name = (String) session.getAttribute("name");

        model.addAttribute("name", name);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());
        model.addAttribute("listProduct", sellingProducts);
        return "User/menu";
    }
}
