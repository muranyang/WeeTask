package Security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

public class AuthenticationListener implements ApplicationListener<ApplicationEvent> {

    // The log
    private static final Log log = LogFactory.getLog(AuthenticationListener.class);
	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		
		if(event instanceof AuthenticationSuccessEvent)
		{
			String userName = ((AuthenticationSuccessEvent) event).getAuthentication().getName();			
			log.info(userName + " has successfully logged in.");
		}
		if(event instanceof AuthenticationFailureBadCredentialsEvent)
		{
			String userName = ((AuthenticationFailureBadCredentialsEvent) event).getAuthentication().getName();			
			log.info(userName + " was unable to log in due to bad credentials.");
		}
		if(event instanceof AuthenticationFailureDisabledEvent)
		{
			String userName = ((AuthenticationFailureDisabledEvent) event).getAuthentication().getName();			
			log.info(userName + " was unable to log in because the user is disabled.");		
		}
	}

}
