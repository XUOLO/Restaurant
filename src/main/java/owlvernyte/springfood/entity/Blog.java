package owlvernyte.springfood.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.sql.Blob;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Blog")
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message = "Title not null ")
    @Column(name = "Title")
    private String title;


    @NotBlank(message = "Description not null")
    @Column(name = "Description", columnDefinition = "LONGTEXT")
    private String description;
    @NotBlank(message = "Detail not null")
    @Column(name = "Detail", columnDefinition = "LONGTEXT")
    private String detail;

    @NotBlank(message = "CreatedBy not null")
    @Column(name = "CreatedBy")
    private String CreatedBy;

    @Column(name = "CreatedDate")
    private LocalDate createdDate;
    @OneToMany(mappedBy = "blog")
    private List<Comment> comments;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Lob
    private Blob image;
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatusString() {
        switch (status) {
            case "1":
                return "Ẩn";
            case "2":
                return "Đăng";

            default:
                return "Unknown";
        }
    }
    public String getStatus() {
        return status;
    }

    private String status;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }
}
