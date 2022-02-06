package az.et.zuulgatewayserver.security;

import az.et.zuulgatewayserver.model.UserEntity;
import az.et.zuulgatewayserver.repository.UserRepository;
import az.et.zuulgatewayserver.repository.UserTokensRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static az.et.zuulgatewayserver.constant.AuthHeader.*;

@Component
@RequiredArgsConstructor
public class AuthFilter extends ZuulFilter {
    private final ObjectMapper objectMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;
    private final UserTokensRepository userTokensRepository;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        final RequestContext currentContext = RequestContext.getCurrentContext();
        final String jwt = parseJwt(currentContext.getRequest());
        System.out.println("JWT is -> " + jwt);
        try {
            if (jwt != null && jwtTokenUtil.validateToken(jwt)) {
                String usernameFromToken = jwtTokenUtil.getUsernameFromToken(jwt);
                System.out.println(usernameFromToken);
                userRepository.findByUsername(usernameFromToken).ifPresent(userEntity -> {
                    System.out.println(userEntity);
                    if (checkAccessTokenIsExist(jwt, userEntity)) {
                        String xUser = null;
                        try {
                            xUser = objectMapper.writeValueAsString(
                                    JwtUserDetails.of(
                                            userEntity.getId(),
                                            userEntity.getUsername(),
                                            userEntity.getPassword(),
                                            userEntity.getRoles()
                                    )
                            );
                            System.out.println("User Detals -> " + xUser);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        currentContext.addZuulRequestHeader(
                                X_USER,
                                xUser
                        );
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String parseJwt(HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTHORIZATION);
        System.out.println(authHeader);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER)) return authHeader.substring(7);
        return null;
    }

    private boolean checkAccessTokenIsExist(String token, UserEntity user) {
//        return userTokensRepository.findAllByUserId(user.getId())
//                .stream()
//                .anyMatch(userLogin ->
//                        userLogin.getAccessToken().equals(DigestUtils.md5DigestAsHex(token.getBytes()))
//                );
        return userTokensRepository.existsUserTokensEntityByAccessTokenAndUser(
                DigestUtils.md5DigestAsHex(token.getBytes()),
                user
        );
    }

}
