package sample.zuul;

import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CustomRouteLocator extends SimpleRouteLocator {
    
    private ZuulProperties zuulProperties;

    public CustomRouteLocator(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
        this.zuulProperties = properties;
    }

    //TODO: Should delegate to SimpeRouteLocator if this routelocator cannot handle the route.
    protected Route getRoute(ZuulProperties.ZuulRoute route, String path) {
        if (route == null) {
            return null;
        }

        String targetPath = path;
        String prefix = this.zuulProperties.getPrefix();

        if (route.isStripPrefix()) {
            targetPath = "/" + Arrays.stream(StringUtils.tokenizeToStringArray(path, "/"))
                    .skip(1).collect(Collectors.joining("/"));
        }
        
        Boolean retryable = this.zuulProperties.getRetryable();

        if (route.getRetryable() != null) {
            retryable = route.getRetryable();
        }
        return new Route(route.getId(), targetPath, route.getLocation(), prefix,
                retryable,
                route.isCustomSensitiveHeaders() ? route.getSensitiveHeaders() : null,
                route.isStripPrefix());
    }
}
