package owlvernyte.springfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import owlvernyte.springfood.entity.Blog;

import java.util.List;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    @Query("SELECT t FROM Blog t  WHERE CONCAT(t.title, t.CreatedBy, t.createdDate) LIKE %?1%")
    List<Blog> findAll(String keyword);
}
