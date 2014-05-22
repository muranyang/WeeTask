package Security.esapi;

import java.util.Set;

import Security.esapi.errors.ESAPIValidationException;

public interface ESAPIValidationRule {

    /**
     * Check if the input is valid, throw an Exception otherwise 
     */
    void assertValid(String context, String input) throws ESAPIValidationException;

    /**
     * Try to call get valid, then call sanitize, finally return a default value
     */
    Object getSafe(String context, String input);

    /**
     * Programmatically supplied name for the validator
     * @return a name, describing the validator
     */
    String getTypeName();

    /**
     * Parse the input, throw exceptions if validation fails
     * 
     * @param context
     *            for logging
     * @param input
     *            the value to be parsed
     * @return a validated value
     * @throws ValidationException
     *             if any validation rules fail
     */
    Object getValid(String context, String input) throws ESAPIValidationException;

    /**
     * Get a validated value, add the errors to an existing error list
     */
    Object getValid(String context, String input, ESAPIValidationErrorList errorList) throws ESAPIValidationException;

    /**
     * @return true if the input passes validation
     */
    boolean isValid(String context, String input);

    /**
     * Whether or not a valid valid can be null. getValid will throw an
     * Exception and getSafe will return the default value if flag is set to
     * true
     * 
     * @param flag
     *            whether or not null values are valid/safe
     */
    void setAllowNull(boolean flag);

    /**
     * @param encoder the encoder to use
     */
    void setEncoder(ESAPIDefaultEncoder encoder);

    /**
     * @param typeName a name, describing the validator
     */
    void setTypeName(String typeName);

    /**
     * String the input of all chars contained in the list
     */
    String whitelist(String input, char[] list);

    /**
     * String the input of all chars contained in the list
     */
    String whitelist(String input, Set<Character> list);

}