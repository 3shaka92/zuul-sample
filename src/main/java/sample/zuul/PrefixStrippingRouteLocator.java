package sample.zuul;

import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PrefixStrippingRouteLocator extends SimpleRouteLocator {

    private ZuulProperties zuulProperties;

    public PrefixStrippingRouteLocator(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
        this.zuulProperties = properties;
    }

    //Delegates to parent if the id of the ZuulRoute is not "*-regex*"
    protected Route getRoute(ZuulProperties.ZuulRoute route, String path) {
        if (route == null) {
            return null;
        }
        if (delegateToParent(route)) {
            return super.getRoute(route, path);
        }

        String[] tokenizedPaths = StringUtils.tokenizeToStringArray(path, "/");

        Boolean retryable = this.zuulProperties.getRetryable();

        if (route.getRetryable() != null) {
            retryable = route.getRetryable();
        }

        if (tokenizedPaths.length > 0) {
            String targetPath = "/" + Arrays.stream(tokenizedPaths)
                    .skip(1).collect(Collectors.joining("/"));
            String targetPrefix = tokenizedPaths[0];

            return new Route(route.getId(), targetPath, route.getLocation(), targetPrefix,
                    retryable,
                    route.isCustomSensitiveHeaders() ? route.getSensitiveHeaders() : null,
                    route.isStripPrefix());
        } else {
            return new Route(route.getId(), path, route.getLocation(), "",
                    retryable,
                    route.isCustomSensitiveHeaders() ? route.getSensitiveHeaders() : null,
                    route.isStripPrefix());
        }
    }

    private boolean delegateToParent(ZuulProperties.ZuulRoute route) {
        return !route.getId().contains("regex");
    }
}
