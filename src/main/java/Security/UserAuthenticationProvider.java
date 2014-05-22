package Security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class UserAuthenticationProvider extends DaoAuthenticationProvider
{
    // The log
    private static final Log log = LogFactory.getLog(AuthenticationListener.class);
	
	public static int MAX_ALLOWED_FAILED_ATTEMPTS = 3;

    /**
     * Method runs method in super class to check password and then performs any
     * additional login rules.
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException
    {
        try
        {
            super.additionalAuthenticationChecks(userDetails, authentication);

            if (userDetails instanceof UserBean)
            {
                UserBean userBean = (UserBean) userDetails;
                UserBeanManager manager = (UserBeanManager) getUserDetailsService();

                if (userBean.getFailAttempts() > 0)
                {
                    userBean.setFailAttempts(0);
                    manager.updateFailedAttempts(userBean);
                }
            }
        }
        catch (AuthenticationException ae)
        {
            // Increment Failed Login Attempts, or anything else that needs to
            // be done.
            //
            if (userDetails instanceof UserBean && ae instanceof BadCredentialsException)
            {
                UserBean userBean = (UserBean) userDetails;
                UserBeanManager manager = (UserBeanManager) getUserDetailsService();

                if (userBean.getFailAttempts() >= MAX_ALLOWED_FAILED_ATTEMPTS-1)
                {
                    manager.updateAccountStatus(userBean, false);
                    log.info(userBean.getUsername() + " has been locked out.");
                    this.authenticate(authentication);
                }
                else
                {
                    userBean.setFailAttempts(userBean.getFailAttempts() + 1);
                    manager.updateFailedAttempts(userBean);
                }
            }

            throw ae;
        }
    }

    /**
     * Set the maximum amount of failed attempts allowed for an user account.
     * 
     * @param maxFailedAttempts
     *            The maximum amount of failed attempts.
     */
    public void setMaxFailedAttempts(int maxFailedAttempts)
    {
        MAX_ALLOWED_FAILED_ATTEMPTS = maxFailedAttempts;
    }
}
