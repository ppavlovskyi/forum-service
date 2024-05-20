package telran.java52.accounting.dto.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectRroleException extends RuntimeException {

	private static final long serialVersionUID = -617235950765921086L;

}
