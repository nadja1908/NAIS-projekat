package rs.ac.uns.acs.nais.ColumnarDatabaseService.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    /**
     * Allow encoded slashes in path variables. This lets values like
     * "2024/2025" be passed (encoded) in a path segment without Tomcat
     * rejecting the request.
     */
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> factory.addConnectorCustomizers(connector ->
                connector.setProperty("allowEncodedSlashes", "true")
        );
    }
}
