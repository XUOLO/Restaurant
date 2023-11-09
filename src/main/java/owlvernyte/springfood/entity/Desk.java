package owlvernyte.springfood.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

//@Entity
//@Table(name = "Desk")
public class Desk {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    @NotBlank(message = "Name not null ")
//    @Column(name = "Name")
//    private String name;
//
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "reservation_id")
//    private Reservation  reservation ;
//    private String status;
//
//
//    public String getStatusString() {
//        switch (status) {
//            case "1":
//                return "Empty";
//            case "2":
//                return "Using";
//
//            default:
//                return "Unknown";
//        }
//    }
//    private LocalDateTime createTime;
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public Reservation getReservation() {
//        return reservation;
//    }
//
//    public void setReservation(Reservation reservation) {
//        this.reservation = reservation;
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public LocalDateTime getCreateTime() {
//        return createTime;
//    }
//
//    public void setCreateTime(LocalDateTime createTime) {
//        this.createTime = createTime;
//    }
}
