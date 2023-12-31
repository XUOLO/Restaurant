package owlvernyte.springfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Reservation;


@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
