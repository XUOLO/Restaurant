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

    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name = "provider", length = 50)
    private String provider;

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
