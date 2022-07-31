package idv.blake.application.model.entity.permission;

import java.io.Serializable;

import lombok.Data;

@Data
public class RolePermissoinPkDbEntity implements Serializable {

	private String role;

	private String permission;

}
