package owlvernyte.springfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import owlvernyte.springfood.entity.ReservationCategory;

@Repository
public interface ReservationCategoryRepository extends JpaRepository<ReservationCategory, Long> {

}
