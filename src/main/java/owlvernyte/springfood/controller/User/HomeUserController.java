package owlvernyte.springfood.controller.User;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import owlvernyte.springfood.entity.*;
import owlvernyte.springfood.repository.CommentRepository;
import owlvernyte.springfood.repository.ProductCategoryRepository;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.repository.RatingRepository;
import owlvernyte.springfood.service.*;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeUserController {

    @Autowired
    private ProductService productService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CsvReader csvReader;
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
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private RatingRepository ratingRepository;

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
//        String csvFilePath = "C:\\Users\\admin\\Downloads\\recommendation.csv";
//
//        // Lấy danh sách sản phẩm khớp từ tệp CSV và cơ sở dữ liệu
//        List<Integer> matchingProductIds = csvReader.getMatchingProductsFromCsv(csvFilePath,userId);
//
//        List<Product> matchingProducts = new ArrayList<>();
//
//        for (Integer productId : matchingProductIds) {
//            Product product = productService.getProductById(productId);
//            if (product != null) {
//                matchingProducts.add(product);
//            }
//        }
//
//        model.addAttribute("matchingProducts", matchingProducts);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());
        model.addAttribute("listProduct", sellingProducts);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);

        List<Long> topFourProductIds = orderDetailService.getTopFourProductIdsByQuantity();
        List<Product> topFourProducts = productService.getTopFourProductsById(topFourProductIds);
        model.addAttribute("top5FourProducts", topFourProducts);
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


    @GetMapping("/user/home")
    public String viewHomepage(  Model model,Principal principal,HttpSession session) {


        List<Product> productList = productService.getAllProduct();


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



        return "User/home";
    }


//    @GetMapping("/user/login")
//    public String userLogin(Principal principal, Model model) {
//
//        return "User/login";
//
//    }
//    @GetMapping("/user/international")
//    public String international(@RequestParam("lang") String lang, HttpServletRequest request   ) {
//
//        request.getSession().setAttribute("lang", lang);
//        String referer = request.getHeader("Referer");
//        return "redirect:" + referer;
//
//    }



    @GetMapping("/user/productDetail/{id}")
    public String getProductDetail(@PathVariable Long id, Model model,HttpSession session,Principal principal) {
        Product product = productService.getProductById(id);

        if (product == null) {
            // Xử lý trường hợp sản phẩm không tồn tại
            return "error";
        }



        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("userId",userId);
        List<Rating> ratings = ratingRepository.findByProduct(product);
        double averageRating = calculateAverageRating(ratings);
        model.addAttribute("product", product);
        model.addAttribute("productId",product.getId());
        model.addAttribute("averageRating", averageRating);
        String username = (String) session.getAttribute("username");
        String name = (String) session.getAttribute("name");
        model.addAttribute("username", username);
        model.addAttribute("name", name);
        Comment comment= new Comment();
        model.addAttribute("comment",comment);
        int commentCount = commentRepository.countCommentsByProductId(id);
        model.addAttribute("commentCount",commentCount);
        Sort sort = Sort.by(Sort.Direction.DESC, "commentDate");
        List<Comment> commentList = commentRepository.findByProduct(product, sort);
        model.addAttribute("commentProduct", commentList);

        model.addAttribute("listCategory", categoryService.getAllCategory());
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        return "User/detailproduct";
    }
    private double calculateAverageRating(List<Rating> ratings) {
        if (ratings.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (Rating rating : ratings) {
            sum += rating.getRatingValue();
        }

        return sum / ratings.size();
    }
}
