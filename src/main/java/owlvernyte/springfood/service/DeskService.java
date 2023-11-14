package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Desk;
import owlvernyte.springfood.entity.Reservation;
import owlvernyte.springfood.repository.DeskRepository;

import java.util.List;

@Service
public class DeskService {
    @Autowired
    private DeskRepository deskRepository;

    public List<Desk> findByReservationId(Long reservationId) {
        return deskRepository.findByReservationId(reservationId);
    }

public Desk viewById(long id) {
    return deskRepository.findById(id).get();
}
}
