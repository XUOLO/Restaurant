package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Role;
import owlvernyte.springfood.repository.RoleRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;


    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }

    public Set<Role> getRolesByIds(List<Long> roleIds) {
        List<Role> roles = roleRepository.findAllById(roleIds);
        return new HashSet<>(roles);
    }

}
