package owlvernyte.springfood.util;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.service.UserService;

import java.io.IOException;
import java.util.Collection;
@Component
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private UserService userService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // Lấy role của người dùng
        Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) authentication.getAuthorities();
        String role = authorities.iterator().next().getAuthority();

        // Lấy tên người dùng
        String username = authentication.getName();

        // Tạo đường dẫn chuyển hướng với tên người dùng
        String redirectUrl;
        if (role.equals("ROLE_ADMIN")) {
            redirectUrl = "/admin";
        } else if (role.equals("ROLE_EMPLOYEE")) {
            redirectUrl = "/admin";
        } else {
            redirectUrl = "/";
        }
        User user = userService.findByUsername(username);


        request.getSession().setAttribute("username", username);
        request.getSession().setAttribute("user", user );
        request.getSession().setAttribute("name", user.getName());
        request.getSession().setAttribute("name", user.getName());
        request.getSession().setAttribute("userId", user.getId());
        request.getSession().setAttribute("email", user.getEmail());
        request.getSession().setAttribute("phone", user.getPhone());
        request.getSession().setAttribute("address", user.getAddress());

        response.sendRedirect(redirectUrl);
    }
}