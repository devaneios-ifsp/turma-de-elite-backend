package com.devaneios.turmadeelite.configuration;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class SecurityFilters implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Strict-Transport-Security","max-age=631138519");
        httpServletResponse.setHeader("Content-Security-Policy","default-src 'self';" +
                "img-src data: https:;" +
                "object-src 'none';" +
                " script-src https://stackpath.bootstrapcdn.com/ 'self' 'unsafe-inline';" +
                "style-src https://stackpath.bootstrapcdn.com/ 'self' 'unsafe-inline';" +
                " upgrade-insecure-requests;");
        httpServletResponse.setHeader("X-Content-Type-Options","nosniff");
        httpServletResponse.setHeader("X-Frame-Options","SAMEORIGIN");
        httpServletResponse.setHeader("Referrer-Policy","same-origin");
        chain.doFilter(request,response);
    }
}
