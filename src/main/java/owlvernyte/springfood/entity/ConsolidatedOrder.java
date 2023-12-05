package owlvernyte.springfood.entity;

import java.util.List;

public class ConsolidatedOrder {
    private String orderCode;
    private List<BookingDetail> bookingDetails;

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public List<BookingDetail> getBookingDetails() {
        return bookingDetails;
    }

    public void setBookingDetails(List<BookingDetail> bookingDetails) {
        this.bookingDetails = bookingDetails;
    }
}