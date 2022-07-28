package idv.blake.application.model.entity.auth;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import idv.blake.application.model.entity.account.AccountDbEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "refresh_token")
public class RefreshTokenDbEntity {

	@Id
	@Column(name = "token_id", nullable = false, columnDefinition = "char(32)")
	private String tokenId;

	@Column(name = "expired_datetime", nullable = false, columnDefinition = "TIMESTAMP")
	private Timestamp expireDate;

	@Column(name = "create_datetime", nullable = false, columnDefinition = "TIMESTAMP")
	private Timestamp createDate;

	@ManyToOne(targetEntity = AccountDbEntity.class, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_refresh_token", foreignKeyDefinition = "FOREIGN KEY (uid) REFERENCES account(uid) ON UPDATE CASCADE ON DELETE CASCADE"), name = "uid", referencedColumnName = "uid", nullable = false)
	private AccountDbEntity account;

}
