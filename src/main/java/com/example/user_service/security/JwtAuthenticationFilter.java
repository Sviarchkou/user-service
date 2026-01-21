package com.example.user_service.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);
            UUID id = jwtService.extractId(token);
            if (id == null)
                throw new BadCredentialsException("UserID is null");

            List<String> string_roles = jwtService.extractRoles(token);
            List<GrantedAuthority> roles = string_roles.stream().map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            if (roles.isEmpty())
                throw new BadCredentialsException("No role is presented");

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    id,
                    null,
                    roles
            );
            SecurityContext context = SecurityContextHolder.getContext();
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            handleJwtException(response, HttpStatus.FORBIDDEN, "Token expired");
        } catch (JwtException | BadCredentialsException e) {
            handleJwtException(response, HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    private void handleJwtException(HttpServletResponse response, HttpStatus status, String message)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");
        String body = """
        {
            "status": %d,
            "error": "%s"
        }
        """.formatted(status.value(), message);
        response.getWriter().write(body);
    }

}
