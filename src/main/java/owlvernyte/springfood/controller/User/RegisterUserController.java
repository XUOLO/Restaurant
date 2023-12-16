package owlvernyte.springfood.controller.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import owlvernyte.springfood.constants.Provider;
import owlvernyte.springfood.entity.*;
import owlvernyte.springfood.repository.ReservationRepository;
import owlvernyte.springfood.service.CategoryService;
import owlvernyte.springfood.service.UserService;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Controller
public class RegisterUserController {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/user/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "User/register";
    }
    @PostMapping("/user/verifyOTP")
    public String verifyOTP(  @RequestParam("username") String username, @RequestParam("otp") String otp, Model model,HttpSession session) {
        // Kiểm tra xác minh OTP cho username tại đây
        if (userService.verifyOTP(username, otp)) {
            userService.setOTPVerified(username, true);
            model.addAttribute("successMessage", "Xác thực OTP thành công.");

            SecurityContextHolder.clearContext();
            session.invalidate();

        } else {
            model.addAttribute("errorMessage", "OTP không hợp lệ. Thử lại!.");
        }

        return "User/otpVerified";
    }
    @PostMapping("/user/verifyOTPAgain")
    public String verifyOTPAgain( @RequestParam("username") String username, @RequestParam("otp") String otp, Model model,HttpSession session) {
        if (userService.verifyOTP(username, otp)) {
            userService.setOTPVerified(username, true);
            model.addAttribute("successMessage", "Xác thực OTP thành công.");
            SecurityContextHolder.clearContext();
            session.invalidate();

        } else {
            model.addAttribute("errorMessage", "OTP không hợp lệ. Thử lại!");
        }

        return "User/otpVerifiedAgain";
    }
    @GetMapping("/user/otpVerified")
    public String showOtpVerifiedForm(Model model) {
        String username = (String) model.getAttribute("username");
        if (username == null) {
            model.addAttribute("errorMessage", "Tài khoản không đúng");
            model.addAttribute("listCategory", categoryService.getAllCategory());
        } else {
            model.addAttribute("username", username);
        }
        return "User/otpVerified";
    }
    @GetMapping("/user/otpVerifiedAgain")
    public String otpVerifiedAgain(RedirectAttributes redirectAttributes,Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            model.addAttribute("listCategory", categoryService.getAllCategory());
            redirectAttributes.addFlashAttribute("errorMessage", "Tài khoản không đúng!");

        } else {
            model.addAttribute("username", username);
        }
        redirectAttributes.addFlashAttribute("errorMessage", "Tài khoả̀n của bạn chưa được kích hoạt. Chúng tôi đã gửi lại mã otp đến email của bạn !");

        return "User/otpVerifiedAgain";
    }
    @PostMapping("/user/register")
    public String registerUser(HttpServletRequest request, @ModelAttribute("user") @Valid User user, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "User/register";
        }
        // Kiểm tra xem username đã tồn tại trong CSDL hay chưa
        if (userService.isUsernameExists(user.getUsername())) {
             redirectAttributes.addFlashAttribute("usernameExists", "Username đã tồn tại.");
            String referer = request.getHeader("Referer");
            return "redirect:" + referer;
        }

        // Kiểm tra xem email đã tồn tại trong CSDL hay chưa
        if (userService.isEmailExists(user.getEmail())) {
             redirectAttributes.addFlashAttribute("emailExists", "Email đã tồn tại.");
            String referer = request.getHeader("Referer");
            return "redirect:" + referer;

        }
        redirectAttributes.addFlashAttribute("successMessage", "Đăng ký thành công. Hãy kiểm tra mail của bạn để lấy mã OTP");
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setProvider(Provider.LOCAL.value);
        String otp = OTPUtils.generateOTP();
        user.setOtp(otp);
        user.setOtpVerified(false);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Đặt các thuộc tính của email
            helper.setTo(user.getEmail());
            helper.setSubject("Mã xác OTP");
            String htmlContent = "<html><body>";
            htmlContent += "<h2>OTP của bạn là: " + otp + "</h2>";
            htmlContent += "</body></html>";

            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();

            model.addAttribute("errorMessage", "email fail.");
            return "User/ErrorPage";
        }

        userService.saveRegisterCustomer(user);
         redirectAttributes.addFlashAttribute("username", user.getUsername());
        return "redirect:/user/otpVerified";
    }
    @Autowired
    private ReservationRepository reservationRepository;
    @PostMapping("/user/reservation")
    public String createBooking(@ModelAttribute("reservation") Reservation reservation) {
        reservationRepository.save(reservation); // Lưu trữ thông tin đặt bàn vào cơ sở dữ liệu
        return "redirect:/user/datetime"; // Chuyển hướng sau khi đặt bàn thành công
    }

    @GetMapping("/user/datetime")
    public String dateee(){
        return "User/datetime";
    }



    @GetMapping("/user/forgotPassword")
    public String forgotPassword( Model model, HttpSession session) {

        String username = (String) session.getAttribute("username");

        model.addAttribute("listCategory", categoryService.getAllCategory());
        model.addAttribute("username", username);




        return "User/forgotPassword";
    }


    @PostMapping("/user/forgotPassword")
    public String submitForgotPassword(Model model, HttpSession session, @RequestParam("email") String email) {
        // Kiểm tra xem email có trong CSDL không (đây giả sử bạn sử dụng một service để kiểm tra)
        boolean emailExists = userService.checkEmailExists(email);

        if (emailExists) {
            String newPassword = generateRandomPassword();

            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

            String encryptedPassword = bCryptPasswordEncoder.encode(newPassword);

            userService.updatePasswordByEmail(email, encryptedPassword);

            sendPasswordResetEmail(email, newPassword);

            model.addAttribute("message", "Mật khẩu của bạn đã được reset, hãy kiểm tra mail của bạn để lấy mật khẩu mới!");
        } else {
            model.addAttribute("errorMessage", "Email not exist!!!");
        }


        return "User/forgotPassword";
    }

    private String generateRandomPassword() {

        String characters = "0123456789";
        StringBuilder newPassword = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 12; i++) {
            int index = random.nextInt(characters.length());
            newPassword.append(characters.charAt(index));
        }

        return newPassword.toString();
    }

    private void sendPasswordResetEmail(String email, String newPassword) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setTo(email);
            helper.setSubject("Reset mật khẩu");
            helper.setText("Mật khẩu mới của bạn là : " + newPassword);

            // Gửi email
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
