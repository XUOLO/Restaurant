package owlvernyte.springfood.controller.User;

import jakarta.mail.internet.MimeMessage;
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
import owlvernyte.springfood.entity.CartItem;
import owlvernyte.springfood.entity.OTPUtils;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.service.UserService;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Locale;

@Controller
public class RegisterUserController {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserService userService;
    @GetMapping("/user/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "User/register";
    }
    @PostMapping("/user/verifyOTP")
    public String verifyOTP(@RequestParam("username") String username, @RequestParam("otp") String otp, Model model,HttpSession session) {
        // Kiểm tra xác minh OTP cho username tại đây
        if (userService.verifyOTP(username, otp)) {
             userService.setOTPVerified(username, true);
            model.addAttribute("successMessage", "OTP verification successful.");
            SecurityContextHolder.clearContext();
            session.invalidate();
        } else {
            model.addAttribute("errorMessage", "Invalid OTP. Please try again.");
        }

        return "User/otpVerified";
    }
    @GetMapping("/user/otpVerified")
    public String showOtpVerifiedForm(Model model) {
        String username = (String) model.getAttribute("username");
        if (username == null) {
            model.addAttribute("errorMessage", "not found username");

        } else {
            model.addAttribute("username", username);
        }
        return "User/otpVerified";
    }
    @GetMapping("/user/otpVerifiedAgain")
    public String otpVerifiedAgain(Model model, HttpSession session) {
        String username = (String) session.getAttribute("username");

        if (username == null) {
            model.addAttribute("errorMessage", "not found username");

        } else {
            model.addAttribute("username", username);
        }
        return "User/otpVerifiedAgain";
    }
    @PostMapping("/user/register")
    public String registerUser(@ModelAttribute("user") @Valid User user, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "User/register";
        }

        // Kiểm tra xem username đã tồn tại trong CSDL hay chưa
        if (userService.isUsernameExists(user.getUsername())) {
            result.rejectValue("username", "error.user", "Username already exists.");
            return "User/register";
        }

        // Kiểm tra xem email đã tồn tại trong CSDL hay chưa
        if (userService.isEmailExists(user.getEmail())) {
            result.rejectValue("email", "error.user", "Email already exists.");
            return "User/register";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Register successful");
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
            helper.setSubject("OTP Verification");
            String htmlContent = "<html><body>";
            htmlContent += "<h2>Your Otp is: " + otp + "</h2>";
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

}
