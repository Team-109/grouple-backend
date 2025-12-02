package com.example.grouple.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import com.example.grouple.security.AuthPrincipal;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity()
@AllArgsConstructor
public class SecurityConfig {

    private final SecretKey jwtKey;
    private final UserDetailsService uds;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider(PasswordEncoder enc) {
        var p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(enc);
        return p;
    }

    @Bean
    public SecurityFilterChain filterChain(org.springframework.security.config.annotation.web.builders.HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        RequestMatcher[] PUBLIC = getRequestMatchers(introspector);
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC).permitAll()   // swagger, /auth/login, /auth/register 만
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"status\":\"error\",\"message\":\"Unauthorized\"}");
                        })
                        .accessDeniedHandler((req, res, e) -> {
                            res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            res.setContentType("application/json");
                            res.getWriter().write("{\"status\":\"error\",\"message\":\"Forbidden\"}");
                        })
                )
                .addFilterBefore(new JwtAuthFilter(jwtKey, PUBLIC), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    private static RequestMatcher[] getRequestMatchers(HandlerMappingIntrospector introspector) {
        var mvc = new MvcRequestMatcher.Builder(introspector);
        return new RequestMatcher[] {
                mvc.pattern("/swagger-ui/**"),
                mvc.pattern("/v3/api-docs/**"),
                mvc.pattern("/webjars/**"),
                mvc.pattern("/auth/login"),
                mvc.pattern("/auth/register"),
                mvc.pattern("/auth/refresh"),
                mvc.pattern("/auth/check_id")
        };
    }

    static class JwtAuthFilter extends OncePerRequestFilter {

        private final JwtParser parser;
        private final RequestMatcher skip; //

        JwtAuthFilter(SecretKey key, RequestMatcher... skipMatchers) {
            this.parser = Jwts.parserBuilder().setSigningKey(key).build();
            if (skipMatchers == null || skipMatchers.length == 0) {
                this.skip = request -> false; // never matches; do not throw
            } else {
                this.skip = new OrRequestMatcher(Arrays.asList(skipMatchers));
            }
        }

        @Override
        protected boolean shouldNotFilter(HttpServletRequest req) {
            if ("OPTIONS".equalsIgnoreCase(req.getMethod())) return true; // CORS preflight
            return skip.matches(req);
        }

        @Override
        protected void doFilterInternal(@NotNull HttpServletRequest req, @NotNull HttpServletResponse res, @NotNull FilterChain chain)
                throws ServletException, IOException {

            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                chain.doFilter(req, res);
                return;
            }

            String h = req.getHeader("Authorization");
            if (h != null && h.startsWith("Bearer ")) {
                String token = h.substring(7);
                try {
                    var jws = parser.parseClaimsJws(token);
                    var claims = jws.getBody();

                    String subject = claims.getSubject();              // username
                    Integer uid = claims.get("id", Integer.class);     // 선택: 사용자 ID 클레임
                    // 권한이 필요하면 List<String> roles = claims.get("roles", List.class);
                    var auth = new UsernamePasswordAuthenticationToken(
                            new AuthPrincipal(uid, subject),
                            null,
                            List.of() // 권한 매핑 시 채움
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (JwtException e) {
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.setHeader("WWW-Authenticate", "Bearer error=\"invalid_token\"");
                    res.setContentType("application/json");
                    res.getWriter().write("{\"status\":\"error\",\"message\":\"Invalid or expired token\"}");
                    return;
                }
            }

            chain.doFilter(req, res);
        }
    }
}
