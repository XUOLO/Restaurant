package owlvernyte.springfood.controller.User;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import owlvernyte.springfood.entity.*;
import owlvernyte.springfood.repository.BookingRepository;
import owlvernyte.springfood.repository.OrderRepository;
import owlvernyte.springfood.repository.ProductCategoryRepository;
import owlvernyte.springfood.repository.ProductRepository;
import owlvernyte.springfood.service.*;

import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UserInfoController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderDetailService orderDetailService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private  BookingService bookingService;
    @Autowired
    private BookingDetailService bookingDetailService;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @GetMapping("/user/info")
    public String showInfo(Model model, HttpSession session, Principal principal){

        String name = (String) session.getAttribute("name");
        User user = (User) session.getAttribute("user");
        String username = (String) session.getAttribute("username");
        String address = (String) session.getAttribute("address");
        String email = (String) session.getAttribute("email");
        String phone = (String) session.getAttribute("phone");
        Blob userImage = (Blob) session.getAttribute("userImage");
        long userId = (long) session.getAttribute("userId");


        model.addAttribute("userId", userId);
        User existingUser = userService.viewById(user.getId());


        model.addAttribute("name", existingUser.getName());
        model.addAttribute("username", existingUser.getUsername());
        model.addAttribute("address", existingUser.getAddress());
        model.addAttribute("email", existingUser.getEmail());
        model.addAttribute("phone", existingUser.getPhone());
        model.addAttribute("user", user);
        model.addAttribute("userImage", userImage);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());
         return "User/userInfo";
    }



    @GetMapping("/user/userOrderInfo")
    public String userOrderInfo(Model model, HttpSession session, Principal principal){


        String name = (String) session.getAttribute("name");
        User user = (User) session.getAttribute("user");
        String username = (String) session.getAttribute("username");
        String address = (String) session.getAttribute("address");
        String email = (String) session.getAttribute("email");
        String phone = (String) session.getAttribute("phone");
        Blob userImage = (Blob) session.getAttribute("userImage");
        long userId = (long) session.getAttribute("userId");


        model.addAttribute("userId", userId);
        model.addAttribute("name", name);
        model.addAttribute("userId", userId);
        model.addAttribute("username", username);
        model.addAttribute("address", address);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("user", user);
        model.addAttribute("userImage", userImage);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());


        List<Order> userOrders = orderService.getOrdersByUserId(userId);
        model.addAttribute("userOrders", userOrders);


        return findPaginatedUserOrder(1,model,"name","asc",session,principal);
    }
    @GetMapping("/user/pageUserOrder/{pageNo}")
    public String findPaginatedUserOrder(@PathVariable(value = "pageNo")int pageNo,Model model,@RequestParam("sortField") String sortField
            ,@RequestParam("sortDir") String sortDir,HttpSession session,Principal principal){
        int pageSize=10;

        long userId = (long) session.getAttribute("userId");

        Page<Order> page= orderService.findPaginatedUserOrder(userId,pageNo,pageSize,sortField,sortDir);

        List<Order> userOrders = page.getContent();

        model.addAttribute("userOrders",userOrders);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());

        model.addAttribute("pageSize", pageSize);

        model.addAttribute("sortField",sortField);
        model.addAttribute("sortDir",sortDir);
        model.addAttribute("reverseSortDir",sortDir.equals("asc")?"desc":"asc");


        String name = (String) session.getAttribute("name");
        User user = (User) session.getAttribute("user");
        String username = (String) session.getAttribute("username");
        String address = (String) session.getAttribute("address");
        String email = (String) session.getAttribute("email");
        String phone = (String) session.getAttribute("phone");
        Blob userImage = (Blob) session.getAttribute("userImage");






        model.addAttribute("name", name);
        model.addAttribute("userId", userId);
        model.addAttribute("username", username);
        model.addAttribute("address", address);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("user", user);
        model.addAttribute("userImage", userImage);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());



        return "User/userOrderInfo";

    }


    @GetMapping("/user/orderDetail/{id}")
    public String showUserOrderDetail( HttpSession session, Principal principal,Authentication authentication, @PathVariable(value = "id") long id, Model model) {

        String name = (String) session.getAttribute("name");
        User user = (User) session.getAttribute("user");
        String username = (String) session.getAttribute("username");
        String address = (String) session.getAttribute("address");
        String email = (String) session.getAttribute("email");
        String phone = (String) session.getAttribute("phone");

        long userId = ((User) session.getAttribute("user")).getId();
        Blob userImage = (Blob) session.getAttribute("userImage");



        model.addAttribute("userId", userId);
        model.addAttribute("name", name);
        model.addAttribute("userId", userId);
        model.addAttribute("username", username);
        model.addAttribute("address", address);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("user", user);
        model.addAttribute("userImage", userImage);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());



        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);

        // Lấy danh sách OrderDetail theo Order
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrder(order);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("totalAmount",order.getTotal());



        return "User/orderDetailUser";
    }

    @PostMapping("/user/{id}/updateOrderStatus")
    public String updateOrderStatus(@PathVariable("id") Long id, @RequestParam("status") String status, Model model, HttpSession session, HttpServletRequest request) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid order id: " + id));
        order.setStatus(status);
        orderRepository.save(order);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }


    @PostMapping("/user/order/search")
    public String searchOrder(HttpSession session,Principal principal, @RequestParam String keyword, Model model) {

        String name = (String) session.getAttribute("name");
        User user = (User) session.getAttribute("user");
        String username = (String) session.getAttribute("username");
        String address = (String) session.getAttribute("address");
        String email = (String) session.getAttribute("email");
        String phone = (String) session.getAttribute("phone");
        Blob userImage = (Blob) session.getAttribute("userImage");
        long userId = (long) session.getAttribute("userId");





        model.addAttribute("name", name);
        model.addAttribute("userId", userId);
        model.addAttribute("username", username);
        model.addAttribute("address", address);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("user", user);
        model.addAttribute("userImage", userImage);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());

        List<Order> userOrders = orderService.searchOrders(userId, keyword);


        if (userOrders.isEmpty()) {

            String errorMessage = "Không tìm thấy thông tin đơn hàng";
            model.addAttribute("errorMessage", errorMessage);
            return findPaginatedUserOrder(1,model,"name","asc",session,principal);
        } else {
             model.addAttribute("userOrders", userOrders);
        }


        return "User/userSearchOrder";
    }


    @GetMapping("/user/editProfile")
    public String showEditInfo(Model model, HttpSession session, Principal principal){

        String name = (String) session.getAttribute("name");
        User user = (User) session.getAttribute("user");
        String username = (String) session.getAttribute("username");
        String address = (String) session.getAttribute("address");
        String email = (String) session.getAttribute("email");
        String phone = (String) session.getAttribute("phone");
        Blob userImage = (Blob) session.getAttribute("userImage");
        long userId = (long) session.getAttribute("userId");


        model.addAttribute("userId", userId);
        User existingUser = userService.viewById(user.getId());


        model.addAttribute("name", existingUser.getName());
        model.addAttribute("username", existingUser.getUsername());
        model.addAttribute("address", existingUser.getAddress());
        model.addAttribute("email", existingUser.getEmail());
        model.addAttribute("phone", existingUser.getPhone());
        model.addAttribute("user", user);
        model.addAttribute("userImage", userImage);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());
        return "User/edit_profile";
    }

    @PostMapping("/user/editProfile")
    public String editProfile(@ModelAttribute("user") @Valid User user,
                              BindingResult bindingResult, HttpSession session,
                              Model model,
                              @RequestParam("image") MultipartFile file ,
                              RedirectAttributes redirectAttributes, HttpServletRequest request) throws IOException, SerialException, SQLException {
        User existingUser = userService.viewById(user.getId());
        if (file.isEmpty()) {
            user.setImage(existingUser.getImage());
        } else {
            byte[] bytes = file.getBytes();
            Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
            user.setImage(blob);
        }

        user.setPassword(existingUser.getPassword());
        user.setOtp(existingUser.getOtp());
        user.setIsOtpVerified(existingUser.getIsOtpVerified());
        user.setProvider(existingUser.getProvider());
        user.setCreateTime(existingUser.getCreateTime());
        user.setRoles(existingUser.getRoles());
        session.setAttribute("user",user);
         


        userService.editUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "Thay đổi thành công");
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;

    }


    @PostMapping("/user/changePassword")
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
    @GetMapping("/user/userBookingInfo")
    public String userBookingInfo(Model model, HttpSession session, Principal principal){


        String name = (String) session.getAttribute("name");
        User user = (User) session.getAttribute("user");
        String username = (String) session.getAttribute("username");
        String address = (String) session.getAttribute("address");
        String email = (String) session.getAttribute("email");
        String phone = (String) session.getAttribute("phone");
        Blob userImage = (Blob) session.getAttribute("userImage");
        long userId = (long) session.getAttribute("userId");


        model.addAttribute("userId", userId);
        model.addAttribute("name", name);
        model.addAttribute("userId", userId);
        model.addAttribute("username", username);
        model.addAttribute("address", address);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("user", user);
        model.addAttribute("userImage", userImage);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());


        List<Booking> userBooking = bookingService.getBookingByUserId(userId);
        model.addAttribute("userBooking", userBooking);


        return findPaginatedUserBooking(1,model,"name","asc",session,principal);
    }
    @GetMapping("/user/pageUserBooking/{pageNo}")
    public String findPaginatedUserBooking(@PathVariable(value = "pageNo")int pageNo,Model model,@RequestParam("sortField") String sortField
            ,@RequestParam("sortDir") String sortDir,HttpSession session,Principal principal){
        int pageSize=10;

        long userId = (long) session.getAttribute("userId");

        Page<Booking> page= bookingService.findPaginatedUserBooing(userId,pageNo,pageSize,sortField,sortDir);

        List<Booking> userBooking = page.getContent();

        model.addAttribute("userBooking",userBooking);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());

        model.addAttribute("pageSize", pageSize);

        model.addAttribute("sortField",sortField);
        model.addAttribute("sortDir",sortDir);
        model.addAttribute("reverseSortDir",sortDir.equals("asc")?"desc":"asc");


        String name = (String) session.getAttribute("name");
        User user = (User) session.getAttribute("user");
        String username = (String) session.getAttribute("username");
        String address = (String) session.getAttribute("address");
        String email = (String) session.getAttribute("email");
        String phone = (String) session.getAttribute("phone");
        Blob userImage = (Blob) session.getAttribute("userImage");






        model.addAttribute("name", name);
        model.addAttribute("userId", userId);
        model.addAttribute("username", username);
        model.addAttribute("address", address);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("user", user);
        model.addAttribute("userImage", userImage);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());



        return "User/userBookingInfo";

    }
    @PostMapping("/user/booking/search")
    public String searchBooking(HttpSession session,Principal principal, @RequestParam String keyword, Model model) {

        String name = (String) session.getAttribute("name");
        User user = (User) session.getAttribute("user");
        String username = (String) session.getAttribute("username");
        String address = (String) session.getAttribute("address");
        String email = (String) session.getAttribute("email");
        String phone = (String) session.getAttribute("phone");
        Blob userImage = (Blob) session.getAttribute("userImage");
        long userId = (long) session.getAttribute("userId");





        model.addAttribute("name", name);
        model.addAttribute("userId", userId);
        model.addAttribute("username", username);
        model.addAttribute("address", address);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("user", user);
        model.addAttribute("userImage", userImage);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());

        List<Booking> userBooking = bookingService.searchBooking(userId, keyword);


        if (userBooking.isEmpty()) {

            String errorMessage = "Không tìm thấy đơn đặt bàn!";
            model.addAttribute("errorMessage", errorMessage);
            return findPaginatedUserBooking(1,model,"name","asc",session,principal);
        } else {
            model.addAttribute("userBooking", userBooking);
        }


        return "User/userSearchBooking";
    }
    @GetMapping("/user/bookingDetail/{id}")
    public String showUserBookingDetail( HttpSession session, Principal principal,Authentication authentication, @PathVariable(value = "id") long id, Model model) {

        String name = (String) session.getAttribute("name");
        User user = (User) session.getAttribute("user");
        String username = (String) session.getAttribute("username");
        String address = (String) session.getAttribute("address");
        String email = (String) session.getAttribute("email");
        String phone = (String) session.getAttribute("phone");

        long userId = ((User) session.getAttribute("user")).getId();
        Blob userImage = (Blob) session.getAttribute("userImage");



        model.addAttribute("userId", userId);
        model.addAttribute("name", name);
        model.addAttribute("userId", userId);
        model.addAttribute("username", username);
        model.addAttribute("address", address);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        model.addAttribute("user", user);
        model.addAttribute("userImage", userImage);
        boolean isAuthenticated = principal != null;
        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("listProductCategory", productCategoryService.getAllProductCategory());



        Booking booking = bookingService.getBookingById(id);
        model.addAttribute("booking", booking);

        // Lấy danh sách OrderDetail theo Order
        List<BookingDetail> bookingDetails = bookingDetailService.getBookingDetailsByBooking(booking);
        model.addAttribute("bookingDetails", bookingDetails);



        return "User/bookingDetailUser";
    }
    @PostMapping("/user/{id}/updateBookingStatus")
    public String userUpdateBookingStatus(@PathVariable("id") Long id, @RequestParam("status") String status, Model model, HttpSession session, HttpServletRequest request) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid booking id: " + id));
        LocalDate currentDateTime = booking.getDateArrive();
        booking.setStatus(status);
        booking.setDateArrive(currentDateTime);
        bookingRepository.save(booking);
        String referer = request.getHeader("Referer");
        return "redirect:" + referer;
    }

}
