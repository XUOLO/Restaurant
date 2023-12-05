package owlvernyte.springfood.controller.Admin;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.Role;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.UserRepository;
import owlvernyte.springfood.service.RoleService;
import owlvernyte.springfood.service.UserService;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Controller
public class StaffController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/admin/new_staff")
    public String showNewStaff(Model model){

        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("listRole",  roleService.getAllRole());
        return "Admin/new_staff";
    }


    @GetMapping("/admin/list_staff")
    public String listStaff(Model model ,Authentication authentication){
        String username = authentication.getName();
        model.addAttribute("listStaff", userService.getEmployeesByRole());
        model.addAttribute("listRole", roleService.getAllRole());
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        return findPaginatedStaff(authentication,1,model,"name","asc");
    }

    @GetMapping("/admin/pageStaff/{pageNo}")
    public String findPaginatedStaff(Authentication authentication,@PathVariable(value = "pageNo")int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir){
        int pageSize=10;
        Page<User> page= userService.findPaginatedStaff(pageNo,pageSize,sortField,sortDir);
        List<User> listStaff = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("pageSize", pageSize);
        String username = authentication.getName();
//        model.addAttribute("listStaff", userService.getEmployeesByRole());
        model.addAttribute("listRole", roleService.getAllRole());
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        model.addAttribute("sortField",sortField);
        model.addAttribute("sortDir",sortDir);
        model.addAttribute("reverseSortDir",sortDir.equals("asc")?"desc":"asc");

        model.addAttribute("listStaff",listStaff);
        return "Admin/list_staff";

    }

    @GetMapping("/admin/list_customer")
    public String listCustomer(Model model ,Authentication authentication){
        String username = authentication.getName();
        model.addAttribute("listStaff", userService.getCustomerByRoleUser());
        model.addAttribute("listRole", roleService.getAllRole());
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);

        return findPaginatedCustomer(authentication,1,model,"name","asc");
    }


    @GetMapping("/admin/pageCustomer/{pageNo}")
    public String findPaginatedCustomer(Authentication authentication,@PathVariable(value = "pageNo")int pageNo, Model model, @RequestParam("sortField") String sortField, @RequestParam("sortDir") String sortDir){
        int pageSize=10;
        Page<User> page= userService.findPaginatedCustomer(pageNo,pageSize,sortField,sortDir);
        List<User> listStaff = page.getContent();
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("pageSize", pageSize);
        String username = authentication.getName();
//        model.addAttribute("listStaff", userService.getCustomerByRoleUser());
//        model.addAttribute("listRole", roleService.getAllRole());
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        model.addAttribute("sortField",sortField);
        model.addAttribute("sortDir",sortDir);
        model.addAttribute("reverseSortDir",sortDir.equals("asc")?"desc":"asc");

        model.addAttribute("listStaff",listStaff);
        return "Admin/list_customer";

    }



    @PostMapping("/admin/addStaff")
    public String newStaff(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, @RequestParam("image") MultipartFile file, @RequestParam(value = "roles", required = false) List<Long> roleIds, Model model) throws IOException, SerialException, SQLException {
        // Xử lý tạo người dùng và gán vai trò
        if (roleIds != null && !roleIds.isEmpty()) {
            Set<Role> roles = roleService.getRolesByIds(roleIds);
            user.setRoles(roles);
        }

        if (file.isEmpty()) {
            user.setImage(null);
        } else {
            byte[] bytes = file.getBytes();
            Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
            user.setImage(blob);
        }
        user.setCreateTime(LocalDateTime.now());
        user.setIsOtpVerified(true);
        userService.saveUser(user);
        return "redirect:/admin/list_staff";


    }

        @GetMapping("/admin/staff_profile")
        public String showInfoStaff(Authentication authentication, Model model) {
            String username = authentication.getName();

            // Lấy thông tin người dùng từ cơ sở dữ liệu (thông qua username)
            User user = userRepository.findByUsername(username);
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
            boolean isEmployee = authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("EMPLOYEE"));

            model.addAttribute("username", username);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isEmployee", isEmployee);
            model.addAttribute("user", user);
            model.addAttribute("roles", user.getRoles());
            model.addAttribute("listRole", roleService.getAllRole());


            return "Admin/staff_profile";
        }


    @GetMapping("/displayStaff")
    public ResponseEntity<byte[]> displayImageStaff(@RequestParam("id") long id) throws IOException, SQLException
    {
        User user = userService.viewById(id);
        byte [] imageBytes = null;
        imageBytes = user.getImage().getBytes(1,(int) user.getImage().length());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
    }


    @PostMapping("/admin/updateProfile")
    public String updateProduct(@ModelAttribute("user") @Valid  User user, BindingResult bindingResult,
                                Model model,
                                @RequestParam("image") MultipartFile file) throws IOException, SerialException, SQLException {

        if (file.isEmpty()) {
            user.setImage(null); // Gán giá trị null cho trường 'image' nếu không có tệp tin tải lên
        } else {
            byte[] bytes = file.getBytes();
            Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
            user.setImage(blob); // Gán giá trị 'blob' cho trường 'image' nếu có tệp tin tải lên
        }
        user.setIsOtpVerified(true);
        userService.saveUser(user);
        return "redirect:/admin/staff_profile";

    }



    @PostMapping("/admin/changePassword")
    public String changePassword(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, HttpSession session,
                                 Model model,
                                 RedirectAttributes redirectAttributes, HttpServletRequest request,
                                 @RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword
    ) throws IOException, SerialException, SQLException {
        User existingUser = userService.viewById(user.getId());
        user.setOtp(existingUser.getOtp());
        user.setEmail(existingUser.getEmail());
        user.setAddress(existingUser.getAddress());
        user.setPhone(existingUser.getPhone());
        user.setName(existingUser.getName());
        user.setImage(existingUser.getImage());
        user.setUsername(existingUser.getUsername());
        user.setIsOtpVerified(existingUser.getIsOtpVerified());
        user.setProvider(existingUser.getProvider());
        user.setCreateTime(existingUser.getCreateTime());
        user.setRoles(existingUser.getRoles());
        session.setAttribute("user",user);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String savePassword =existingUser.getPassword();
        if(!passwordEncoder.matches(currentPassword,savePassword)){
            redirectAttributes.addFlashAttribute("errorPassword", "Mật khẩu hiện tại không khớp");
            String referer = request.getHeader("Referer");
            return "redirect:" + referer;
        }
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("NewAndConfirmError", "Mật khẩu mới và xác nhận mật khẩu mới không khớp");
            String referer = request.getHeader("Referer");
            return "redirect:" + referer;
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userService.editUser(user);
        redirectAttributes.addFlashAttribute("changePasswordSuccess", "Thay đổi mật khẩu thành công");
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;

    }


    @PostMapping("/admin/user/search")
    public String searchTicket(@RequestParam("keyword") String keyword, Model model ,Authentication authentication ) {
        String username = authentication.getName();
        model.addAttribute("listStaff", userService.getAllUser());
        model.addAttribute("listRole", roleService.getAllRole());
        User user = userRepository.findByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("username", username);
        List<User> users = userService.searchUser(keyword);
        if (users.isEmpty()) {
            String errorMessage = "No matching products found";
            model.addAttribute("errorMessage", errorMessage);
        } else {
            model.addAttribute("listStaff", users);
        }

        return "Admin/list_staff"  ;
    }


}
