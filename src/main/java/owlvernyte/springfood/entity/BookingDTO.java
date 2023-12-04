package owlvernyte.springfood.entity;

import java.time.LocalDate;
import java.util.Date;

public class  BookingDTO {
    private Long id;
    private String title;
    private LocalDate start;


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

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }
}
