package owlvernyte.springfood.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.constants.Provider;
import owlvernyte.springfood.constants.Role;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.RoleRepository;
import owlvernyte.springfood.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;


    public void save(User user) {
        user.setProvider(Provider.LOCAL.value);
        user.getRoles().add(roleRepository.findRoleById(Role.USER.value));
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);
    }
    public void saveUser(User user) {

        BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        this.userRepository.save(user);

    }

    public void saveOauthUser(String email, @NotNull String username) {
        if (userRepository.findByUsername(username) != null) return;
        var user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(new BCryptPasswordEncoder().encode(username));
        user.setProvider(Provider.GOOGLE.value);
        user.getRoles().add(roleRepository.findRoleById(Role.USER.value));
        userRepository.save(user);
    }
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    public void setDefaultRole(String username){
        userRepository.findByUsername(username).getRoles()
                .add(roleRepository
                        .findRoleById(Role.USER.value));
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User viewById(long id) {
        return userRepository.findById(id).get();
    }




    public List<User> searchUser(String keyword) {

        if(keyword!=null){
            return userRepository.findAll(keyword);
        }
        return userRepository.findAll();
    }


}
