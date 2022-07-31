package idv.blake.application.model.dao.permission;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import idv.blake.application.model.entity.account.AccountDbEntity;
import idv.blake.application.model.entity.permission.AccountRoleDbEntity;

@Repository
public interface AccountRoleDao extends CrudRepository<AccountRoleDbEntity, AccountDbEntity> {

}
