package owlvernyte.springfood.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.constants.Provider;
import owlvernyte.springfood.constants.Role;
import owlvernyte.springfood.entity.Order;

import owlvernyte.springfood.entity.User;
import owlvernyte.springfood.repository.RoleRepository;
import owlvernyte.springfood.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
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
    public void editUser(User user) {

        userRepository.save(user);
    }
    public void saveUser(User user) {

        BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
        this.userRepository.save(user);

    }

    public User findUserByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
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


    public void saveRegisterCustomer(User user) {
        userRepository.save(user);
        Long userId = userRepository.getUserIdByUsername(user.getUsername());
        Long roleId = roleRepository.getRoleIdByName("USER");
        if(roleId != 0 && userId != 0){
            userRepository.addRoleToUser(userId,roleId);
        }

    }
    public void saveOtp(User user) {
        userRepository.save(user);


    }

    public boolean verifyOTP(String username, String otp) {

        User user = userRepository.findByUsername(username);
        if (user == null) {

            return false;
        }


        return user.getOtp().equals(otp);
    }
    public void setOTPVerified(String username, boolean isVerified) {
        // Lấy thông tin người dùng từ CSDL dựa trên username
        User user = userRepository.findByUsername(username);
        if (user == null) {

            return;
        }


        user.setOtpVerified(isVerified);
        userRepository.save(user);
    }

    public boolean isUsernameExists(String username) {
        User existingUser = userRepository.findByUsername(username);
        return existingUser != null;
    }

    public boolean isEmailExists(String email) {
        User existingUser = userRepository.findByEmail(email);
        return existingUser != null;
    }
    public List<User> searchUser(String keyword) {

        if(keyword!=null){
            return userRepository.findAll(keyword);
        }
        return userRepository.findAll();
    }


    public int countCustomers() {
        List<User> users = userRepository.findAll();
        int count = 0;
        for (User user : users) {
            for (owlvernyte.springfood.entity.Role role : user.getRoles()) {
                if (role.getName().equals("USER")) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }
    public List<User> getCustomerByRoleUser() {
        List<User> users = userRepository.findAll();
        List<User> usersWithRoleUser = new ArrayList<>();

        for (User user : users) {
            for (owlvernyte.springfood.entity.Role role : user.getRoles()) {
                if (role.getName().equals("USER")) {
                    usersWithRoleUser.add(user);
                    break;
                }
            }
        }

        return usersWithRoleUser;
    }
    public List<User> getEmployeesByRole() {
        List<User> users = userRepository.findAll();
        List<User> usersWithRoleUser = new ArrayList<>();

        for (User user : users) {
            for (owlvernyte.springfood.entity.Role role : user.getRoles()) {
                if (role.getName().equals("ADMIN")||role.getName().equals("EMPLOYEE")||role.getName().equals("CHEF")) {
                    usersWithRoleUser.add(user);
                    break;
                }
            }
        }

        return usersWithRoleUser;
    }

    public int countAdmin() {
        List<User> users = userRepository.findAll();
        int count = 0;
        for (User user : users) {
            for (owlvernyte.springfood.entity.Role role : user.getRoles()) {
                if (role.getName().equals("ADMIN")) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }
    public int countEmployee() {
        List<User> users = userRepository.findAll();
        int count = 0;
        for (User user : users) {
            for (owlvernyte.springfood.entity.Role role : user.getRoles()) {
                if (role.getName().equals("EMPLOYEE")) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }
    public int countEmployeeAndAdmin() {
        List<User> users = userRepository.findAll();
        int count = 0;
        for (User user : users) {
            for (owlvernyte.springfood.entity.Role role : user.getRoles()) {
                if (role.getName().equals("EMPLOYEE") || role.getName().equals("ADMIN")) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }
    public boolean checkEmailExists(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    public void updatePasswordByEmail(String email, String newPassword) {
        User user = userRepository.findByEmail(email);
        user.setPassword(newPassword);
        userRepository.save(user);
    }


    public Page<User> findPaginatedStaff(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return this.userRepository.findPaginatedStaff(pageable);
    }

    public Page<User> findPaginatedCustomer(int pageNo, int pageSize, String sortField, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        return userRepository.findPaginatedCustomer(pageable);
    }
}
