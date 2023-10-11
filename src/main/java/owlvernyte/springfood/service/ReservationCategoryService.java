package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Reservation;
import owlvernyte.springfood.entity.ReservationCategory;
import owlvernyte.springfood.repository.ReservationCategoryRepository;

import java.util.List;

@Service
public class ReservationCategoryService {
    @Autowired
    private ReservationCategoryRepository reservationCategoryRepository;

    public List<ReservationCategory> getAllReservationCategory() {
        return reservationCategoryRepository.findAll();
    }

    public void saveReservationCategory(ReservationCategory reservationCategory) {
        this.reservationCategoryRepository.save(reservationCategory);

    }

}
