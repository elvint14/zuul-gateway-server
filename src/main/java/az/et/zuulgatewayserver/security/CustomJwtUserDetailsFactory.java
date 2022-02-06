package az.et.zuulgatewayserver.security;

import az.et.zuulgatewayserver.model.RoleEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomJwtUserDetailsFactory {

    public static JwtUserDetails of(Long id, String username, String password, Collection<RoleEntity> roles) {
        return JwtUserDetails.builder()
                .id(id)
                .username(username)
                .password(password)
                .authorities(
                        roles.stream()
                                .map(RoleEntity::getName)
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                )
                .build();
    }

    public static JwtUserDetails create(String username, String password, List<String> roles) {
        return JwtUserDetails.builder()
                .username(username)
                .password(password)
                .authorities(
                        roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                ).build();
    }

    public static JwtUserDetails create(String username, List<String> roles) {
        return JwtUserDetails.builder()
                .username(username)
                .authorities(
                        roles.stream()
                                .map(SimpleGrantedAuthority::new)
                                .collect(Collectors.toList())
                ).build();
    }
}
