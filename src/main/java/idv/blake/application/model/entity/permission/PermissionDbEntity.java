package idv.blake.application.model.entity.permission;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "permission")
public class PermissionDbEntity {

	@Id
	@Column(name = "permission_id", nullable = false, columnDefinition = "char(8)")
	private String permissionId;

	@Column(name = "api_url", nullable = false, columnDefinition = "varchar(255)")
	private String apiUrl;

	@Column(name = "http_method", nullable = false, columnDefinition = "varchar(5)")
	private String httpMethod = "";

}
