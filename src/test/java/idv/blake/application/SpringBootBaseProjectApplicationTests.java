package idv.blake.application;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import idv.blake.application.model.dao.account.AccountDao;
import idv.blake.application.model.dao.permission.AccountRoleDao;
import idv.blake.application.model.dao.permission.PermissionDao;
import idv.blake.application.model.dao.permission.RoleDao;
import idv.blake.application.model.dao.permission.RolePermissionDao;
import idv.blake.application.model.entity.account.AccountDbEntity;
import idv.blake.application.model.entity.permission.AccountRoleDbEntity;
import idv.blake.application.model.entity.permission.PermissionDbEntity;
import idv.blake.application.model.entity.permission.RoleDbEntity;
import idv.blake.application.model.entity.permission.RolePermissionDbEntity;
import idv.blake.application.util.EncodeUtil;

class SpringBootBaseProjectApplicationTests extends BaseUnitTest {

	@Autowired
	RoleDao roleDao;

	@Autowired
	AccountDao accountDao;

	@Autowired
	AccountRoleDao accountRoleDao;

	@Autowired
	PermissionDao permissionDao;

	@Autowired
	RolePermissionDao rolePermissionDao;

	@Test
	public void insertData() {

		AccountDbEntity accountDbEntity = accountDao.findByAccount("root");
		if (accountDbEntity != null) {
			return;
		}

		accountDao.save(new AccountDbEntity("00000000-0000-0000-0000-000000000000", "root",
				EncodeUtil.encryptSHA256("Aa123456"), new Timestamp(System.currentTimeMillis())));

		accountDao.save(new AccountDbEntity("00000000-0000-0000-0000-000000000001", "user",
				EncodeUtil.encryptSHA256("Aa123456"), new Timestamp(System.currentTimeMillis())));

		roleDao.save(new RoleDbEntity("ROLE_ADMIN", "管理者"));
		roleDao.save(new RoleDbEntity("ROLE_USER", "一般使用者"));

		accountRoleDao.save(
				new AccountRoleDbEntity("00000000-0000-0000-0000-000000000000", new RoleDbEntity("ROLE_ADMIN", null)));
		accountRoleDao.save(
				new AccountRoleDbEntity("00000000-0000-0000-0000-000000000001", new RoleDbEntity("ROLE_USER", null)));

		permissionDao.save(new PermissionDbEntity("00010001", "/check", "GET"));
		permissionDao.save(new PermissionDbEntity("00010002", "/check/token", "GET"));
		permissionDao.save(new PermissionDbEntity("00010003", "/check/token/[\\w]+", "GET"));

		rolePermissionDao.save(new RolePermissionDbEntity(new RoleDbEntity("ROLE_ADMIN", null),
				new PermissionDbEntity("00010001", null, null), true));
		rolePermissionDao.save(new RolePermissionDbEntity(new RoleDbEntity("ROLE_ADMIN", null),
				new PermissionDbEntity("00010002", null, null), true));
		rolePermissionDao.save(new RolePermissionDbEntity(new RoleDbEntity("ROLE_ADMIN", null),
				new PermissionDbEntity("00010003", null, null), true));

		rolePermissionDao.save(new RolePermissionDbEntity(new RoleDbEntity("ROLE_USER", null),
				new PermissionDbEntity("00010001", null, null), true));
	}

//	@Transactional
//	@Test
	public void showData() {
		System.out.println("===========================");
		AccountDbEntity accountDbEntity = accountDao.findByUid("00000000-0000-0000-0000-000000000000");
		System.out.println(accountDbEntity.getAccount());
		System.out.println(accountDbEntity.getAccountRole().getUid());
		System.out.println(accountDbEntity.getAccountRole().getRole().getRoleId());

		for (RolePermissionDbEntity rolePermission : accountDbEntity.getAccountRole().getRole().getRolePermission()) {
			System.out.println(rolePermission.getPermission().getPermissionId());
			System.out.println(rolePermission.getIsAllow());
		}

		System.out.println("===========================");
		accountDbEntity = accountDao.findByUid("00000000-0000-0000-0000-000000000001");
		System.out.println(accountDbEntity.getAccount());
		System.out.println(accountDbEntity.getAccountRole().getUid());
		System.out.println(accountDbEntity.getAccountRole().getRole().getRoleId());

		for (RolePermissionDbEntity rolePermission : accountDbEntity.getAccountRole().getRole().getRolePermission()) {
			System.out.println(rolePermission.getPermission().getPermissionId());
			System.out.println(rolePermission.getIsAllow());
		}
	}
}
