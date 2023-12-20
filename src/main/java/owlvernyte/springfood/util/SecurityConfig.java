package owlvernyte.springfood.util;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.IdTokenClaimNames;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.SecurityFilterChain;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.service.CustomOAuth2UserService;
import owlvernyte.springfood.service.CustomUserDetailService;
import owlvernyte.springfood.service.OAuthService;
import owlvernyte.springfood.service.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService oAuthService;

    private final UserService userService;
    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
    @Autowired
    private CustomLogoutSuccessHandler customLogoutSuccessHandler;
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        var auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService());
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(@NotNull
                                                   HttpSecurity http) throws Exception {
        http.csrf().disable()

                .authorizeRequests()

                                .requestMatchers("/").permitAll()
                                .requestMatchers("/Admin/css/**", "/Admin/js/**", "/Admin/img/**", "/Admin/vendor/**", "/Admin/scss/**","/Admin/node_modules/**").permitAll()

                                .requestMatchers("/User/css/**", "/User/js/**","/User/img/**","/User/vendor/**", "/User/images/**", "/User/fonts/**").permitAll()
                                .requestMatchers("/admin/deleteProduct/*").hasAuthority("ADMIN")
                                .requestMatchers("/user/**").permitAll()

                                .requestMatchers("/admin/**").hasAnyAuthority("EMPLOYEE", "ADMIN","CHEF")
                                .requestMatchers("/displayStaff/**").permitAll()
                                .requestMatchers("/display").permitAll()
                                .requestMatchers("/displayBlog").permitAll()
                                .requestMatchers("/displayReservation").permitAll()
                                .anyRequest()
                                .authenticated()
                .and()
                .oauth2Login(oauth2Login -> oauth2Login.loginPage("/login")
                        .failureUrl("/login?error")
                        .userInfoEndpoint(userInfoEndpoint ->
                                userInfoEndpoint
                                        .userService(oAuthService)
                        )
                        .successHandler(
                                (request, response,
                                 authentication) -> {
                                    var oidcUser = (DefaultOidcUser) authentication.getPrincipal();
                                    userService.saveOauthUser(oidcUser.getEmail(), oidcUser.getName(),oidcUser.getFullName(),oidcUser.getPhoneNumber());
                                    HttpSession session = request.getSession();
                                    session.setAttribute("name", oidcUser.getFullName());
                                    User user=userService.findByUsername(oidcUser.getName());
                                    request.getSession().setAttribute("username", user.getUsername());
                                     request.getSession().setAttribute("user", user );
                                    request.getSession().setAttribute("name", user.getName());
                                    request.getSession().setAttribute("userImage", user.getImage());
                                    request.getSession().setAttribute("userId", user.getId());
                                    request.getSession().setAttribute("email", user.getEmail());
                                    request.getSession().setAttribute("phone", user.getPhone());
                                    request.getSession().setAttribute("address", user.getAddress());
                                    response.sendRedirect("/");
                                }
                        )
                        .permitAll())

                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .failureUrl("/login?error")
                        .successHandler(loginSuccessHandler)
                        .permitAll()
                )
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
//                .rememberMe(rememberMe ->
//                        rememberMe.key("dat").rememberMeCookieName("dat")
//                                .tokenValiditySeconds(24 * 60 * 60)
//                                .userDetailsService(userDetailsService())
//                )
//                .exceptionHandling(exceptionHandling ->
//                        exceptionHandling.accessDeniedPage("/403"))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/login"))
                )
//                .httpBasic(httpBasic -> httpBasic.realmName("dat"))
//                .csrf(AbstractHttpConfigurer::disable)
//                .cors(AbstractHttpConfigurer::disable)
        ;

        return http.build();
    }

}
