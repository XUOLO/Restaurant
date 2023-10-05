package owlvernyte.springfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {


}
