package idv.blake.application.model.entity.permission;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "role")
@AllArgsConstructor
@NoArgsConstructor
@org.hibernate.annotations.Table(comment = "角色", appliesTo = "role")
public class RoleDbEntity {

	@Id
	@Comment(value = "角色ID")
	@Column(name = "role_id", nullable = false, columnDefinition = "char(32)")
	private String roleId;

	@Comment(value = "角色名稱")
	@Column(name = "role_name", nullable = false, columnDefinition = "char(32)")
	private String roleName;

	@OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
	private List<RolePermissionDbEntity> rolePermission;

	public RoleDbEntity(String roleId, String roleName) {
		this.roleId = roleId;
		this.roleName = roleName;
	}

}
