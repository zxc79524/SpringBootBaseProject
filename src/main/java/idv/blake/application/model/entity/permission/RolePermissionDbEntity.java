package idv.blake.application.model.entity.permission;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@IdClass(RolePermissoinPkDbEntity.class)
@Table(name = "role_permission")
@AllArgsConstructor
@NoArgsConstructor
@org.hibernate.annotations.Table(comment = "角色和權限對應", appliesTo = "role_permission")
public class RolePermissionDbEntity {

	@Id
	@OneToOne(targetEntity = RoleDbEntity.class, cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_role_permission_by_role", foreignKeyDefinition = "FOREIGN KEY (role_id) REFERENCES role(role_id) ON UPDATE CASCADE ON DELETE CASCADE"), name = "role_id", referencedColumnName = "role_id", nullable = false)
	private RoleDbEntity role;

	@Id
	@OneToOne(targetEntity = PermissionDbEntity.class, cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
	@JoinColumn(foreignKey = @ForeignKey(name = "fk_role_permission_by_permission", foreignKeyDefinition = "FOREIGN KEY (permission_id) REFERENCES permission(permission_id) ON UPDATE CASCADE ON DELETE CASCADE"), name = "permission_id", referencedColumnName = "permission_id", nullable = false)
	private PermissionDbEntity permission;

	@Comment(value = "是否允許")
	@Column(name = "is_allow", nullable = false)
	private Boolean isAllow = false;

}
