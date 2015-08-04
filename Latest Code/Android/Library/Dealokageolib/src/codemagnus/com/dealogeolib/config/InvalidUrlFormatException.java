package codemagnus.com.dealogeolib.config;

/**
 * Created by codemagnus on 5/14/15.
 */
public class InvalidUrlFormatException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidUrlFormatException(String message, Throwable t){
        super(message, t);
    }

    public InvalidUrlFormatException(String message){
        super(message);
    }
}
