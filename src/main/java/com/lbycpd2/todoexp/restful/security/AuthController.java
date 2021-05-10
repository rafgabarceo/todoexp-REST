package com.lbycpd2.todoexp.restful.security;

import com.lbycpd2.todoexp.restful.security.jwt.AuthenticationRequest;
import com.lbycpd2.todoexp.restful.security.jwt.AuthenticationResponse;
import com.lbycpd2.todoexp.restful.security.util.JWTUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JWTUtil jwtUtil;

    @PostMapping(path = "/auth")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authRequest,
                                                       HttpServletResponse response) throws Exception {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(),
                    authRequest.getPassword()));
        } catch (Exception e){
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        Cookie jwtCookie = new Cookie("auth", jwt);
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));

    }
}