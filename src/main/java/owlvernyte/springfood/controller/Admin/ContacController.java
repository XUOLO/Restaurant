package owlvernyte.springfood.controller.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import owlvernyte.springfood.entity.Contact;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.RoleRepository;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.ContactService;
import owlvernyte.springfood.service.RoleService;
import owlvernyte.springfood.service.UserService;

import java.util.List;

@Controller
public class ContacController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ContactService contactService;
    @GetMapping("/admin/list_contact")
    public String listContact(Authentication authentication, Model model){
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
        boolean isEmployee = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("EMPLOYEE"));
        model.addAttribute("user", user);

        model.addAttribute("username", username);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isEmployee", isEmployee);
        model.addAttribute("listContact",contactService.getAllContact());
        return "Admin/list_contact";
    }

    @PostMapping("/admin/contact/search")
    public String searchContact(@RequestParam("keyword") String keyword, Model model , Authentication authentication ) {
        String username = authentication.getName();
        model.addAttribute("listStaff", userService.getAllUser());
        model.addAttribute("listRole", roleService.getAllRole());
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        List<Contact> contacts = contactService.searchContactAdmin(keyword);
        if (contacts.isEmpty()) {
            String errorMessage = "No matching contact found";
            model.addAttribute("errorMessage", errorMessage);
        } else {
            model.addAttribute("listContact", contacts);
        }

        return "Admin/list_contact"  ;
    }



}
