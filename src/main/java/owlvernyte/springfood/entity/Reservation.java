package owlvernyte.springfood.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.sql.Blob;
import java.time.LocalDateTime;
@Entity
@Table(name = "Reservation")
public class Reservation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message = "Name not null")
    @Column(name = "Name")
    private String name;

    @NotBlank(message = "Description not null ")
    @Column(name = "Description")
    private String description;
    @Lob
    private Blob image;
    @Positive(message = "NumberOfPeople > 0")
    @Column(name = "NumberOfPeople")
    private int numberOfPeople;
    @NotBlank(message = "Detail not null")
    @Column(name = "Detail")
    private String detail;
    private LocalDateTime createTime;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reservationCategory_id")
    private ReservationCategory reservationCategory;

    private String status;


    public String getStatusString() {
        switch (status) {
            case "1":
                return "Empty";
            case "2":
                return "Using";

            default:
                return "Unknown";
        }
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public int getNumberOfPeople() {
        return numberOfPeople;
    }

    public void setNumberOfPeople(int numberOfPeople) {
        this.numberOfPeople = numberOfPeople;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public ReservationCategory getReservationCategory() {
        return reservationCategory;
    }

    public void setReservationCategory(ReservationCategory reservationCategory) {
        this.reservationCategory = reservationCategory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
