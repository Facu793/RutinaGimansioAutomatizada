package com.gestionrutina.auth.controller;

import com.gestionrutina.auth.JwtService;
import com.gestionrutina.auth.dto.LoginRequest;
import com.gestionrutina.auth.dto.LoginResponse;
import com.gestionrutina.auth.entity.Usuario;
import com.gestionrutina.auth.repository.UsuarioRepository;
import com.gestionrutina.common.exception.PeticionInvalidaException;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username().trim(), request.password())
            );
        } catch (AuthenticationException exception) {
            throw new PeticionInvalidaException("Usuario o contraseña incorrectos");
        }
        Usuario usuario = usuarioRepository.findByUsername(request.username().trim())
                .orElseThrow(() -> new PeticionInvalidaException("Usuario o contraseña incorrectos"));
        return new LoginResponse(jwtService.generar(usuario.getUsername(), usuario.getRol()), usuario.getUsername(), usuario.getRol());
    }
}
