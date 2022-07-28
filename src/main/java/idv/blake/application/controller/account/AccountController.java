package idv.blake.application.controller.account;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/account")
@Tag(name = "檢查服務用")
public class AccountController {

//	@Operation(summary = "取得API狀態", description = "確認服務是否運行中")
//	@RequestMapping(value = "", method = { RequestMethod.GET })
//	public ResponseEntity<String> addPermission(HttpServletRequest request, HttpServletResponse response) {
//
//		ResponseEntity<String> responseEntity = new ResponseEntity<>();
//		responseEntity.setStatusCode(200);
//		responseEntity.setStatusMessage("Success");
//
//		return responseEntity;
//	}

}
