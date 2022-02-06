package az.et.zuulgatewayserver.security;

import az.et.zuulgatewayserver.constant.ErrorEnum;
import az.et.zuulgatewayserver.exception.BaseException;
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
    public Object run()  {
        final RequestContext currentContext = RequestContext.getCurrentContext();
        final String jwt = parseJwt(currentContext.getRequest());
        System.out.println(jwt);
        try {
            if (jwt != null && jwtTokenUtil.validateToken(jwt)) {
                userRepository.findByUsername(jwtTokenUtil.getUsernameFromToken(jwt))
                        .ifPresent(userFromJwt -> {
                            System.out.println(userFromJwt);
                            if (checkAccessTokenIsExist(jwt, userFromJwt)) {
                                String xUser = null;
                                try {
                                    xUser = objectMapper.writeValueAsString(
                                            CustomJwtUserDetailsFactory.of(
                                                    userFromJwt.getId(),
                                                    userFromJwt.getUsername(),
                                                    userFromJwt.getPassword(),
                                                    userFromJwt.getRoles()
                                            )
                                    );
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                                System.out.println(xUser);
                                currentContext.addZuulRequestHeader(
                                        X_USER,
                                        xUser
                                );
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw BaseException.of(ErrorEnum.AUTH_ERROR);
        }
        return null;
    }

    private String parseJwt(HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTHORIZATION);
        System.out.println(authHeader);
        if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER)) {
            return authHeader.substring(7);
        }
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
