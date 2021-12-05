package com.devaneios.turmadeelite.external.classroom.authentication;
import com.devaneios.turmadeelite.security.guards.IsManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;
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
        return this.googleOauth2Service.classroomAuth(managerAuthUuid);
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
    public RedirectView classroomCallback(HttpServletRequest request, @RequestParam String state) throws IOException{
        String url = request.getRequestURL() + "?" + request.getQueryString();
        this.googleOauth2Service.classroomCallback(url,state);
        return new RedirectView("http://localhost:4200?authenticationSuccess=true");
    }

}