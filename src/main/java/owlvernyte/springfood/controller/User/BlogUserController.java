package owlvernyte.springfood.controller.User;

 import jakarta.servlet.http.HttpSession;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.data.domain.Sort;
 import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.PathVariable;
 import owlvernyte.springfood.entity.Blog;
 import owlvernyte.springfood.entity.Comment;
 import owlvernyte.springfood.entity.Product;
 import owlvernyte.springfood.entity.Rating;
 import owlvernyte.springfood.repository.BlogRepository;
 import owlvernyte.springfood.repository.CommentRepository;
 import owlvernyte.springfood.service.BlogService;
 import org.springframework.ui.Model;

 import java.security.Principal;
 import java.util.List;

@Controller
public class BlogUserController {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private BlogService blogService;


    @GetMapping("/user/blog")
    private String ShowBlog (Model model){

        model.addAttribute("listBlog",blogRepository.findAll());

        return "User/blog";
    }

    @GetMapping("/user/blogDetail/{id}")
    private String ShowBlogDetail (@PathVariable Long id, Model model, HttpSession session, Principal principal){

         Blog blog = blogService.getBlogById(id);

        if (blog == null) {
            // Xử lý trường hợp sản phẩm không tồn tại
            return "error";
        }



        Long userId = (Long) session.getAttribute("userId");
        model.addAttribute("userId",userId);

        model.addAttribute("blog", blog);

        model.addAttribute("blogId",blog.getId());
        String username = (String) session.getAttribute("username");
        String name = (String) session.getAttribute("name");
        model.addAttribute("username", username);
        model.addAttribute("name", name);
        Comment comment= new Comment();
        model.addAttribute("comment",comment);
        int commentCount = commentRepository.countCommentsByBlogId(id);
        model.addAttribute("commentCount",commentCount);
        Sort sort = Sort.by(Sort.Direction.DESC, "commentDate");
        List<Comment> commentList = commentRepository.findByBlog(blog, sort);
        model.addAttribute("commentProduct", commentList);


        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        return "User/blogDetail";
    }
}
