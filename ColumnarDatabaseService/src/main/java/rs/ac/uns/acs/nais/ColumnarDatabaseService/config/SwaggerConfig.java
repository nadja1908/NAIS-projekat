package rs.ac.uns.acs.nais.ColumnarDatabaseService.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("public")
            .pathsToMatch(
                "/api/v1/subjects/**",
                "/api/v1/student-grades/**",
                "/api/v1/subject-statistics/**",
                "/api/v1/advanced/**")
            .build();
    }

    @Bean
    public GroupedOpenApi subjectsCrud() {
        return GroupedOpenApi.builder()
            .group("subjects-crud")
            .pathsToMatch("/api/v1/subjects/**")
            .build();
    }

    @Bean
    public GroupedOpenApi gradesCrud() {
        return GroupedOpenApi.builder()
            .group("grades-crud")
            .pathsToMatch("/api/v1/student-grades/**")
            .build();
    }

    @Bean
    public GroupedOpenApi subjectStatistics() {
        return GroupedOpenApi.builder()
            .group("subject-statistics")
            .pathsToMatch("/api/v1/subject-statistics/**")
            .build();
    }

    @Bean
    public GroupedOpenApi advanced() {
        return GroupedOpenApi.builder()
            .group("advanced")
            .pathsToMatch("/api/v1/advanced/**")
            .build();
    }
}