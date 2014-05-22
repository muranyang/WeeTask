/*
 * Needed for i18n of Spring Security Messages
 * 
 * */
package Security.esapi;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import Security.esapi.filter.ESAPISecurityWrapperRequest;
import Security.esapi.filter.ESAPISecurityWrapperResponse;

public class ESAPIFilter extends OncePerRequestFilter implements Filter {

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        chain.doFilter(new ESAPISecurityWrapperRequest(request), new ESAPISecurityWrapperResponse(response));
    }

}
