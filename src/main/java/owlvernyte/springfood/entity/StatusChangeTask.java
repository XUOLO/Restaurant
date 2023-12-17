package owlvernyte.springfood.entity;

import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import owlvernyte.springfood.service.BookingService;

import java.util.TimerTask;

public class StatusChangeTask extends TimerTask {
    private Booking booking;
    @Autowired
    private BookingService bookingService;



    public StatusChangeTask(Booking booking, BookingService bookingService) {
        this.booking = booking;
        this.bookingService = bookingService;
    }

    @Override
    public void run() {
        if (booking.getStatus().equals("1")) {
            booking.setStatus("1");
        } else if (booking.getStatus().equals("2") || booking.getStatus().equals("3")) {
            booking.setStatus("3");
        } else if (booking.getStatus().equals("4")) {
            booking.setStatus("4");
        }

        bookingService.saveBooking(booking);
    }
}