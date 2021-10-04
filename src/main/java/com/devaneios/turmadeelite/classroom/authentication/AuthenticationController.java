package com.devaneios.turmadeelite.classroom.authentication;

import com.devaneios.turmadeelite.security.guards.IsManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@AllArgsConstructor
public class AuthenticationController {

    private final GoogleOauth2Service googleOauth2Service;
    private final String COOKIE_NAME = "authUuid";

    @Operation(summary = "Recupera o URL de authenticação do Google Oauth2")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "URL Recuperada com sucesso"
            )
    })
    @IsManager
    @GetMapping("/api/classroom/auth")
    public String getOauth2AuthenticationLink(Authentication authentication, HttpServletResponse response) throws IOException {
        String managerAuthUuid = (String) authentication.getPrincipal();
        Cookie cookie = new Cookie(COOKIE_NAME,managerAuthUuid);
        cookie.setMaxAge(10 * 60 * 60); // Ten minutes
        cookie.setPath("/Callback");
        response.addCookie(cookie);
        return this.googleOauth2Service.classroomAuth();
    }

    @Operation(summary = "Encerra o fluxo validando o código recebido pelo cliente")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticação realizada com sucesso"
            )
    })
    @IsManager
    @GetMapping("/Callback")
    public void classroomCallback(HttpServletRequest request, @CookieValue(name = COOKIE_NAME, defaultValue = "_") String authUuid) throws IOException{
        String url = request.getRequestURL() + "?" + request.getQueryString();
        if(authUuid.equals("_")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        this.googleOauth2Service.classroomCallback(url,authUuid);
    }

}
