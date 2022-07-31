package idv.blake.application.model.dao.permission;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import idv.blake.application.model.entity.permission.RoleDbEntity;

@Repository
public interface RoleDao extends CrudRepository<RoleDbEntity, String> {

}
