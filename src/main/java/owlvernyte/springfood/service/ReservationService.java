package owlvernyte.springfood.service;

import org.apache.regexp.RE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Booking;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.Reservation;
import owlvernyte.springfood.repository.ReservationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {
    @Autowired
    public ReservationRepository reservationRepository;
    public List<Reservation> getAllReservation() {
        return reservationRepository.findAll();
    }

    public void saveReservation(Reservation reservation) {
        this.reservationRepository.save(reservation);

    }
    public void deleteReservationById(long id) {
        this.reservationRepository.deleteById(id);
    }

    public Reservation viewById(long id) {
        return reservationRepository.findById(id).get();
    }



    public Reservation getReservationById(long id) {
        Optional<Reservation> optional = reservationRepository.findById(id);
        Reservation reservation = null;
        if (optional.isPresent()) {
            reservation = optional.get();
        }
        else
        {
            throw new RuntimeException(" Cant find product id : " + id);
        }
        return reservation;
    }
    public Page<Reservation> findPaginatedReservation(int pageNo, int pageSize, String sortField, String sortDirection){
        Sort sort= sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name())? Sort.by(sortField).ascending():
                Sort.by(sortField).descending();


        Pageable pageable= PageRequest.of(pageNo - 1,pageSize,sort);
        return this.reservationRepository.findAll(pageable);
    }
}
