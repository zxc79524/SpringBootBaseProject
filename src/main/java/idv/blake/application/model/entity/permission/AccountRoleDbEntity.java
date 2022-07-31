package idv.blake.application.model.entity.permission;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import idv.blake.application.model.entity.account.AccountDbEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account_role")
public class AccountRoleDbEntity implements Serializable {

	@Id
	private String uid;

	@OneToOne(targetEntity = AccountDbEntity.class, cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn(name = "uid", referencedColumnName = "uid")
	private AccountDbEntity account;

	@OneToOne(targetEntity = RoleDbEntity.class, cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_account_role_by_role", foreignKeyDefinition = "FOREIGN KEY (role_id) REFERENCES role(role_id) ON UPDATE CASCADE ON DELETE CASCADE"), name = "role_id", referencedColumnName = "role_id", nullable = false)
	private RoleDbEntity role;

	public AccountRoleDbEntity(String uid, RoleDbEntity role) {
		this.uid = uid;
		this.role = role;
	}

}
