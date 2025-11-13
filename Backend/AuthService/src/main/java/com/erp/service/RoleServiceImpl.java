package com.erp.service;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.dto.RoleDTO;
import com.erp.entity.Role;
import com.erp.repository.RoleRepository;


@Service
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    
    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
		super();
		this.roleRepository = roleRepository;

	}

	@Override
    public Role createRole(RoleDTO dto) {
        Role role = new Role();
        role.setRoleName(dto.getRoleName());

        return roleRepository.save(role);
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
