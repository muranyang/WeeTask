package Security;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

/**
 */
public class UserBean extends User
{
    private int               failAttempts;
    
    private Date			  creationDate;

    /**
     * Version the bean to 1.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Construct the <code>User</code> with the details required by
     * {@link org.springframework.security.authentication.dao.DaoAuthenticationProvider}
     * .
     * 
     * @param username
     *            the username presented to the
     *            <code>DaoAuthenticationProvider</code>
     * @param password
     *            the password that should be presented to the
     *            <code>DaoAuthenticationProvider</code>
     * @param enabled
     *            set to <code>true</code> if the user is enabled
     * @param accountNonExpired
     *            set to <code>true</code> if the account has not expired
     * @param credentialsNonExpired
     *            set to <code>true</code> if the credentials have not expired
     * @param accountNonLocked
     *            set to <code>true</code> if the account is not locked
     * @param authorities
     *            the authorities that should be granted to the caller if they
     *            presented the correct username and password and the user is
     *            enabled. Not null.
     * @param failedAttempts
     *            set the number of failed attempts for this user account
     * 
     * @throws IllegalArgumentException
     *             if a <code>null</code> value was passed either as a parameter
     *             or as an element in the <code>GrantedAuthority</code>
     *             collection
     */
    public UserBean(String username, String password, boolean enabled, boolean accountNonExpired,
                    boolean credentialsNonExpired, boolean accountNonLocked,
                    Collection<? extends GrantedAuthority> authorities, int failedAttempts, Date creationDate)

    {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired,
              accountNonLocked, authorities);
        failAttempts = failedAttempts;
        this.creationDate = creationDate;
    }

    /**
     * @return The current count of the number of failed attempts to login.
     */
    public int getFailAttempts()
    {
        return failAttempts;
    }

    /**
     * @param failAttempts
     *            The current count of the number of failed attempts to login.
     */
    public void setFailAttempts(int failAttempts)
    {
        this.failAttempts = failAttempts;
    }

	/**
	 * @return The date the user was created.
	 */
	public Date getCreationDate() {
		return creationDate;
	}
	
	/**
	 * @return The date (in a formatted string) the user was created.
	 */
	public String getFormattedCreationDate() {
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		return formatter.format(creationDate);
	}

	/**
	 * @param creationDate The date the user was created.
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
    
    
}
