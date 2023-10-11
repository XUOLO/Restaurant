package owlvernyte.springfood.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import owlvernyte.springfood.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleById(Long id);

    @Query("SELECT r.id FROM Role r WHERE r.name = ?1")
    Long getRoleIdByName (String roleName);
}
