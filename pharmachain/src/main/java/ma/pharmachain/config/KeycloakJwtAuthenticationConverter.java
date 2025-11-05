//package ma.pharmachain.config;
//
//import org.springframework.core.convert.converter.Converter;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
//import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
//import java.util.Collection;
//import java.util.Map;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {
//
//    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//
//    @Override
//    public JwtAuthenticationToken convert(Jwt jwt) {
//        Collection<GrantedAuthority> authorities = Stream.concat(
//                defaultGrantedAuthoritiesConverter.convert(jwt).stream(),
//                extractResourceRoles(jwt).stream()
//        ).collect(Collectors.toSet());
//
//        return new JwtAuthenticationToken(jwt, authorities);
//    }
//
//    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
//        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
//        Collection<String> roles;
//
//        if (realmAccess != null && realmAccess.containsKey("roles")) {
//            roles = (Collection<String>) realmAccess.get("roles");
//            return roles.stream()
//                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
//                    .collect(Collectors.toSet());
//        }
//        return java.util.Collections.emptySet();
//    }
//}
//
