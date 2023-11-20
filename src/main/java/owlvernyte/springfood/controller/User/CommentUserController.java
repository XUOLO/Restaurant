package owlvernyte.springfood.controller.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import owlvernyte.springfood.entity.Comment;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.CommentRepository;
import owlvernyte.springfood.service.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class CommentUserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CommentRepository commentRepository;
    @PostMapping("/user/submitComment")
    public String placeSubmitComment(@Valid @ModelAttribute("comment") Comment comment, @RequestParam("productId") Product productId,  Model model, HttpSession session, Principal principal, HttpServletRequest request) {
        String name = (String) session.getAttribute("name");

        model.addAttribute("name", name);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.viewById(userId);
        LocalDate date = LocalDate.now();
        comment.setUser(user);
        comment.setProduct(productId);
        comment.setCommentDate(date);
        commentRepository.save(comment);

        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }
}
