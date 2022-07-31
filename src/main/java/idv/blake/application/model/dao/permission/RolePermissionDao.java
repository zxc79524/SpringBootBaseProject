package idv.blake.application.model.dao.permission;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import idv.blake.application.model.entity.permission.RoleDbEntity;
import idv.blake.application.model.entity.permission.RolePermissionDbEntity;

@Repository
public interface RolePermissionDao extends CrudRepository<RolePermissionDbEntity, String> {

	List<RolePermissionDbEntity> findByRole(RoleDbEntity role);

}
