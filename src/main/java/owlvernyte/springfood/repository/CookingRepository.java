package owlvernyte.springfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Category;
import owlvernyte.springfood.entity.Cooking;

@Repository
public interface CookingRepository extends JpaRepository<Cooking, Long> {


}
