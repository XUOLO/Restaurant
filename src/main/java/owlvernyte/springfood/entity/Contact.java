package owlvernyte.springfood.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "Contact")
public class Contact {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message = "Name not null ")
    @Column(name = "CustomerName")
    private String name;

    @NotBlank(message = "Message not null")
    @Column(name = "Message")
    private String message;

    @NotBlank(message = "Phone not null")
    @Column(name = "Phone")
    private String phone;

    @NotBlank(message = "Email not null")
    @Column(name = "Email")
    private String email;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
