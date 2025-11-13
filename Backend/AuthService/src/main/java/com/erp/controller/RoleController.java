package com.erp.controller;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.erp.dto.RoleDTO;
import com.erp.entity.Role;
import com.erp.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin("*")
@Tag(name = "Role Management", description = "APIs for managing roles")
public class RoleController {

	@Autowired
    private final RoleService roleService;
    

	public RoleController(RoleService roleService) {
		super();
		this.roleService = roleService;
	}

	@PostMapping
    public ResponseEntity<Role> createRole(@RequestBody RoleDTO roleDTO) {
        return ResponseEntity.ok(roleService.createRole(roleDTO));
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }
}
