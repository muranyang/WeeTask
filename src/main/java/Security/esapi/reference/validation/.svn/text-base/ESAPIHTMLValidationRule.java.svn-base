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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.StringUtilities;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

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
public class ESAPIHTMLValidationRule extends ESAPIStringValidationRule {

    /** OWASP AntiSamy markup verification policy */
    private static Policy    antiSamyPolicy = null;
    private static final Log LOGGER         = LogFactory.getLog(ESAPIHTMLValidationRule.class);

    static {

        try {
            antiSamyPolicy = Policy.getInstance();
        }
        catch (PolicyException e) {

        }

    }

    public ESAPIHTMLValidationRule(String typeName) {
        super(typeName);
    }

    public ESAPIHTMLValidationRule(String typeName, ESAPIDefaultEncoder encoder) {
        super(typeName, encoder);
    }

    public ESAPIHTMLValidationRule(String typeName, ESAPIDefaultEncoder encoder, String whitelistPattern) {
        super(typeName, encoder, whitelistPattern);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValid(String context, String input) throws ESAPIValidationException {
        return invokeAntiSamy(context, input);
    }

    private String invokeAntiSamy(String context, String input) throws ESAPIValidationException {
        // CHECKME should this allow empty Strings? "   " us IsBlank instead?
        if (StringUtilities.isEmpty(input)) {
            if (allowNull) {
                return null;
            }
            throw new ESAPIValidationException(context + " is required", "AntiSamy validation error: context=" + context + ", input=" + input, context);
        }

        String canonical = super.getValid(context, input);

        try {
            AntiSamy as = new AntiSamy();
            CleanResults test = as.scan(canonical, antiSamyPolicy);

            List<String> errors = test.getErrorMessages();
            if (!errors.isEmpty()) {
                LOGGER.info("Cleaned up invalid HTML input: " + errors);
            }

            return test.getCleanHTML().trim();

        }
        catch (ScanException e) {
            throw new ESAPIValidationException(context + ": Invalid HTML input", "Invalid HTML input: context=" + context + " error=" + e.getMessage(), e, context);
        }
        catch (PolicyException e) {
            throw new ESAPIValidationException(context + ": Invalid HTML input", "Invalid HTML input does not follow rules in antisamy-esapi.xml: context=" + context + " error=" + e.getMessage(), e,
                    context);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String sanitize(String context, String input) {
        String safe = "";
        try {
            safe = invokeAntiSamy(context, input);
        }
        catch (ESAPIValidationException e) {
            // just return safe
        }
        return safe;
    }
}
