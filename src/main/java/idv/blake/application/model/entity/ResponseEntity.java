package idv.blake.application.model.entity;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseEntity<Result> implements Serializable {

	@Schema(description = "狀態碼", required = true)
	private int statusCode;

	@Schema(description = "狀態訊息", required = true)
	private String statusMessage;

	@Schema(description = "回傳資料", required = true)
	private Result resultData;
}
