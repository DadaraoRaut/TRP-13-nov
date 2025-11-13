package com.erp.dto;

import lombok.Data;
import java.util.Set;

@Data
public class RoleDTO {
    private String roleName;
    private Set<Long> permissionIds;
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public Set<Long> getPermissionIds() {
		return permissionIds;
	}
	public void setPermissionIds(Set<Long> permissionIds) {
		this.permissionIds = permissionIds;
	}
    
    
}

