package owlvernyte.springfood.util;


import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import owlvernyte.springfood.entity.OTPUtils;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.service.UserService;

import java.io.IOException;
import java.util.Collection;
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private UserService userService;
    @Autowired
    private JavaMailSender mailSender;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Lấy role của người dùng
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) authentication.getAuthorities();
        String role = authorities.iterator().next().getAuthority();


        String username = authentication.getName();

        User user = userService.findByUsername(username);
        if (user.isOtpVerified()) {
            String redirectUrl;
            if (role.equals("ADMIN")) {
                redirectUrl = "/admin";
            } else if (role.equals("EMPLOYEE")) {
                redirectUrl = "/admin";
            } else {
                redirectUrl = "/";
            }
            request.getSession().setAttribute("username", username);
            request.getSession().setAttribute("user", user );
            request.getSession().setAttribute("name", user.getName());
            request.getSession().setAttribute("userImage", user.getImage());
            request.getSession().setAttribute("userId", user.getId());
            request.getSession().setAttribute("email", user.getEmail());
            request.getSession().setAttribute("phone", user.getPhone());
            request.getSession().setAttribute("address", user.getAddress());

            response.sendRedirect(redirectUrl);
        } else {

            String redirectUrl;
            String otp = OTPUtils.generateOTP();
            user.setOtp(otp);
            userService.saveOtp(user);
            MimeMessage message = mailSender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(user.getEmail());
                helper.setSubject("OTP Verification");
                String htmlContent = "<html><body>";
                htmlContent += "<h2>Your Otp is: " + otp + "</h2>";
                htmlContent += "</body></html>";

                helper.setText(htmlContent, true);

                mailSender.send(message);

            } catch (Exception e) {
                e.printStackTrace();
            }
            request.getSession().setAttribute("username", username);
            redirectUrl = "/user/otpVerifiedAgain";
            response.sendRedirect(redirectUrl);
        }

    }
}