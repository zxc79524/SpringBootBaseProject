package idv.blake.application.model.dao.account;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import idv.blake.application.model.entity.account.AccountDbEntity;

@Repository
public interface AccountDao extends CrudRepository<AccountDbEntity, String> {

	AccountDbEntity findByAccount(String account);

	AccountDbEntity findByUid(String uid);

	void deleteByAccount(String account);

}
