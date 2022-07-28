package idv.blake.application.model.dao.auth;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import idv.blake.application.model.entity.account.AccountDbEntity;
import idv.blake.application.model.entity.auth.TokenDbEntity;

@Repository
public interface TokenDao extends CrudRepository<TokenDbEntity, String> {

	void deleteByAccount(AccountDbEntity account);

	TokenDbEntity findByTokenId(String tokenId);

}
