package idv.blake.application.controller.permission;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import idv.blake.application.model.entity.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/permission")
@Tag(name = "權限管理")
public class PermissoinController {

	@Operation(summary = "取得權限", description = "取得帳號權限")
	@RequestMapping(value = "", method = { RequestMethod.GET })
	public ResponseEntity<String> addPermission(HttpServletRequest request, HttpServletResponse response) {

		return new ResponseEntity<>();
	}

}
