package owlvernyte.springfood.controller.Admin;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import owlvernyte.springfood.entity.ProductCategory;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.ProductCategoryService;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.sql.SQLException;

@Controller
public class ProductCategoryController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryService productCategoryService;


    @GetMapping("/admin/list_productCategory")
    public String showListProductCategory(Authentication authentication, Model model) {

        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
        boolean isEmployee = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("EMPLOYEE"));
         User user = userRepository.findByUsername(username);
        model.addAttribute("username", username);
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isEmployee", isEmployee);
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());


        return "Admin/list_productCategory";
    }

    @GetMapping("/admin/new_productCategory")
    public String showNewProductCategory(Authentication authentication,Model model) {
        ProductCategory productCategory = new ProductCategory();
        model.addAttribute("productCategory", productCategory);
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("username", username);

        model.addAttribute("user", user);
        return "Admin/new_productCategory";
    }
    @PostMapping("/admin/addProductCategory")
    public String addProductCategory(@Valid @ModelAttribute("productCategory") ProductCategory productCategory, BindingResult bindingResult, Model model) throws IOException, SerialException, SQLException {



        productCategoryService.saveProductCategory(productCategory);
        return "redirect:/admin/list_productCategory";

    }


    @GetMapping("/admin/showFormForUpdateProductCategory/{id}")
    public String showFormForUpdateProductCategory(Authentication authentication,@PathVariable(value = "id") long id, Model model) {
        ProductCategory productCategory = productCategoryService.getProductCategoryById(id);
        model.addAttribute("productCategory", productCategory);
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("username", username);

        model.addAttribute("user", user);
        return "Admin/update_productCategory";
    }
    @GetMapping("/admin/deleteProductCategory/{id}")
    public String deleteProduct(@PathVariable(value = "id") long id) {
        this.productCategoryService.deleteProductCategoryById(id);
        return "redirect:/admin/list_productCategory";
    }

    @PostMapping("/admin/updateProductCategory")
    public String updateProduct(@ModelAttribute("productCategory") @Valid ProductCategory productCategory, BindingResult bindingResult,
                                Model model
                              ) throws IOException, SerialException, SQLException {



        productCategoryService.saveProductCategory(productCategory);
        return "redirect:/admin/list_productCategory";

    }
}
