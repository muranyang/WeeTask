package Security;

import org.springframework.security.provisioning.UserDetailsManager;

public interface UserBeanManager extends UserDetailsManager
{
    /**
     * Update the user account status to the specified status.
     * 
     * @param user
     *            the current user account information.
     * @param isEnabled
     *            true if account should be enabled, otherwise false.
     */
    public void updateAccountStatus(UserBean user, boolean isEnabled);

    /**
     * Update the failed attempt counter for the provided user account.
     * 
     * @param user
     *            the user account to update
     */
    public void updateFailedAttempts(UserBean user);
}
