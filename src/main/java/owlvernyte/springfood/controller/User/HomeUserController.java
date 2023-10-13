package owlvernyte.springfood.controller.User;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import owlvernyte.springfood.entity.Category;
import owlvernyte.springfood.entity.Contact;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.repository.ProductCategoryRepository;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.service.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeUserController {

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
    @Autowired
    private ReservationCategoryService reservationCategoryService;

    @Autowired
    private ReservationService reservationService;


    @GetMapping("/")
    public String showIndexUser(Model model, Principal principal, HttpSession session, Authentication authentication) {
        List<Product> productList = productService.getAllProduct();
        List<Product> sellingProducts = new ArrayList<>();
        for (Product product : productList) {
            if ("1".equals(product.getStatus())) {
                sellingProducts.add(product);
            }
        }

        String username = (String) session.getAttribute("username");
        String name = (String) session.getAttribute("name");
//        Long userId = (Long) session.getAttribute("userId");
//        User user = userService.viewById(userId);
        model.addAttribute("username", username);
        model.addAttribute("name", name);
//        model.addAttribute("userId", userId);

        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());
        model.addAttribute("listProduct", sellingProducts);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        return "User/home";
    }


    @GetMapping("/user/category/{id}")
    public String getCategoryPage(@PathVariable("id") Long categoryId, Model model,Principal principal,HttpSession session) {
        Category category = categoryService.getCategoryById(categoryId);
        Contact contact= new Contact();
        List<Product> productList = productService.getAllProduct();
        String templateName =   "User/"+  category.getName().toLowerCase()  ;

        model.addAttribute("listProductCategory",productCategoryService.getAllProductCategory());
        model.addAttribute("listProduct",productService.getAllProduct());
        model.addAttribute("listReservation",reservationService.getAllReservation());
        model.addAttribute("listReservationCategory",reservationCategoryService.getAllReservationCategory());
        model.addAttribute("listCategory",categoryService.getAllCategory());
        List<Product> sellingProducts = new ArrayList<>();

        for (Product product : productList) {
            if ("1".equals(product.getStatus())) {
                sellingProducts.add(product);
            }
        }

        String username = (String) session.getAttribute("username");
        String name = (String) session.getAttribute("name");
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("username", username);
        model.addAttribute("name", name);
        model.addAttribute("userId", userId);

        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listProduct", sellingProducts);

        model.addAttribute("contact", contact);

        model.addAttribute("category", category);
        return templateName;
    }

//    @GetMapping("/user/login")
//    public String userLogin(Principal principal, Model model) {
//
//        return "User/login";
//
//    }
    @GetMapping("/user/international")
    public String international(  HttpServletRequest request   ) {

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;

    }

}
