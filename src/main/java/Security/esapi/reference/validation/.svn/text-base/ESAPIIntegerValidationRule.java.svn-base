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

import org.owasp.esapi.StringUtilities;

import Security.esapi.ESAPIDefaultEncoder;
import Security.esapi.errors.ESAPIValidationException;

/**
 * A validator performs syntax and possibly semantic validation of a single
 * piece of data from an untrusted source.
 * 
 * @author Jeff Williams (jeff.williams .at. aspectsecurity.com) <a
 *         href="http://www.aspectsecurity.com">Aspect Security</a>
 * @since June 1, 2007
 * @see org.owasp.esapi.Validator
 */
public class ESAPIIntegerValidationRule extends ESAPIBaseValidationRule {

    private int minValue = Integer.MIN_VALUE;
    private int maxValue = Integer.MAX_VALUE;

    public ESAPIIntegerValidationRule(String typeName, ESAPIDefaultEncoder encoder) {
        super(typeName, encoder);
    }

    public ESAPIIntegerValidationRule(String typeName, ESAPIDefaultEncoder encoder, int minValue, int maxValue) {
        super(typeName, encoder);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public Integer getValid(String context, String input) throws ESAPIValidationException {
        return safelyParse(context, input);
    }

    private Integer safelyParse(String context, String input) throws ESAPIValidationException {
        // do not allow empty Strings such as "   " - so trim to ensure 
        // isEmpty catches "    "
        if (input != null) {
            input = input.trim();
        }

        if (StringUtilities.isEmpty(input)) {
            if (allowNull) {
                return null;
            }
            throw new ESAPIValidationException(context + ": Input number required", "Input number required: context=" + context + ", input=" + input, context);
        }

        // canonicalize
        String canonical = encoder.canonicalize(input);

        if (minValue > maxValue) {
            throw new ESAPIValidationException(context + ": Invalid number input: context", "Validation parameter error for number: maxValue ( " + maxValue + ") must be greater than minValue ( "
                    + minValue + ") for " + context, context);
        }

        // validate min and max
        try {
            int i = Integer.valueOf(canonical);
            if (i < minValue) {
                throw new ESAPIValidationException("Invalid number input must be between " + minValue + " and " + maxValue + ": context=" + context, "Invalid number input must be between " + minValue
                        + " and " + maxValue + ": context=" + context + ", input=" + input, context);
            }
            if (i > maxValue) {
                throw new ESAPIValidationException("Invalid number input must be between " + minValue + " and " + maxValue + ": context=" + context, "Invalid number input must be between " + minValue
                        + " and " + maxValue + ": context=" + context + ", input=" + input, context);
            }
            return i;
        }
        catch (NumberFormatException e) {
            throw new ESAPIValidationException(context + ": Invalid number input", "Invalid number input format: context=" + context + ", input=" + input, e, context);
        }
    }

    @Override
    public Integer sanitize(String context, String input) {
        Integer toReturn = Integer.valueOf(0);
        try {
            toReturn = safelyParse(context, input);
        }
        catch (ESAPIValidationException e) {
            // do nothing
        }
        return toReturn;
    }
}