package az.et.zuulgatewayserver.security;

import az.et.zuulgatewayserver.constant.ErrorEnum;
import az.et.zuulgatewayserver.exception.BaseException;
import az.et.zuulgatewayserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> JwtUserDetails.of(
                                user.getId(),
                                user.getUsername(),
                                user.getPassword(),
                                user.getRoles()
                        )
                ).orElseThrow(
                        () -> BaseException.of(ErrorEnum.USERNAME_NOT_FOUND)
                );
    }
}