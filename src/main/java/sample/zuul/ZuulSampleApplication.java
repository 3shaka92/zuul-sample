package sample.zuul;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@SpringBootApplication
@EnableZuulProxy
public class ZuulSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZuulSampleApplication.class, args);
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> commonTags() {
        return registry ->
                registry.config()
                        .commonTags("application", "zuul-sample");
    }
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(corsConfigurationSource);
    }
    
    @Bean
    @Order(-1)
    public RouteLocator customRouteLocator(ZuulProperties zuulProperties) {
        return new CustomRouteLocator("/", zuulProperties);
    }
}
