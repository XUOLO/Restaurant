package owlvernyte.springfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import owlvernyte.springfood.entity.Role;
import owlvernyte.springfood.repository.IRoleRepository;

@Service
public class RoleRepository {
    @Autowired
    private IRoleRepository roleRepository;


}
