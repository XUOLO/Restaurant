package owlvernyte.springfood.controller.Admin;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.ProductCategoryService;
import owlvernyte.springfood.service.ProductService;
import owlvernyte.springfood.service.UserService;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/display")
    public ResponseEntity<byte[]> displayImage(@RequestParam("id") long id) throws IOException, SQLException
    {
        Product product = productService.viewById(id);
        byte [] imageBytes = null;
        imageBytes = product.getImage().getBytes(1,(int) product.getImage().length());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }
    @GetMapping("/admin/list_product")
    public String showListProduct(Authentication authentication,Model model) {



        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        boolean isEmployee = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_EMPLOYEE"));
        model.addAttribute("user", user);

        model.addAttribute("username", username);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isEmployee", isEmployee);
        model.addAttribute("listProduct", productService.getAllProduct());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());

        return "Admin/list_product";
    }


    @GetMapping("/admin/new_product")
    public String showNewProduct(Authentication authentication,Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        model.addAttribute("listProductCategory",  productCategoryService.getAllProductCategory());
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        return "Admin/new_product";
    }


    @PostMapping("/admin/addProduct")
    public String addProduct(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult, @RequestParam("image") MultipartFile file, Model model) throws IOException, SerialException, SQLException {
        if (file.isEmpty()) {
            product.setImage(null);
        } else {
            byte[] bytes = file.getBytes();
            Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
            product.setImage(blob);
        }
        product.setCreateTime(LocalDateTime.now());
        product.setStatus("2");
        productService.saveProduct(product);
        return "redirect:/admin/list_product";
    }






    @PostMapping("/admin/{id}/updateStatus")
    public String updateProductStatus(@PathVariable("id") Long id, @RequestParam("status") String status, Model model, HttpSession session, HttpServletRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid ticket id: " + id));
        product.setStatus(status);
        productRepository.save(product);
        String referer = request.getHeader("Referer");


        return "redirect:" + referer;
    }



    @PostMapping("/admin/updateProduct")
    public String updateProduct(@ModelAttribute("product") @Valid  Product product, BindingResult bindingResult,
                           Model model,
                           @RequestParam("image") MultipartFile file) throws IOException, SerialException, SQLException {

        if (file.isEmpty()) {
            product.setImage(null);
        } else {
            byte[] bytes = file.getBytes();
            Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
            product.setImage(blob);
        }

            productService.saveProduct(product);
            return "redirect:/admin/list_product";

    }
    @GetMapping("/admin/showFormForUpdateProduct/{id}")
    public String showFormForUpdateSP(Authentication authentication,@PathVariable(value = "id") long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        return "Admin/update_product";
    }
    @GetMapping("/admin/deleteProduct/{id}")
    public String deleteProduct(@PathVariable(value = "id") long id) {
        this.productService.deleteProductById(id);
        return "redirect:/admin/list_product";
    }




}
