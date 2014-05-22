package Security.esapi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import Security.esapi.reference.validation.ESAPIDateValidationRule;
import Security.esapi.reference.validation.ESAPIHTMLValidationRule;
import Security.esapi.reference.validation.ESAPIIntegerValidationRule;
import Security.esapi.reference.validation.ESAPINumberValidationRule;
import Security.esapi.reference.validation.ESAPIStringValidationRule;

import Security.esapi.errors.ESAPIIntrusionException;
import Security.esapi.errors.ESAPIValidationException;

public class ESAPIDefaultValidator {
    private static volatile ESAPIDefaultValidator instance = null;

    public static ESAPIDefaultValidator getInstance() {
        if ( instance == null ) {
            synchronized ( ESAPIDefaultValidator.class ) {
                if ( instance == null ) {
                    instance = new ESAPIDefaultValidator();
                }
            }
        }
        return instance;
    }

	/** A map of validation rules */
	private Map<String, ESAPIValidationRule> rules = new HashMap<String, ESAPIValidationRule>();

	/** The encoder to use for canonicalization */
	private ESAPIDefaultEncoder encoder = null;

	/** The encoder to use for file system */
	private static ESAPIDefaultValidator fileValidator = null;

	private Map<String,Pattern> patterns = new HashMap<String,Pattern>();

	/** Initialize file validator with an appropriate set of codecs */
	static {
		List<String> list = new ArrayList<String>();
		list.add( "HTMLEntityCodec" );
		list.add( "PercentCodec" );
		ESAPIDefaultEncoder fileEncoder = new ESAPIDefaultEncoder( list );
		fileValidator = new ESAPIDefaultValidator( fileEncoder );
	}


