package com.guilhermenogueira.jwt.filter;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guilhermenogueira.jwt.service.TokenAuthenticationService;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

    public JWTLoginFilter(String url, AuthenticationManager authManager) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException, IOException, ServletException {
       
    	UserCredentials creds = new UserCredentials();
    	
    	try {
    		//Bind para UserCredentials do Header "authorization: Basic YWRtaW46cGFzc3dvcmQ="
    		creds = getUserCredentialsByBasicAuthorization(req);
		} catch (Exception e) {
			//Bind para UserCredentials do Body "{"username":"usr","password":"s3cret"}"
			creds = new ObjectMapper().readValue(req.getInputStream(), UserCredentials.class);
		}
        
        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        creds.getUsername(),
                        creds.getPassword(),
                        Collections.<GrantedAuthority>emptyList()
                )
        );
    }
    
    private static UserCredentials getUserCredentialsByBasicAuthorization(HttpServletRequest req) {
    	String authorization = req.getHeader("authorization");
		String decoded = new String(Base64.getDecoder().decode(authorization.replace("Basic ", "")));
		String[] userDetails = decoded.split(":", 2);
		
		UserCredentials userCredentials = new UserCredentials();
		userCredentials.setUsername(userDetails[0]);
		userCredentials.setPassword(userDetails[1]);
		
		return userCredentials;
    }


    @Override
    protected void successfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse res, FilterChain chain,
            Authentication auth
    ) throws IOException, ServletException {
        TokenAuthenticationService.addAuthentication(res, auth.getName());
    }
}