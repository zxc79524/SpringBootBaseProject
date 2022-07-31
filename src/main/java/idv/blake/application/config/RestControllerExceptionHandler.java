package idv.blake.application.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import idv.blake.application.model.entity.ResponseEntity;
import idv.blake.application.model.exception.AlreadyExistExceptionException;
import idv.blake.application.model.exception.InvalidArgumentException;
import idv.blake.application.model.exception.PermissionException;
import idv.blake.application.model.exception.UnauthorizedException;

@RestControllerAdvice
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(InvalidArgumentException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public ResponseEntity<String> handleExceptions(InvalidArgumentException exception) {
		exception.printStackTrace();

		ResponseEntity<String> responseEntity = new ResponseEntity<>();
		responseEntity.setStatusCode(HttpStatus.BAD_REQUEST.value());
		responseEntity.setStatusMessage(exception.getMessage());

		return responseEntity;
	}

	@ExceptionHandler(AlreadyExistExceptionException.class)
	@ResponseStatus(value = HttpStatus.CONFLICT)
	public ResponseEntity<String> handleExceptions(AlreadyExistExceptionException exception) {
		exception.printStackTrace();

		ResponseEntity<String> responseEntity = new ResponseEntity<>();
		responseEntity.setStatusCode(HttpStatus.CONFLICT.value());
		responseEntity.setStatusMessage(exception.getMessage());

		return responseEntity;
	}

	@ExceptionHandler(PermissionException.class)
	@ResponseStatus(value = HttpStatus.FORBIDDEN)
	public void handlePermissionException() {
	}

	@ExceptionHandler({ UnauthorizedException.class })
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public void handleUnauthorizedException(UnauthorizedException exception) {
		exception.printStackTrace();
	}

}
