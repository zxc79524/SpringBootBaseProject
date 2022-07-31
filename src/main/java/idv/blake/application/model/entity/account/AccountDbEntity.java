package idv.blake.application.model.entity.account;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import idv.blake.application.model.entity.permission.AccountRoleDbEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account")
public class AccountDbEntity {

	@Id
	@Column(name = "uid", nullable = false, columnDefinition = "char(36)")
	private String uid;

	@Column(name = "account", nullable = false, columnDefinition = "varchar(20)")
	private String account;

	@Column(name = "passowrd", nullable = false, columnDefinition = "char(64)")
	private String password;

	@Column(name = "create_datetime", nullable = false, columnDefinition = "TIMESTAMP")
	private Timestamp createDate;

	@OneToOne(mappedBy = "account", fetch = FetchType.LAZY)
	private AccountRoleDbEntity accountRole;

	public AccountDbEntity(String uid, String account, String password, Timestamp createDate) {
		this.uid = uid;
		this.account = account;
		this.password = password;
		this.createDate = createDate;
	}

}
