package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

}
