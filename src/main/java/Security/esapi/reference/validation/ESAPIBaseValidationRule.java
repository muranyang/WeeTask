/**
 * OWASP Enterprise Security API (ESAPI)
 * 
 * This file is part of the Open Web Application Security Project (OWASP)
 * Enterprise Security API (ESAPI) project. For details, please see
 * <a href="http://www.owasp.org/index.php/ESAPI">http://www.owasp.org/index.php/ESAPI</a>.
 *
 * Copyright (c) 2007 - The OWASP Foundation
 * 
 * The ESAPI is published by OWASP under the BSD license. You should read and accept the
 * LICENSE before you use, modify, and/or redistribute this software.
 * 
 * @author Jeff Williams <a href="http://www.aspectsecurity.com">Aspect Security</a>
 * @created 2007
 */
package Security.esapi.reference.validation;

import java.util.HashSet;
import java.util.Set;

import Security.esapi.ESAPIDefaultEncoder;
import Security.esapi.ESAPIValidationErrorList;
import Security.esapi.ESAPIValidationRule;
import Security.esapi.errors.ESAPIValidationException;

/**
 * A ValidationRule performs syntax and possibly semantic validation of a single
 * piece of data from an untrusted source.
 * 
 * @author Jeff Williams (jeff.williams .at. aspectsecurity.com) <a
 *         href="http://www.aspectsecurity.com">Aspect Security</a>
 * @since June 1, 2007
 * @see org.owasp.esapi.Validator
 */
public abstract class ESAPIBaseValidationRule implements ESAPIValidationRule {

    // CHECKME should be moved to some utility class (Would potentially be used by new EncoderConstants class)
    // Is there a standard way to convert an array of primitives to a Collection
    /**
     * Convert an array of characters to a {@code Set<Character>} (so duplicates
     * are removed).
     * @param array The character array.
     * @return A {@code Set<Character>} of the unique characters from {@code array}
     *         is returned.
     */
    public static Set<Character> charArrayToSet(char[] array) {
        Set<Character> toReturn = new HashSet<Character>(array.length);
        for (char c : array) {
            toReturn.add(c);
        }
        return toReturn;
    }

    private String                typeName  = null;
    protected boolean             allowNull = false;

    protected ESAPIDefaultEncoder encoder   = null;

    private ESAPIBaseValidationRule() {
        // prevent use of no-arg constructor
    }

    public ESAPIBaseValidationRule(String typeName) {
        this();
        setEncoder(ESAPIDefaultEncoder.getInstance());
        setTypeName(typeName);
    }

    public ESAPIBaseValidationRule(String typeName, ESAPIDefaultEncoder encoder) {
        this();
        setEncoder(encoder);
        setTypeName(typeName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void assertValid(String context, String input) throws ESAPIValidationException {
        getValid(context, input, null);
    }

    public ESAPIDefaultEncoder getEncoder() {
        return encoder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getSafe(String context, String input) {
        Object valid = null;
        try {
            valid = getValid(context, input);
        }
        catch (ESAPIValidationException e) {
            return sanitize(context, input);
        }
        return valid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeName() {
        return typeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValid(String context, String input, ESAPIValidationErrorList errorList) throws ESAPIValidationException {
        Object valid = null;
        try {
            valid = getValid(context, input);
        }
        catch (ESAPIValidationException e) {
            //errorList.addError(context, e);
        }
        return valid;
    }

    public boolean isAllowNull() {
        return allowNull;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(String context, String input) {
        boolean valid = false;
        try {
            getValid(context, input);
            valid = true;
        }
        catch (Exception e) {
            valid = false;
        }

        return valid;
    }

    /**
     * The method is similar to ValidationRuile.getSafe except that it returns a
     * harmless object that <b>may or may not have any similarity to the original
     * input (in some cases you may not care)</b>. In most cases this should be the
     * same as the getSafe method only instead of throwing an exception, return
     * some default value.
     * 
     * @param context
     * @param input
     * @return a parsed version of the input or a default value.
     */
    protected abstract Object sanitize(String context, String input);

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAllowNull(boolean flag) {
        allowNull = flag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setEncoder(ESAPIDefaultEncoder encoder) {
        this.encoder = encoder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String whitelist(String input, char[] whitelist) {
        return whitelist(input, charArrayToSet(whitelist));
    }

    /**
     * Removes characters that aren't in the whitelist from the input String.  
     * O(input.length) whitelist performance
     * @param input String to be sanitized
     * @param whitelist allowed characters
     * @return input stripped of all chars that aren't in the whitelist 
     */
    @Override
    public String whitelist(String input, Set<Character> whitelist) {
        StringBuilder stripped = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (whitelist.contains(c)) {
                stripped.append(c);
            }
        }
        return stripped.toString();
    }
}
