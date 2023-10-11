package owlvernyte.springfood.repository;


import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Order;
import owlvernyte.springfood.entity.User;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = ?1")
    User findByUsername(String username);

    @Query("SELECT o FROM User o WHERE CONCAT(o.name, o.username, o.phone, o.email, o.address) LIKE %?1%")
    List<User> findAll(String keyword);

    @Query("SELECT u.id FROM User u WHERE u.username = ?1")
    Long getUserIdByUsername(String username);

    @Query(value = "SELECT r.name FROM role r INNER JOIN user_role ur  " +
            "ON r.id = ur.role_id WHERE ur.user_id = ?1", nativeQuery = true)
    String[] getRolesOfUser(Long userId);

    @Query("SELECT u FROM User u WHERE u.username = :username OR u.email = :email")
    public User findByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
    @Modifying
    @Transactional
    @Query(value ="INSERT INTO user_role(user_id, role_id) " +
            "VALUES(?1,?2)", nativeQuery = true)
    void addRoleToUser(Long userId, Long roleId);

    @Query("SELECT u FROM User u WHERE u.email = :email")
    User findByEmail(@Param("email") String email);


}
