package idv.blake.application.model.service.permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import idv.blake.application.model.dao.permission.RolePermissionDao;

@Service("PermissoinService")
@Scope("prototype")
public class PermissionService {

	@Autowired
	private RolePermissionDao rolePermissionDao;
	
	
}
