package in.coempt.service;

import in.coempt.entity.Roles;

import java.util.List;

public interface RolesService {

    public List<Roles> getAllRoles();


    Roles getRoleDetails(int roleId);
}
