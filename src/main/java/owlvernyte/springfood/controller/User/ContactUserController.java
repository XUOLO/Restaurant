package owlvernyte.springfood.controller.User;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import owlvernyte.springfood.entity.Contact;
import owlvernyte.springfood.service.ContactService;

import java.security.Principal;

@Controller
public class ContactUserController {

@Autowired
private ContactService contactService;




    @PostMapping("/user/submitContact")
    public String submitContactForm(RedirectAttributes redirectAttributes, Model model, HttpServletRequest request, @Valid @ModelAttribute("contact") Contact contact, BindingResult bindingResult )   {
        if (bindingResult.hasErrors()) {

            redirectAttributes.addFlashAttribute("failMessage", "Invalid contact");
            String referer = request.getHeader("Referer");
            return "redirect:" + referer;
        }
        contactService.saveContact(contact);
        redirectAttributes.addFlashAttribute("successMessage", "Send successful");
         String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }


    @GetMapping("/user/contact")
    public String viewContactpage(Model model, Principal principal, HttpSession session) {



        Contact contact= new Contact();
        model.addAttribute("contact", contact);
        String username = (String) session.getAttribute("username");
        String name = (String) session.getAttribute("name");
        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("username", username);
        model.addAttribute("name", name);
        model.addAttribute("userId", userId);

        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);




        return "User/contact";
    }
}
