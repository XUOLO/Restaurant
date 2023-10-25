package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.*;
import owlvernyte.springfood.repository.BookingRepository;

import java.util.*;

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

    public List<Booking> getAllBooking() {
        return bookingRepository.findAll();
    }
    public Page<Booking> findPaginatedBooking(int pageNo, int pageSize, String sortField, String sortDirection){
        Sort sort= sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())? Sort.by(sortField).ascending():
                Sort.by(sortField).descending();


        Pageable pageable= PageRequest.of(pageNo - 1,pageSize,sort);
        return this.bookingRepository.findAll(pageable);
    }


    public Booking getBookingById(long id) {
        Optional<Booking> optional = bookingRepository.findById(id);
        Booking booking = null;
        if (optional.isPresent()) {
            booking = optional.get();
        }
        else
        {
            throw new RuntimeException(" Cant find booking id : " + id);
        }
        return booking;
    }
    public List<Booking> searchBookingAdmin(String keyword) {

        if(keyword!=null){
            return bookingRepository.findAll(keyword);
        }
        return bookingRepository.findAll();
    }

    public Long CountBooking(){
        return bookingRepository.count();
    }
    public Long countHandlingBooking() {
        return bookingRepository.countHandlingBooking();
    }

    public Long countConfirmedBooking() {
        return bookingRepository.countConfirmedBooking();
    }

    public Long countCancelBooking() {
        return bookingRepository.countCancelBooking();
    }

}
