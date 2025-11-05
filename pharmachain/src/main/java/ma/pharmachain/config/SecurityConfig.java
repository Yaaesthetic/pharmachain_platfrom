package ma.pharmachain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ADD THIS
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")  // Changed to plural
                        // Admin OR Manager can access manager endpoints
                        .requestMatchers("/api/managers/**").hasAnyRole("ADMIN", "MANAGER")

                        // Admin OR Manager can access driver endpoints
                        .requestMatchers("/api/drivers/**").hasAnyRole("ADMIN", "MANAGER", "DRIVER")
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }


    /**
     * Converts JWT claims to Spring Security authorities
     * Maps Keycloak roles to Spring Security roles with ROLE_ prefix
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setPrincipalClaimName("preferred_username"); // ADD THIS
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        return converter;
    }

    /**
     * Custom converter to extract roles from Keycloak JWT token
     */
    static class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        @Override
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");

            if (realmAccess == null || realmAccess.isEmpty()) {
                return List.of();
            }

            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");

            if (roles == null) {
                return List.of();
            }

            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());
        }
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://127.0.0.1:3000"
        ));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Total-Count"
        ));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}





























































//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity
//public class SecurityConfig {
//
////
////    @Value("${cors.allowed-origins}")
////    private String[] allowedOrigins;
////
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(authorize -> authorize
//                        // Admin endpoints - only ADMIN role
//                        .requestMatchers(HttpMethod.POST, "/api/managers").hasAnyRole("ADMIN", "MANAGER")
//                        .requestMatchers(HttpMethod.PUT, "/api/managers/**").hasAnyRole("ADMIN", "MANAGER")
//                        .requestMatchers(HttpMethod.DELETE, "/api/managers/**").hasAnyRole("ADMIN", "MANAGER")
//                        .requestMatchers(HttpMethod.GET, "/api/managers/**").hasAnyRole("ADMIN", "MANAGER")
//
//                        // Driver endpoints - ADMIN and MANAGER roles
//                        .requestMatchers(HttpMethod.POST, "/api/drivers").hasAnyRole("ADMIN", "MANAGER")
//                        .requestMatchers(HttpMethod.PUT, "/api/drivers/**").hasAnyRole("ADMIN", "MANAGER")
//                        .requestMatchers(HttpMethod.DELETE, "/api/drivers/**").hasAnyRole("ADMIN", "MANAGER")
//                        .requestMatchers(HttpMethod.GET, "/api/drivers/**").hasAnyRole("ADMIN", "MANAGER", "USER")
//
//                        .anyRequest().authenticated()
//                )
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter()))
//                );
//
//        return http.build();
//    }
//
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        // Allow specific origins (your frontend URLs)
//        configuration.setAllowedOrigins(Arrays.asList(
////                allowedOrigins
//                "http://localhost:3000",     // Web frontend (Angular/React/Vue)
//                "capacitor://localhost",     // Capacitor mobile apps
//                "ionic://localhost"          // Ionic mobile apps
//        ));
//
//        // Allow specific HTTP methods
//        configuration.setAllowedMethods(Arrays.asList(
//                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
//        ));
//
//        // Allow all headers (including Authorization)
//        configuration.setAllowedHeaders(List.of("*"));
//
//        // Allow credentials (cookies, authorization headers)
//        configuration.setAllowCredentials(true);
//
//        // How long the browser can cache preflight responses (in seconds)
//        configuration.setMaxAge(3600L);
//
//        // Expose headers that the frontend can access
//        configuration.setExposedHeaders(Arrays.asList(
//                "Authorization", "Content-Type"
//        ));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//}
//
