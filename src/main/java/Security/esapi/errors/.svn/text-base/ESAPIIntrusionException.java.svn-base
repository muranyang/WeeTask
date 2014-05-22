package Security.esapi.errors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ESAPIIntrusionException extends RuntimeException {
	
	protected static final long serialVersionUID = 1L;
	
    /** The logger. */
    protected final transient Log logger = LogFactory.getLog(ESAPIIntrusionException.class);

	
	/** The UI reference that caused this ValidationException */
	private String context;

	/**
	 * Instantiates a new validation exception.
	 */
	protected ESAPIIntrusionException() {
		// hidden
	}

    /**
     * Creates a new instance of ValidationException.
     * 
     * @param userMessage
     *            the message to display to users
     * @param logMessage
	 * 			  the message logged
     */
    public ESAPIIntrusionException(String userMessage, String logMessage) {
        super(userMessage);
    }

    /**
     * Instantiates a new ValidationException.
     * 
     * @param userMessage
     *            the message to display to users
     * @param logMessage
	 * 			  the message logged
     * @param cause
     *            the cause
     */
    public ESAPIIntrusionException(String userMessage, String logMessage, Throwable cause) {
        super(userMessage, cause);
    }
    
    /**
     * Creates a new instance of ValidationException.
     * 
     * @param userMessage
     *            the message to display to users
     * @param logMessage
	 * 			  the message logged
     * @param context
     *            the source that caused this exception
     */
    public ESAPIIntrusionException(String userMessage, String logMessage, String context) {
        super(userMessage);
        setContext(context);
    }
    
    /**
     * Instantiates a new ValidationException.
     * 
     * @param userMessage
     *            the message to display to users
     * @param logMessage
	 * 			  the message logged
     * @param cause
     *            the cause
     * @param context
     *            the source that caused this exception
     */
    public ESAPIIntrusionException(String userMessage, String logMessage, Throwable cause, String context) {
        super(userMessage, cause);
    	setContext(context);
    }
    
	/**
	 * Returns the UI reference that caused this ValidationException
	 *  
	 * @return context, the source that caused the exception, stored as a string
	 */
	public String getContext() {
		return context;
	}

	/**
	 * Set's the UI reference that caused this ValidationException
	 *  
	 * @param context
	 * 			the context to set, passed as a String
	 */
	public void setContext(String context) {
		this.context = context;
	}
}
