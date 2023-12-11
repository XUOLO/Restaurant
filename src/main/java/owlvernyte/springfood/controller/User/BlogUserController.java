package owlvernyte.springfood.controller.User;

 import jakarta.servlet.http.HttpSession;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.data.domain.Page;
 import org.springframework.data.domain.Sort;
 import org.springframework.security.core.Authentication;
 import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
 import org.springframework.web.bind.annotation.PathVariable;
 import org.springframework.web.bind.annotation.RequestParam;
 import owlvernyte.springfood.entity.*;
 import owlvernyte.springfood.repository.BlogRepository;
 import owlvernyte.springfood.repository.CommentRepository;
 import owlvernyte.springfood.service.BlogService;
 import org.springframework.ui.Model;

 import java.security.Principal;
 import java.util.ArrayList;
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
        List<Blog> blogList = blogService.getAllBlog();
        List<Blog> showBlogList = new ArrayList<>();

        for (Blog blog : blogList) {
            if ("2".equals(blog.getStatus())) {
                showBlogList.add(blog);
            }
        }

         model.addAttribute("listBlog",showBlogList);

        return findPaginatedBlogRequest(1,model,"title","asc");
    }


    @GetMapping("/user/pageBlog/{pageNo}")
    public String findPaginatedBlogRequest(@PathVariable(value = "pageNo")int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir){
        int pageSize=1;
        Page<Blog> page= blogService.findPaginatedProduct(pageNo,pageSize,sortField,sortDir);
        List<Blog> blogList = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());

        model.addAttribute("pageSize", pageSize);

        model.addAttribute("sortField",sortField);
        model.addAttribute("sortDir",sortDir);
        model.addAttribute("reverseSortDir",sortDir.equals("asc")?"desc":"asc");
        model.addAttribute("listBlog",blogList);

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
