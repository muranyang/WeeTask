package Security.esapi.errors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ESAPIValidationException extends Exception {
	
	protected static final long serialVersionUID = 1L;
	
    /** The logger. */
    protected final transient Log logger = LogFactory.getLog(ESAPIValidationException.class);

	
	/** The UI reference that caused this ValidationException */
	private String context;

    protected String logMessage = null;
	
	/**
	 * Instantiates a new validation exception.
	 */
	protected ESAPIValidationException() {
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
    public ESAPIValidationException(String userMessage, String logMessage) {
        super(userMessage);
        this.logMessage = logMessage;
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
    public ESAPIValidationException(String userMessage, String logMessage, Throwable cause) {
        super(userMessage, cause);
        this.logMessage = logMessage;
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
    public ESAPIValidationException(String userMessage, String logMessage, String context) {
        super(userMessage);
        setContext(context);
        this.logMessage = logMessage;
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
    public ESAPIValidationException(String userMessage, String logMessage, Throwable cause, String context) {
        super(userMessage, cause);
    	setContext(context);
    	this.logMessage = logMessage;
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

    /**
     * Returns a message that is safe to display in logs, but may contain
     * sensitive information and therefore probably should not be displayed to
     * users.
     * 
     * @return a String containing a message that is safe to display in logs,
     * but probably not to users as it may contain sensitive information.
     */
    public String getLogMessage() {
        return logMessage;
    }
}
