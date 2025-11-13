package com.erp.service;
import java.util.List;

import com.erp.dto.RoleDTO;
import com.erp.entity.Role;

public interface RoleService {
    Role createRole(RoleDTO dto);
    List<Role> getAllRoles();
}
