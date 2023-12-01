package owlvernyte.springfood.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Blog;
import owlvernyte.springfood.entity.Category;
import owlvernyte.springfood.entity.Comment;
import owlvernyte.springfood.entity.Product;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByProduct(Product product, Sort sort);
    List<Comment> findByBlog(Blog blog, Sort sort);
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.product.id = :productId")
    int countCommentsByProductId(@Param("productId") Long productId);
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.blog.id = :blogId")
    int countCommentsByBlogId(@Param("blogId") Long blogId);

}
