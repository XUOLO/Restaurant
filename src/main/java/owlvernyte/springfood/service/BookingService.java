package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Booking;
import owlvernyte.springfood.entity.CartItem;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.ReservationItem;
import owlvernyte.springfood.repository.BookingRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingService {
        @Autowired
        private ProductService productService;
    Map<Long, ReservationItem> reservationCart = new HashMap<>();

    public void add(ReservationItem newItem) {
        ReservationItem reservationItem = reservationCart.get(newItem.getProductId());
        if (reservationItem == null) {
            newItem.setQuantity(0); // Đặt giá trị mặc định là 0
            reservationCart.put(newItem.getProductId(), newItem);
        } else {
            reservationItem.setQuantity(reservationItem.getQuantity());
        }
    }


    public void addReservationItem(ReservationItem reservationItem) {
        reservationCart.put(reservationItem.getProductId(), reservationItem);
    }
    public void remove(long id){

        reservationCart.remove(id);
    }


    public ReservationItem update(long productId, int quantity){
        ReservationItem reservationItem = reservationCart.get(productId);
        if (reservationItem != null) {
            reservationItem.setQuantity(quantity);
        }
        return reservationItem;
    }

    public void clear(){

        reservationCart.clear();
    }

    public Collection<ReservationItem> getAllReservationItem(){

        return reservationCart.values();
    }

    public int getCount(){

        return reservationCart.values().size();
    }

    public  double getAmount(){

        return reservationCart.values().stream()
                .mapToDouble(item->item.getQuantity()*item.getPrice()).sum();
    }

@Autowired
private BookingRepository bookingRepository;
    public void saveBooking(Booking booking) {
        this.bookingRepository.save(booking);

    }
}