	/**
	 * Default constructor uses the ESAPI standard encoder for canonicalization.
	 */
	public ESAPIDefaultValidator() {
	    this.encoder = ESAPIDefaultEncoder.getInstance();

	    //Fill in patterns map
		//patterns.put("SafeString",Pattern.compile("^[\p{L}\p{N}.]{0,1024}$"));
	    patterns.put("Email",Pattern.compile("^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,4}$"));
	    patterns.put("IPAddress",Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"));
	    patterns.put("URL",Pattern.compile("^(ht|f)tp(s?)\\:\\/\\/[0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*(:(0-9)*)*(\\/?)([a-zA-Z0-9\\-\\.\\?\\,\\:\\'\\/\\\\\\+=&amp;%\\$#_]*)?$"));
	    patterns.put("SSN",Pattern.compile("^(?!000)([0-6]\\d{2}|7([0-6]\\d|7[012]))([ -]?)(?!00)\\d\\d\\3(?!0000)\\d{4}$"));

	    patterns.put("Redirect",Pattern.compile("^\\/AIR.*$"));

	    // Global HTTP Validation Rules
        patterns.put("HTTPScheme", Pattern.compile("^(http|https)$"));
        patterns.put("HTTPServerName", Pattern.compile("^[a-zA-Z0-9_.\\-]*$"));
        patterns.put("HTTPParameterName", Pattern.compile("^[a-zA-Z0-9_\\[\\]\\.]{1,32}$"));
        patterns.put("HTTPParameterValue", Pattern.compile("^.*$", Pattern.DOTALL));
        patterns.put("HTTPParameterValueLang", Pattern.compile("^[a-zA-Z0-9_\\-]{1,32}$"));
        patterns.put("HTTPCookieName", Pattern.compile("^[a-zA-Z0-9\\-_\\.]{1,40}$"));
        patterns.put("HTTPCookieValue", Pattern.compile("^[a-zA-Z0-9\\-\\/+=_ \\.]*$"));
        patterns.put("HTTPHeaderName", Pattern.compile("^[a-zA-Z0-9\\-_]{1,32}$"));
        patterns.put("HTTPHeaderValue", Pattern.compile("^[a-zA-Z0-9()\"\\-=\\*\\.\\?;,+\\/:&_ ]*$"));
        patterns.put("HTTPContextPath", Pattern.compile("^\\/?[a-zA-Z0-9.\\-\\/_]*$"));
        patterns.put("HTTPServletPath", Pattern.compile("^[a-zA-Z0-9.\\-\\/_]*$"));
        patterns.put("HTTPPath", Pattern.compile("^[a-zA-Z0-9.\\-\\/_]*$"));
        patterns.put("HTTPQueryString", Pattern.compile("^[a-zA-Z0-9()\\-=\\*\\.\\?;,+\\/:&_ %]*$"));
        patterns.put("HTTPURI", Pattern.compile("^[a-zA-Z0-9()\\-=\\*\\.\\?;,+\\/:&_ ]*$"));
        patterns.put("HTTPURL", Pattern.compile("^.*$"));
        patterns.put("HTTPJSESSIONID", Pattern.compile("^[A-Z0-9]{10,32}$"));

	}

	/**
	 * Construct a new DefaultValidator that will use the specified
	 * Encoder for canonicalization.
     *
     * @param encoder
     */
	public ESAPIDefaultValidator( ESAPIDefaultEncoder encoder ) {
	    this.encoder = encoder;
	}


	/**
	 * Add a validation rule to the registry using the "type name" of the rule as the key.
	 */
	public void addRule( ESAPIValidationRule rule ) {
		rules.put( rule.getTypeName(), rule );
	}

	/**
	 * Get a validation rule from the registry with the "type name" of the rule as the key.
	 */
	public ESAPIValidationRule getRule( String name ) {
		return rules.get( name );
	}


	/**
	 * Returns true if data received from browser is valid. Double encoding is treated as an attack. The
	 * default encoder supports html encoding, URL encoding, and javascript escaping. Input is canonicalized
	 * by default before validation.
	 *
	 * @param context A descriptive name for the field to validate. This is used for error facing validation messages and element identification.
	 * @param input The actual user input data to validate.
	 * @param type The regular expression name while maps to the actual regular expression from "ESAPI.properties".
	 * @param maxLength The maximum post-canonicalized String length allowed.
	 * @param allowNull If allowNull is true then a input that is NULL or an empty string will be legal. If allowNull is false then NULL or an empty String will throw a ValidationException.
	 * @return The canonicalized user input.
	 * @throws ESAPIIntrusionException
	 */
	public boolean isValidInput(String context, String input, String type, int maxLength, boolean allowNull) {
		return isValidInput(context, input, type, maxLength, allowNull, true);
	}

        public boolean isValidInput(String context, String input, String type, int maxLength, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException  {
		return isValidInput(context, input, type, maxLength, allowNull, true, errors);
	}

	public boolean isValidInput(String context, String input, String type, int maxLength, boolean allowNull, boolean canonicalize) {
		try {
			getValidInput( context, input, type, maxLength, allowNull, canonicalize);
			return true;
		} catch( Exception e ) {
			return false;
		}
	}

        public boolean isValidInput(String context, String input, String type, int maxLength, boolean allowNull, boolean canonicalize, ESAPIValidationErrorList errors) throws ESAPIIntrusionException  {
		try {
			getValidInput( context, input, type, maxLength, allowNull, canonicalize);
			return true;
		} catch( ESAPIValidationException e ) {
			//errors.addError( context, e );
			return false;
		}
	}

	/**
	 * Validates data received from the browser and returns a safe version.
	 * Double encoding is treated as an attack. The default encoder supports
	 * html encoding, URL encoding, and javascript escaping. Input is
	 * canonicalized by default before validation.
	 *
	 * @param context A descriptive name for the field to validate. This is used for error facing validation messages and element identification.
	 * @param input The actual user input data to validate.
	 * @param type The regular expression name which maps to the actual regular expression from "ESAPI.properties".
	 * @param maxLength The maximum post-canonicalized String length allowed.
	 * @param allowNull If allowNull is true then a input that is NULL or an empty string will be legal. If allowNull is false then NULL or an empty String will throw a ValidationException.
	 * @return The canonicalized user input.
	 * @throws ValidationException
	 * @throws ESAPIIntrusionException
	 */
	public String getValidInput(String context, String input, String type, int maxLength, boolean allowNull) throws ESAPIValidationException {
		return getValidInput(context, input, type, maxLength, allowNull, true);
	}

	/**
	 * Validates data received from the browser and returns a safe version. Only
	 * URL encoding is supported. Double encoding is treated as an attack.
	 *
	 * @param context A descriptive name for the field to validate. This is used for error facing validation messages and element identification.
	 * @param input The actual user input data to validate.
	 * @param type The regular expression name which maps to the actual regular expression in the ESAPI validation configuration file
	 * @param maxLength The maximum String length allowed. If input is canonicalized per the canonicalize argument, then maxLength must be verified after canonicalization
     * @param allowNull If allowNull is true then a input that is NULL or an empty string will be legal. If allowNull is false then NULL or an empty String will throw a ValidationException.
	 * @param canonicalize If canonicalize is true then input will be canonicalized before validation
	 * @return The user input, may be canonicalized if canonicalize argument is true
	 * @throws ValidationException
	 * @throws ESAPIIntrusionException
	 */
	public String getValidInput(String context, String input, String type, int maxLength, boolean allowNull, boolean canonicalize) throws ESAPIValidationException {
		ESAPIStringValidationRule rvr = new ESAPIStringValidationRule( type, encoder );

		Pattern p = patterns.get( type );
		if ( p != null ) {
			rvr.addWhitelistPattern( p );
		} else {
            // Issue 232 - Specify requested type in exception message - CS
			throw new IllegalArgumentException("The selected type [" + type + "] was not set via the ESAPI validation configuration");
		}
		// "lang" parameters require special processing for XSS attacks
        if ("HTTP parameter name: lang".equals(context) && input != null)
        {
            // Explicitly check against lang parameter
            Pattern pLang = patterns.get("HTTPParameterValueLang");
            if (!pLang.matcher(input).matches())
            {
                return null;
            }
        }
		rvr.setMaximumLength(maxLength);
		rvr.setAllowNull(allowNull);
		rvr.setValidateInputAndCanonical(canonicalize);
		return rvr.getValid(context, input);
	}

	/**
	 * Validates data received from the browser and returns a safe version. Only
	 * URL encoding is supported. Double encoding is treated as an attack. Input
	 * is canonicalized by default before validation.
	 *
	 * @param context A descriptive name for the field to validate. This is used for error facing validation messages and element identification.
	 * @param input The actual user input data to validate.
	 * @param type The regular expression name while maps to the actual regular expression from "ESAPI.properties".
	 * @param maxLength The maximum String length allowed. If input is canonicalized per the canonicalize argument, then maxLength must be verified after canonicalization
	 * @param allowNull If allowNull is true then a input that is NULL or an empty string will be legal. If allowNull is false then NULL or an empty String will throw a ValidationException.
	 * @param errors If ValidationException is thrown, then add to error list instead of throwing out to caller
	 * @return The canonicalized user input.
	 * @throws ESAPIIntrusionException
	 */
	public String getValidInput(String context, String input, String type, int maxLength, boolean allowNull, ESAPIValidationErrorList errors) {
		return getValidInput(context, input, type, maxLength, allowNull, true, errors);
	}

	/**
	 * Validates data received from the browser and returns a safe version. Only
	 * URL encoding is supported. Double encoding is treated as an attack.
	 *
	 * @param context A descriptive name for the field to validate. This is used for error facing validation messages and element identification.
	 * @param input The actual user input data to validate.
	 * @param type The regular expression name while maps to the actual regular expression from "ESAPI.properties".
	 * @param maxLength The maximum post-canonicalized String length allowed
	 * @param allowNull If allowNull is true then a input that is NULL or an empty string will be legal. If allowNull is false then NULL or an empty String will throw a ValidationException.
	 * @param canonicalize If canonicalize is true then input will be canonicalized before validation
	 * @param errors If ValidationException is thrown, then add to error list instead of throwing out to caller
	 * @return The user input, may be canonicalized if canonicalize argument is true
	 * @throws ESAPIIntrusionException
	 */
	public String getValidInput(String context, String input, String type, int maxLength, boolean allowNull, boolean canonicalize, ESAPIValidationErrorList errors) {
		try {
			return getValidInput(context,  input,  type,  maxLength,  allowNull, canonicalize);
		} catch (ESAPIValidationException e) {
			//errors.addError(context, e);
		}

		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValidDate(String context, String input, DateFormat format, boolean allowNull) throws ESAPIIntrusionException {
		try {
			getValidDate( context, input, format, allowNull);
			return true;
		} catch( Exception e ) {
			return false;
		}
	}

        /**
	 * {@inheritDoc}
	 */
	public boolean isValidDate(String context, String input, DateFormat format, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			getValidDate( context, input, format, allowNull);
			return true;
		} catch( ESAPIValidationException e ) {
            //errors.addError(context, e);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Date getValidDate(String context, String input, DateFormat format, boolean allowNull) throws ESAPIValidationException, ESAPIIntrusionException {
		ESAPIDateValidationRule dvr = new ESAPIDateValidationRule( "SimpleDate", encoder, format);
		dvr.setAllowNull(allowNull);
		return dvr.getValid(context, input);
	}

	/**
	 * {@inheritDoc}
	 */
	public Date getValidDate(String context, String input, DateFormat format, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			return getValidDate(context, input, format, allowNull);
		} catch (ESAPIValidationException e) {
			//errors.addError(context, e);
		}
		// error has been added to list, so return null
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValidSafeHTML(String context, String input, int maxLength, boolean allowNull) throws ESAPIIntrusionException {
		try {
			getValidSafeHTML( context, input, maxLength, allowNull);
			return true;
		} catch( Exception e ) {
			return false;
		}
	}

        /**
	 * {@inheritDoc}
	 */
	public boolean isValidSafeHTML(String context, String input, int maxLength, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			getValidSafeHTML( context, input, maxLength, allowNull);
			return true;
		} catch( ESAPIValidationException e ) {
            //errors.addError(context, e);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * This implementation relies on the OWASP AntiSamy project.
	 */
	public String getValidSafeHTML( String context, String input, int maxLength, boolean allowNull ) throws ESAPIValidationException, ESAPIIntrusionException {
		ESAPIHTMLValidationRule hvr = new ESAPIHTMLValidationRule( "safehtml", encoder );
		hvr.setMaximumLength(maxLength);
		hvr.setAllowNull(allowNull);
		hvr.setValidateInputAndCanonical(false);
		return hvr.getValid(context, input);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getValidSafeHTML(String context, String input, int maxLength, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			return getValidSafeHTML(context, input, maxLength, allowNull);
		} catch (ESAPIValidationException e) {
			//errors.addError(context, e);
		}

		return "";
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p><b>Note:</b> On platforms that support symlinks, this function will fail canonicalization if directorypath
	 * is a symlink. For example, on MacOS X, /etc is actually /private/etc. If you mean to use /etc, use its real
	 * path (/private/etc), not the symlink (/etc).</p>
	 */
	public boolean isValidDirectoryPath(String context, String input, File parent, boolean allowNull) throws ESAPIIntrusionException {
		try {
			getValidDirectoryPath( context, input, parent, allowNull);
			return true;
		} catch( Exception e ) {
			return false;
		}
	}

        /**
	 * {@inheritDoc}
	 *
	 * <p><b>Note:</b> On platforms that support symlinks, this function will fail canonicalization if directorypath
	 * is a symlink. For example, on MacOS X, /etc is actually /private/etc. If you mean to use /etc, use its real
	 * path (/private/etc), not the symlink (/etc).</p>
	 */
	public boolean isValidDirectoryPath(String context, String input, File parent, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			getValidDirectoryPath( context, input, parent, allowNull);
			return true;
		} catch( ESAPIValidationException e ) {
            //errors.addError(context, e);
			return false;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	public String getValidDirectoryPath(String context, String input, File parent, boolean allowNull) throws ESAPIValidationException, ESAPIIntrusionException {
		try {
			if (isEmpty(input)) {
				if (allowNull) return null;
       			throw new ESAPIValidationException( context + ": Input directory path required", "Input directory path required: context=" + context + ", input=" + input, context );
			}

			File dir = new File( input );

			// check dir exists and parent exists and dir is inside parent
			if ( !dir.exists() ) {
				throw new ESAPIValidationException( context + ": Invalid directory name", "Invalid directory, does not exist: context=" + context + ", input=" + input );
			}
			if ( !dir.isDirectory() ) {
				throw new ESAPIValidationException( context + ": Invalid directory name", "Invalid directory, not a directory: context=" + context + ", input=" + input );
			}
			if ( !parent.exists() ) {
				throw new ESAPIValidationException( context + ": Invalid directory name", "Invalid directory, specified parent does not exist: context=" + context + ", input=" + input + ", parent=" + parent );
			}
			if ( !parent.isDirectory() ) {
				throw new ESAPIValidationException( context + ": Invalid directory name", "Invalid directory, specified parent is not a directory: context=" + context + ", input=" + input + ", parent=" + parent );
			}
			if ( !dir.getCanonicalPath().startsWith(parent.getCanonicalPath() ) ) {
				throw new ESAPIValidationException( context + ": Invalid directory name", "Invalid directory, not inside specified parent: context=" + context + ", input=" + input + ", parent=" + parent );
			}

			// check canonical form matches input
			String canonicalPath = dir.getCanonicalPath();
			String canonical = fileValidator.getValidInput( context, canonicalPath, "DirectoryName", 255, false);
			if ( !canonical.equals( input ) ) {
				throw new ESAPIValidationException( context + ": Invalid directory name", "Invalid directory name does not match the canonical path: context=" + context + ", input=" + input + ", canonical=" + canonical, context );
			}
			return canonical;
		} catch (Exception e) {
			throw new ESAPIValidationException( context + ": Invalid directory name", "Failure to validate directory path: context=" + context + ", input=" + input, e, context );
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getValidDirectoryPath(String context, String input, File parent, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {

		try {
			return getValidDirectoryPath(context, input, parent, allowNull);
		} catch (ESAPIValidationException e) {
			//errors.addError(context, e);
		}

		return "";
	}


	/**
	 * {@inheritDoc}
	 */
	public boolean isValidFileName(String context, String input, boolean allowNull) throws ESAPIIntrusionException {
		return isValidFileName( context, input, Arrays.asList(".zip,.pdf,.tar,.gz,.xls,.properties,.txt,.xml".split(",")), allowNull );
	}

        /**
	 * {@inheritDoc}
	 */
	public boolean isValidFileName(String context, String input, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		return isValidFileName( context, input, Arrays.asList(".zip,.pdf,.tar,.gz,.xls,.properties,.txt,.xml".split(",")), allowNull, errors );
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValidFileName(String context, String input, List<String> allowedExtensions, boolean allowNull) throws ESAPIIntrusionException {
		try {
			getValidFileName( context, input, allowedExtensions, allowNull);
			return true;
		} catch( Exception e ) {
			return false;
		}
	}

        /**
	 * {@inheritDoc}
	 */
	public boolean isValidFileName(String context, String input, List<String> allowedExtensions, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			getValidFileName( context, input, allowedExtensions, allowNull);
			return true;
		} catch( ESAPIValidationException e ) {
            //errors.addError(context, e);
			return false;
		}
	}
	/**
	 * {@inheritDoc}
	 */
	public String getValidFileName(String context, String input, List<String> allowedExtensions, boolean allowNull) throws ESAPIValidationException, ESAPIIntrusionException {
		if ((allowedExtensions == null) || (allowedExtensions.isEmpty())) {
			throw new ESAPIValidationException( "Internal Error", "getValidFileName called with an empty or null list of allowed Extensions, therefore no files can be uploaded" );
		}

		String canonical = "";
		// detect path manipulation
		try {
			if (isEmpty(input)) {
				if (allowNull) return null;
	   			throw new ESAPIValidationException( context + ": Input file name required", "Input required: context=" + context + ", input=" + input, context );
			}

			// do basic validation
	        canonical = new File(input).getCanonicalFile().getName();
	        getValidInput( context, input, "FileName", 255, true );

			File f = new File(canonical);
			String c = f.getCanonicalPath();
			String cpath = c.substring(c.lastIndexOf(File.separator) + 1);


			// the path is valid if the input matches the canonical path
			if (!input.equals(cpath)) {
				throw new ESAPIValidationException( context + ": Invalid file name", "Invalid directory name does not match the canonical path: context=" + context + ", input=" + input + ", canonical=" + canonical, context );
			}

		} catch (IOException e) {
			throw new ESAPIValidationException( context + ": Invalid file name", "Invalid file name does not exist: context=" + context + ", canonical=" + canonical, e, context );
		}

		// verify extensions
		Iterator<String> i = allowedExtensions.iterator();
		while (i.hasNext()) {
			String ext = i.next();
			if (input.toLowerCase().endsWith(ext.toLowerCase())) {
				return canonical;
			}
		}
		throw new ESAPIValidationException( context + ": Invalid file name does not have valid extension ( "+allowedExtensions+")", "Invalid file name does not have valid extension ( "+allowedExtensions+"): context=" + context+", input=" + input, context );
	}

	/**
	 * {@inheritDoc}
	 */
	public String getValidFileName(String context, String input, List<String> allowedParameters, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			return getValidFileName(context, input, allowedParameters, allowNull);
		} catch (ESAPIValidationException e) {
			//errors.addError(context, e);
		}

		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValidNumber(String context, String input, long minValue, long maxValue, boolean allowNull) throws ESAPIIntrusionException {
		try {
			getValidNumber(context, input, minValue, maxValue, allowNull);
			return true;
		} catch( Exception e ) {
			return false;
		}
	}

        /**
	 * {@inheritDoc}
	 */
	public boolean isValidNumber(String context, String input, long minValue, long maxValue, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			getValidNumber(context, input, minValue, maxValue, allowNull);
			return true;
		} catch( ESAPIValidationException e ) {
            //errors.addError(context, e);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Double getValidNumber(String context, String input, long minValue, long maxValue, boolean allowNull) throws ESAPIValidationException, ESAPIIntrusionException {
		Double minDoubleValue = new Double(minValue);
		Double maxDoubleValue = new Double(maxValue);
		return getValidDouble(context, input, minDoubleValue.doubleValue(), maxDoubleValue.doubleValue(), allowNull);
	}

	/**
	 * {@inheritDoc}
	 */
	public Double getValidNumber(String context, String input, long minValue, long maxValue, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			return getValidNumber(context, input, minValue, maxValue, allowNull);
		} catch (ESAPIValidationException e) {
			//errors.addError(context, e);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValidDouble(String context, String input, double minValue, double maxValue, boolean allowNull) throws ESAPIIntrusionException {
        try {
            getValidDouble( context, input, minValue, maxValue, allowNull );
            return true;
        } catch( Exception e ) {
            return false;
        }
	}

        /**
	 * {@inheritDoc}
	 */
	public boolean isValidDouble(String context, String input, double minValue, double maxValue, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
        try {
            getValidDouble( context, input, minValue, maxValue, allowNull );
            return true;
        } catch( ESAPIValidationException e ) {
            //errors.addError(context, e);
            return false;
        }
	}

	/**
	 * {@inheritDoc}
	 */
	public Double getValidDouble(String context, String input, double minValue, double maxValue, boolean allowNull) throws ESAPIValidationException, ESAPIIntrusionException {
		ESAPINumberValidationRule nvr = new ESAPINumberValidationRule( "number", encoder, minValue, maxValue );
		nvr.setAllowNull(allowNull);
		return nvr.getValid(context, input);
	}

	/**
	 * {@inheritDoc}
	 */
	public Double getValidDouble(String context, String input, double minValue, double maxValue, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			return getValidDouble(context, input, minValue, maxValue, allowNull);
		} catch (ESAPIValidationException e) {
			//errors.addError(context, e);
		}

		return new Double(Double.NaN);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValidInteger(String context, String input, int minValue, int maxValue, boolean allowNull) throws ESAPIIntrusionException {
		try {
			getValidInteger( context, input, minValue, maxValue, allowNull);
			return true;
		} catch( ESAPIValidationException e ) {
			return false;
		}
	}

        /**
	 * {@inheritDoc}
	 */
	public boolean isValidInteger(String context, String input, int minValue, int maxValue, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			getValidInteger( context, input, minValue, maxValue, allowNull);
			return true;
		} catch( ESAPIValidationException e ) {
            //errors.addError(context, e);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Integer getValidInteger(String context, String input, int minValue, int maxValue, boolean allowNull) throws ESAPIValidationException, ESAPIIntrusionException {
		ESAPIIntegerValidationRule ivr = new ESAPIIntegerValidationRule( "number", encoder, minValue, maxValue );
		ivr.setAllowNull(allowNull);
		return ivr.getValid(context, input);
	}

	/**
	 * {@inheritDoc}
	 */
	public Integer getValidInteger(String context, String input, int minValue, int maxValue, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			return getValidInteger(context, input, minValue, maxValue, allowNull);
		} catch (ESAPIValidationException e) {
			//errors.addError(context, e);
		}
		// error has been added to list, so return original input
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isValidFileContent(String context, byte[] input, int maxBytes, boolean allowNull) throws ESAPIIntrusionException {
		try {
			getValidFileContent( context, input, maxBytes, allowNull);
			return true;
		} catch( Exception e ) {
			return false;
		}
	}

        /**
	 * {@inheritDoc}
	 */
	public boolean isValidFileContent(String context, byte[] input, int maxBytes, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			getValidFileContent( context, input, maxBytes, allowNull);
			return true;
		} catch( ESAPIValidationException e ) {
            //errors.addError(context, e);
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public byte[] getValidFileContent(String context, byte[] input, int maxBytes, boolean allowNull) throws ESAPIValidationException, ESAPIIntrusionException {
		if (isEmpty(input)) {
			if (allowNull) return null;
   			throw new ESAPIValidationException( context + ": Input required", "Input required: context=" + context + ", input=" + input, context );
		}

		long esapiMaxBytes = 5000000;
		if (input.length > esapiMaxBytes ) throw new ESAPIValidationException( context + ": Invalid file content can not exceed " + esapiMaxBytes + " bytes", "Exceeded ESAPI max length", context );
		if (input.length > maxBytes ) throw new ESAPIValidationException( context + ": Invalid file content can not exceed " + maxBytes + " bytes", "Exceeded maxBytes ( " + input.length + ")", context );

		return input;
	}

	/**
	 * {@inheritDoc}
	 */
	public byte[] getValidFileContent(String context, byte[] input, int maxBytes, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			return getValidFileContent(context, input, maxBytes, allowNull);
		} catch (ESAPIValidationException e) {
			//errors.addError(context, e);
		}
		// return empty byte array on error
		return new byte[0];
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p><b>Note:</b> On platforms that support symlinks, this function will fail canonicalization if directorypath
	 * is a symlink. For example, on MacOS X, /etc is actually /private/etc. If you mean to use /etc, use its real
	 * path (/private/etc), not the symlink (/etc).</p>
     */
	public boolean isValidFileUpload(String context, String directorypath, String filename, File parent, byte[] content, int maxBytes, boolean allowNull) throws ESAPIIntrusionException {
		return( isValidFileName( context, filename, allowNull ) &&
				isValidDirectoryPath( context, directorypath, parent, allowNull ) &&
				isValidFileContent( context, content, maxBytes, allowNull ) );
	}

        /**
	 * {@inheritDoc}
	 *
	 * <p><b>Note:</b> On platforms that support symlinks, this function will fail canonicalization if directorypath
	 * is a symlink. For example, on MacOS X, /etc is actually /private/etc. If you mean to use /etc, use its real
	 * path (/private/etc), not the symlink (/etc).</p>
     */
	public boolean isValidFileUpload(String context, String directorypath, String filename, File parent, byte[] content, int maxBytes, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		return( isValidFileName( context, filename, allowNull, errors ) &&
				isValidDirectoryPath( context, directorypath, parent, allowNull, errors ) &&
				isValidFileContent( context, content, maxBytes, allowNull, errors ) );
	}

	/**
	 * {@inheritDoc}
	 */
	public void assertValidFileUpload(String context, String directorypath, String filename, File parent, byte[] content, int maxBytes, List<String> allowedExtensions, boolean allowNull) throws ESAPIValidationException, ESAPIIntrusionException {
		getValidFileName( context, filename, allowedExtensions, allowNull );
		getValidDirectoryPath( context, directorypath, parent, allowNull );
		getValidFileContent( context, content, maxBytes, allowNull );
	}

	/**
	 * {@inheritDoc}
	 */
	public void assertValidFileUpload(String context, String filepath, String filename, File parent, byte[] content, int maxBytes, List<String> allowedExtensions, boolean allowNull, ESAPIValidationErrorList errors)
		throws ESAPIIntrusionException {
		try {
			assertValidFileUpload(context, filepath, filename, parent, content, maxBytes, allowedExtensions, allowNull);
		} catch (ESAPIValidationException e) {
			//errors.addError(context, e);
		}
	}

	 /**
	 * {@inheritDoc}
	 *
	 * Returns true if input is a valid list item.
	 */
	public boolean isValidListItem(String context, String input, List<String> list) {
		try {
			getValidListItem( context, input, list);
			return true;
		} catch( Exception e ) {
			return false;
		}
	}

        /**
	 * {@inheritDoc}
	 *
	 * Returns true if input is a valid list item.
	 */
	public boolean isValidListItem(String context, String input, List<String> list, ESAPIValidationErrorList errors) {
		try {
			getValidListItem( context, input, list);
			return true;
		} catch( ESAPIValidationException e ) {
            //errors.addError(context, e);
			return false;
		}
	}

	/**
	 * Returns the list item that exactly matches the canonicalized input. Invalid or non-matching input
	 * will generate a descriptive ValidationException, and input that is clearly an attack
	 * will generate a descriptive ESAPIIntrusionException.
	 */
	public String getValidListItem(String context, String input, List<String> list) throws ESAPIValidationException {
		if (list.contains(input)) return input;
		throw new ESAPIValidationException( context + ": Invalid list item", "Invalid list item: context=" + context + ", input=" + input, context );
	}


	/**
	 * ESAPIValidationErrorList variant of getValidListItem
     *
     * @param errors
     */
	public String getValidListItem(String context, String input, List<String> list, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			return getValidListItem(context, input, list);
		} catch (ESAPIValidationException e) {
			//errors.addError(context, e);
		}
		// error has been added to list, so return original input
		return input;
	}

	 /**
	 * {@inheritDoc}
     */
	public boolean isValidHTTPRequestParameterSet(String context, HttpServletRequest request, Set<String> requiredNames, Set<String> optionalNames) {
		try {
			assertValidHTTPRequestParameterSet( context, request, requiredNames, optionalNames);
			return true;
		} catch( Exception e ) {
			return false;
		}
	}

         /**
	 * {@inheritDoc}
     */
	public boolean isValidHTTPRequestParameterSet(String context, HttpServletRequest request, Set<String> requiredNames, Set<String> optionalNames, ESAPIValidationErrorList errors) {
		try {
			assertValidHTTPRequestParameterSet( context, request, requiredNames, optionalNames);
			return true;
		} catch( ESAPIValidationException e ) {
            //errors.addError(context, e);
			return false;
		}
	}

	/**
	 * Validates that the parameters in the current request contain all required parameters and only optional ones in
	 * addition. Invalid input will generate a descriptive ValidationException, and input that is clearly an attack
	 * will generate a descriptive ESAPIIntrusionException.
	 *
	 * Uses current HTTPRequest
	 */
	public void assertValidHTTPRequestParameterSet(String context, HttpServletRequest request, Set<String> required, Set<String> optional) throws ESAPIValidationException {
		Set<String> actualNames = request.getParameterMap().keySet();

		// verify ALL required parameters are present
		Set<String> missing = new HashSet<String>(required);
		missing.removeAll(actualNames);
		if (missing.size() > 0) {
			throw new ESAPIValidationException( context + ": Invalid HTTP request missing parameters", "Invalid HTTP request missing parameters " + missing + ": context=" + context, context );
		}

		// verify ONLY optional + required parameters are present
		Set<String> extra = new HashSet<String>(actualNames);
		extra.removeAll(required);
		extra.removeAll(optional);
		if (extra.size() > 0) {
			throw new ESAPIValidationException( context + ": Invalid HTTP request extra parameters " + extra, "Invalid HTTP request extra parameters " + extra + ": context=" + context, context );
		}
	}

	/**
	 * ESAPIValidationErrorList variant of assertIsValidHTTPRequestParameterSet
     *
	 * Uses current HTTPRequest saved in ESAPI Authenticator
     * @param errors
     */
	public void assertValidHTTPRequestParameterSet(String context, HttpServletRequest request, Set<String> required, Set<String> optional, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			assertValidHTTPRequestParameterSet(context, request, required, optional);
		} catch (ESAPIValidationException e) {
			//errors.addError(context, e);
		}
	}

	/**
     * {@inheritDoc}
     *
	 * Checks that all bytes are valid ASCII characters (between 33 and 126
	 * inclusive). This implementation does no decoding. http://en.wikipedia.org/wiki/ASCII.
	 */
	public boolean isValidPrintable(String context, char[] input, int maxLength, boolean allowNull) throws ESAPIIntrusionException {
		try {
			getValidPrintable( context, input, maxLength, allowNull);
			return true;
		} catch( Exception e ) {
			return false;
		}
	}

        /**
     * {@inheritDoc}
     *
	 * Checks that all bytes are valid ASCII characters (between 33 and 126
	 * inclusive). This implementation does no decoding. http://en.wikipedia.org/wiki/ASCII.
	 */
	public boolean isValidPrintable(String context, char[] input, int maxLength, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			getValidPrintable( context, input, maxLength, allowNull);
			return true;
		} catch( ESAPIValidationException e ) {
            //errors.addError(context, e);
			return false;
		}
	}

	/**
	 * Returns canonicalized and validated printable characters as a byte array. Invalid input will generate a descriptive ValidationException, and input that is clearly an attack
	 * will generate a descriptive ESAPIIntrusionException.
     *
     * @throws ESAPIIntrusionException
     */
	public char[] getValidPrintable(String context, char[] input, int maxLength, boolean allowNull) throws ESAPIValidationException, ESAPIIntrusionException {
		if (isEmpty(input)) {
			if (allowNull) return null;
   			throw new ESAPIValidationException(context + ": Input bytes required", "Input bytes required: HTTP request is null", context );
		}

		if (input.length > maxLength) {
			throw new ESAPIValidationException(context + ": Input bytes can not exceed " + maxLength + " bytes", "Input exceeds maximum allowed length of " + maxLength + " by " + (input.length-maxLength) + " bytes: context=" + context + ", input=" + new String( input ), context);
		}

		for (int i = 0; i < input.length; i++) {
			if (input[i] <= 0x20 || input[i] >= 0x7E ) {
				throw new ESAPIValidationException(context + ": Invalid input bytes: context=" + context, "Invalid non-ASCII input bytes, context=" + context + ", input=" + new String( input ), context);
			}
		}
		return input;
	}

	/**
	 * ESAPIValidationErrorList variant of getValidPrintable
     *
     * @param errors
     */
	public char[] getValidPrintable(String context, char[] input,int maxLength, boolean allowNull, ESAPIValidationErrorList errors)
		throws ESAPIIntrusionException {

		try {
			return getValidPrintable(context, input, maxLength, allowNull);
		} catch (ESAPIValidationException e) {
			//errors.addError(context, e);
		}
		// error has been added to list, so return original input
		return input;
	}


	 /**
	 * {@inheritDoc}
	 *
	 * Returns true if input is valid printable ASCII characters (32-126).
	 */
	public boolean isValidPrintable(String context, String input, int maxLength, boolean allowNull) throws ESAPIIntrusionException {
		try {
			getValidPrintable( context, input, maxLength, allowNull);
			return true;
		} catch( Exception e ) {
			return false;
		}
	}

        /**
	 * {@inheritDoc}
	 *
	 * Returns true if input is valid printable ASCII characters (32-126).
	 */
	public boolean isValidPrintable(String context, String input, int maxLength, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			getValidPrintable( context, input, maxLength, allowNull);
			return true;
		} catch( ESAPIValidationException e ) {
            //errors.addError(context, e);
			return false;
		}
	}

	/**
	 * Returns canonicalized and validated printable characters as a String. Invalid input will generate a descriptive ValidationException, and input that is clearly an attack
	 * will generate a descriptive ESAPIIntrusionException.
     *
     * @throws ESAPIIntrusionException
     */
	public String getValidPrintable(String context, String input, int maxLength, boolean allowNull) throws ESAPIValidationException, ESAPIIntrusionException {
		try {
    		String canonical = encoder.canonicalize(input);
    		return new String( getValidPrintable( context, canonical.toCharArray(), maxLength, allowNull) );
	    //TODO - changed this to base Exception since we no longer need EncodingException
    	//TODO - this is a bit lame: we need to re-think this function.
		} catch (Exception e) {
	        throw new ESAPIValidationException( context + ": Invalid printable input", "Invalid encoding of printable input, context=" + context + ", input=" + input, e, context);
	    }
	}

	/**
	 * ESAPIValidationErrorList variant of getValidPrintable
     *
     * @param errors
     */
	public String getValidPrintable(String context, String input,int maxLength, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			return getValidPrintable(context, input, maxLength, allowNull);
		} catch (ESAPIValidationException e) {
			//errors.addError(context, e);
		}
		// error has been added to list, so return original input
		return input;
	}


	/**
	 * Returns true if input is a valid redirect location.
	 */
	public boolean isValidRedirectLocation(String context, String input, boolean allowNull) {
		return isValidInput( context, input, "Redirect", 512, allowNull);
	}

        /**
	 * Returns true if input is a valid redirect location.
	 */
	public boolean isValidRedirectLocation(String context, String input, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		return isValidInput( context, input, "Redirect", 512, allowNull, errors);
	}


	/**
	 * Returns a canonicalized and validated redirect location as a String. Invalid input will generate a descriptive ValidationException, and input that is clearly an attack
	 * will generate a descriptive ESAPIIntrusionException.
	 */
	public String getValidRedirectLocation(String context, String input, boolean allowNull) throws ESAPIValidationException, ESAPIIntrusionException {
		return getValidInput( context, input, "Redirect", 512, allowNull);
	}

	/**
	 * ESAPIValidationErrorList variant of getValidRedirectLocation
     *
     * @param errors
     */
	public String getValidRedirectLocation(String context, String input, boolean allowNull, ESAPIValidationErrorList errors) throws ESAPIIntrusionException {
		try {
			return getValidRedirectLocation(context, input, allowNull);
		} catch (ESAPIValidationException e) {
			//errors.addError(context, e);
		}
		// error has been added to list, so return original input
		return input;
	}

	/**
     * {@inheritDoc}
     *
	 * This implementation reads until a newline or the specified number of
	 * characters.
     *
     * @param in
     * @param max
     */
	public String safeReadLine(InputStream in, int max) throws ESAPIValidationException {
		if (max <= 0) {
			throw new ESAPIValidationException( "Invalid input", "Invalid readline. Must read a positive number of bytes from the stream");
		}

		StringBuilder sb = new StringBuilder();
		int count = 0;
		int c;

		try {
			while (true) {
				c = in.read();
				if ( c == -1 ) {
					if (sb.length() == 0) {
						return null;
					}
					break;
				}
				if (c == '\n' || c == '\r') {
					break;
				}
				count++;
				if (count > max) {
					throw new ESAPIValidationException( "Invalid input", "Invalid readLine. Read more than maximum characters allowed (" + max + ")");
				}
				sb.append((char) c);
			}
			return sb.toString();
		} catch (IOException e) {
			throw new ESAPIValidationException( "Invalid input", "Invalid readLine. Problem reading from input stream", e);
		}
	}

	/**
	 * Helper function to check if a String is empty
	 *
	 * @param input string input value
	 * @return boolean response if input is empty or not
	 */
	private final boolean isEmpty(String input) {
		return (input==null || input.trim().length() == 0);
	}

	/**
	 * Helper function to check if a byte array is empty
	 *
	 * @param input string input value
	 * @return boolean response if input is empty or not
	 */
	private final boolean isEmpty(byte[] input) {
		return (input==null || input.length == 0);
	}


	/**
	 * Helper function to check if a char array is empty
	 *
	 * @param input string input value
	 * @return boolean response if input is empty or not
	 */
	private final boolean isEmpty(char[] input) {
		return (input==null || input.length == 0);
	}
}
