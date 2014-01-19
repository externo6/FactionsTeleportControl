package externo6.factionsteleportcontrol.extras;


public class BailingOutException extends RuntimeException {

	public BailingOutException( Throwable cause ) {
		super(cause);
	}
	
	public BailingOutException( String message, Throwable cause) {
		super(message, cause);
	}
}
