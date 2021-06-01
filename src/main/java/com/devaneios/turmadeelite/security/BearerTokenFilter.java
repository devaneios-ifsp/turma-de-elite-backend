package com.devaneios.turmadeelite.security;

import com.devaneios.turmadeelite.exceptions.BearerTokenNotFoundException;
import com.devaneios.turmadeelite.exceptions.UnexpectedAuthenticationException;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BearerTokenFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    FirebaseApp firebaseApp;

    public BearerTokenFilter(){
        super("/**");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String authorizationHeader = request.getHeader("Authorization");
        if(authorizationHeader == null){
            throw new BearerTokenNotFoundException();
        }else{
            try{
                String bearerToken = authorizationHeader.split(" ")[1];
                if(bearerToken != null){
                    FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(bearerToken);
                    FirebaseAuthenticationInfo authenticationToken = new FirebaseAuthenticationInfo(firebaseToken.getUid(), firebaseToken.getEmail(), true);
                    return getAuthenticationManager().authenticate(authenticationToken);
                }
            }catch (Exception e){
                throw new UnexpectedAuthenticationException();
            }

        }
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
            throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}
