package idv.blake.application.model.entity.account;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
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

}
