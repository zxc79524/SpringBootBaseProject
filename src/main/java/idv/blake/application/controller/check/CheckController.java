package idv.blake.application.controller.check;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import idv.blake.application.model.entity.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/check")
@Tag(name = "檢查服務用")
public class CheckController {

	@Operation(summary = "取得API狀態", description = "確認服務是否運行中")
	@RequestMapping(value = "", method = { RequestMethod.GET })
	public ResponseEntity<String> check(HttpServletRequest request, HttpServletResponse response) {

		ResponseEntity<String> responseEntity = new ResponseEntity<>();
		responseEntity.setStatusCode(200);
		responseEntity.setStatusMessage("Success");

		return responseEntity;
	}

	@Operation(summary = "檢查token 是否有效", description = "確認服務是否運行中")
	@RequestMapping(value = "/token", method = { RequestMethod.GET })
	public ResponseEntity<String> checkToken(HttpServletRequest request, HttpServletResponse response) {

		ResponseEntity<String> responseEntity = new ResponseEntity<>();
		responseEntity.setStatusCode(200);
		responseEntity.setStatusMessage("Success");

		return responseEntity;
	}

	@Operation(summary = "檢查token 是否有效", description = "確認服務是否運行中")
	@RequestMapping(value = "/token/{code}", method = { RequestMethod.GET })
	public ResponseEntity<String> checkToken(@PathVariable("code") String code, HttpServletRequest request,
			HttpServletResponse response) {

		ResponseEntity<String> responseEntity = new ResponseEntity<>();
		responseEntity.setStatusCode(200);
		responseEntity.setStatusMessage("Success");

		return responseEntity;
	}

}
