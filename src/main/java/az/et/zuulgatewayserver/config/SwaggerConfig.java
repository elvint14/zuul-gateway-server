package az.et.zuulgatewayserver.config;

import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Primary
@Configuration
@EnableSwagger2
//@Profile("swagger")
public class SwaggerConfig implements SwaggerResourcesProvider {
    private final ZuulProperties zuulProperties;

    public SwaggerConfig(ZuulProperties zuulProperties) {
        this.zuulProperties = zuulProperties;
    }

    @Override
    public List<SwaggerResource> get() {
        final List<SwaggerResource> resources = new ArrayList<>();
        zuulProperties
                .getRoutes()
                .values()
                .forEach(zuulRoute -> {
                    final String link = zuulRoute.getPath().replace("**", "v2/api-docs");
                    if (!link.contains("/eureka-service")) {
                        resources.add(
                                swaggerResource(
                                        zuulRoute.getServiceId(),
                                        link
                                )
                        );
                    }
                });
        resources.sort(Comparator.comparing(SwaggerResource::getName));
        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        final SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("2.0");
        return swaggerResource;
    }

}