package idv.blake.application.model.dao.auth;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import idv.blake.application.model.entity.account.AccountDbEntity;
import idv.blake.application.model.entity.auth.RefreshTokenDbEntity;

@Repository
public interface RefreshTokenDao extends CrudRepository<RefreshTokenDbEntity, String> {

	RefreshTokenDbEntity findByTokenId(String tokenId);

	void deleteByTokenId(String tokenId);

	void deleteByAccount(AccountDbEntity account);

}
