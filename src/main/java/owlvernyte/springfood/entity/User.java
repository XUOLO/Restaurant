package owlvernyte.springfood.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Entity
@Table (name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 250, nullable = false)
    private String password;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "phone", length = 50)
    private String phone;
    @Lob
    private Blob image;
    private String address;
    private LocalDateTime createTime;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Booking> bookings;
    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name = "provider", length = 50)
    private String provider;

    @Column(name = "otp", length = 6)
    private String otp;
    @Column(name = "is_otp_verified")
    private Boolean isOtpVerified;

    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings;
    }

    public Boolean getOtpVerified() {
        return isOtpVerified;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Boolean isOtpVerified() {
        return isOtpVerified;
    }

    public void setOtpVerified(Boolean otpVerified) {
        isOtpVerified = otpVerified;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public boolean hasRole(String roleName) {
        Iterator<Role> iterator = this.roles.iterator();
        while (iterator.hasNext()) {
            Role role = iterator.next();
            if (role.getName().equals(roleName)) {
                return true;
            }
        }

        return false;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Blob getImage() {
        return image;
    }

    public String getAddress() {
        return address;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public String getProvider() {
        return provider;
    }
}
