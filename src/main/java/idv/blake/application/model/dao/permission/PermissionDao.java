package idv.blake.application.model.dao.permission;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import idv.blake.application.model.entity.permission.PermissionDbEntity;

@Repository
public interface PermissionDao extends CrudRepository<PermissionDbEntity, String> {

}
