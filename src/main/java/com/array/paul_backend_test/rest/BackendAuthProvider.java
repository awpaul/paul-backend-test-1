package com.array.paul_backend_test.rest;

import com.array.paul_backend_test.db.User;
import com.array.paul_backend_test.db.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class BackendAuthProvider implements AuthenticationProvider {
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private UserRepository userRepo;


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = request.getHeader("app_uid");
        String creds = request.getHeader("creds");
        if (authenticateUser(name,creds)) {
            //could implement different roles and all them access to various rest services
            return new UsernamePasswordAuthenticationToken(name,creds,null);
        }
        return null;
    }
    public boolean authenticateUser(String name,String creds) {
        boolean authorized = false;
        if (name==null||creds==null)
            return authorized;

        User dbUser = userRepo.findByEmail(name);
        if (dbUser!=null && creds.equals(dbUser.getPassword())) {
            authorized=true;
        }
        return authorized;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
