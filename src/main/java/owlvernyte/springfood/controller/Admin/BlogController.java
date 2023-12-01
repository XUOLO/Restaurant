package owlvernyte.springfood.controller.Admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import owlvernyte.springfood.entity.Blog;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.BlogRepository;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.BlogService;
import owlvernyte.springfood.service.RoleService;
import owlvernyte.springfood.service.UserService;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Controller
public class BlogController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private BlogRepository blogRepository;



    @GetMapping("/admin/showFormForUpdateBlog/{id}")
    public String showFormForUpdateBlog(Authentication authentication, @PathVariable(value = "id") long id, Model model) {
        Blog blog = blogService.getBlogById(id);
        model.addAttribute("blog", blog);
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        return "Admin/update_blog";
    }
    @GetMapping("/admin/deleteBlog/{id}")
    public String deleteBlog(@PathVariable(value = "id") long id) {
        this.blogService.deleteBlogById(id);
        return "redirect:/admin/list_blog";
    }
    @PostMapping("/admin/updateBlog")
    public String updateBlog( @ModelAttribute("blog") @Valid  Blog blog, BindingResult bindingResult,
                              Model model,
                              @RequestParam("image") MultipartFile file) throws IOException, SerialException, SQLException  {

        Blog existingUser = blogService.viewById(blog.getId());
        if (file.isEmpty()) {
            blog.setImage(existingUser.getImage());
        } else {
            byte[] bytes = file.getBytes();
            Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
            blog.setImage(blob);
        }
        blog.setCreatedDate(existingUser.getCreatedDate());
        blog.setCreatedBy(existingUser.getCreatedBy());
        blog.setStatus(existingUser.getStatus());

          blogService.saveBlog(blog);

        return "redirect:/admin/list_blog"  ;

    }


    @PostMapping("/admin/addBlog")
    public String addBlog(@RequestParam("image") MultipartFile file, @RequestParam("createBy") String username, Authentication authentication, @Valid @ModelAttribute("blog") Blog blog, BindingResult bindingResult, Model model) throws IOException, SerialException, SQLException {

        blog.setCreatedBy(username);
        LocalDate localDate = LocalDate.now();
        blog.setCreatedDate(localDate);
        if (file.isEmpty()) {
            blog.setImage(null);
        } else {
            byte[] bytes = file.getBytes();
            Blob blob = new SerialBlob(bytes);
            blog.setImage(blob);
        }

        blog.setStatus("1");
        blogService.saveBlog(blog);
        return "redirect:/admin/list_blog";

    }
    @GetMapping("/displayBlog")
    public ResponseEntity<byte[]> displayImageBlog(@RequestParam("id") long id) throws IOException, SQLException
    {
        Blog blog = blogService.viewById(id);
        byte [] imageBytes = null;
        imageBytes = blog.getImage().getBytes(1,(int) blog.getImage().length());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }
    @GetMapping("/admin/new_blog")
    public String showNewCategory(Authentication authentication,Model model) {
        Blog blog = new Blog();
        model.addAttribute("blog", blog);
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        model.addAttribute("username", username);

        model.addAttribute("user", user);
        return "Admin/new_blog";
    }





    @GetMapping("/admin/list_blog")
    public String showListBlog(Authentication authentication, Model model) {


        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
        User user = userRepository.findByUsername(username);

        model.addAttribute("user", user);
        model.addAttribute("username", username);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("listBlog", blogService.getAllBlog());

        return findPaginatedBlogRequest(1,model,"title","asc");
    }

    @GetMapping("/admin/pageBlog/{pageNo}")
    public String findPaginatedBlogRequest(@PathVariable(value = "pageNo")int pageNo,Model model,@RequestParam("sortField") String sortField,@RequestParam("sortDir") String sortDir){
        int pageSize=3;
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

        return "Admin/list_blog";

    }

    @PostMapping("/admin/{id}/updateStatusBlog")
    public String updateProductStatus(@PathVariable("id") Long id, @RequestParam("status") String status, Model model, HttpSession session, HttpServletRequest request) {
        Blog blog = blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid blog id: " + id));
        blog.setStatus(status);
        blogRepository.save(blog);
        String referer = request.getHeader("Referer");


        return "redirect:/admin/list_blog";
    }
    @PostMapping("/admin/blog/search")
    public String searchBlog(@RequestParam("keyword") String keyword, Model model ,Authentication authentication ) {
        String username = authentication.getName();
        model.addAttribute("listStaff", userService.getAllUser());
        model.addAttribute("listRole", roleService.getAllRole());
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        List<Blog> blogs = blogService.searchBlogAdmin(keyword);
        if (blogs.isEmpty()) {
            String errorMessage = "No matching products found";
            model.addAttribute("errorMessage", errorMessage);
            return findPaginatedBlogRequest(1,model,"title","asc");
        } else {
            model.addAttribute("listBlog", blogs);
        }

        return "Admin/list_searchBlog"  ;
    }
}
