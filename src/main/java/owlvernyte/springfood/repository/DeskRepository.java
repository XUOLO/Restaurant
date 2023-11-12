package owlvernyte.springfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Desk;
import owlvernyte.springfood.entity.OrderDetail;

import java.util.List;

@Repository
public interface DeskRepository  extends JpaRepository<Desk, Long> {


    List<Desk> findByReservationId(Long reservationId);
}
