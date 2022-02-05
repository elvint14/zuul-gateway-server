package az.et.zuulgatewayserver.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AuthFilter extends ZuulFilter {
    private final RestTemplate restTemplate;
    private static final String X_USER = "X-User";
    private static final String USER_VALIDATION_URL = "http://localhost:8085/api/v1/auth/validate";

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
    @SneakyThrows
    public Object run() {
        RequestContext
                .getCurrentContext()
                .addZuulRequestHeader(
                        X_USER,
                        restTemplate.getForObject(
                                USER_VALIDATION_URL,
                                String.class
                        )
                );
        return null;
    }

}
