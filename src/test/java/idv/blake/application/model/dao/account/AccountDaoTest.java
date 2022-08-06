package idv.blake.application.model.dao.account;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import idv.blake.application.BaseUnitTest;
import idv.blake.application.model.entity.account.AccountDbEntity;

class AccountDaoTest extends BaseUnitTest {

	@Autowired
	AccountDao accountDao;

	@Autowired
	StringRedisTemplate stringRedisTemplate;

	@Test
	@Transactional
	void test() {

		AccountDbEntity account = accountDao.findByUid("00000000-0000-0000-0000-000000000000");
		account.getAccountRole();
		account.getAccountRole().getRole();
		account.getAccountRole().getRole().getRolePermission().size();
//
//		for (RolePermissionDbEntity rolePermissionDbEntity : account.getAccountRole().getRole().getRolePermission()) {
//			System.out.println(String.format("permission id %s httpmethod %s aipurl %s ",
//					rolePermissionDbEntity.getPermission().getPermissionId(),
//					rolePermissionDbEntity.getPermission().getHttpMethod(),
//					rolePermissionDbEntity.getPermission().getApiUrl()));
//		}

//		Set<String> keySet = stringRedisTemplate.keys("test*");
//		System.out.println(keySet);
//		stringRedisTemplate.delete(keySet);

//		String value = stringRedisTemplate.opsForValue().getAndDelete("test1");
//		System.out.println(value);
	}

}
