package in.coempt.service.impl;

import in.coempt.entity.Roles;
import in.coempt.repository.RolesRepository;
import in.coempt.service.RolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolesServiceImpl implements RolesService {
    @Autowired
    public RolesRepository repository;

    @Override
    public List<Roles> getAllRoles() {
        return repository.findAll();
    }

    @Override
    public Roles getRoleDetails(int roleId) {
        return repository.findById(Long.valueOf(roleId)).get();
    }
}
