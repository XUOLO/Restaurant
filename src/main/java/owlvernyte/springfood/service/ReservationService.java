package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Product;
import owlvernyte.springfood.entity.Reservation;
import owlvernyte.springfood.repository.ReservationRepository;

import java.util.List;

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
}
