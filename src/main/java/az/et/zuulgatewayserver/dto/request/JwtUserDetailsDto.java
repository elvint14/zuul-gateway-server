package az.et.zuulgatewayserver.dto.request;

import az.et.zuulgatewayserver.model.RoleEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class JwtUserDetailsDto implements Serializable {
    private static final long serialVersionUID = 5926468583005150707L;

    private Long id;
    private String username;
    private String password;
    private List<String> authorities;

    public static JwtUserDetailsDto of(Long id, String username, String password, List<RoleEntity> roles) {
        return new JwtUserDetailsDto(
                id,
                username,
                password,
                mapRoles(roles)
        );
    }

    private static List<String> mapRoles(List<RoleEntity> roles) {
        return roles
                .stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toList());
    }

}
