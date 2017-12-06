package com.davinryan.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * All webconfiguration for this application. Mostly swagger stuff here.
 */
@Configuration
@EnableSwagger2
@EnableCaching
public class WebAppConfig extends WebMvcConfigurerAdapter {

    @Value("#{'${invoicing.swagger.version:Not Set}'}")
    private String version;

    @Value("#{'${invoicing.swagger.title:Not Set}'}")
    private String title;

    @Value("#{'${invoicing.swagger.description:Not Set}'}")
    private String description;

    @Value("#{'${invoicing.swagger.termsOfService:Not Set}'}")
    private String termsOfService;

    @Value("#{'${invoicing.swagger.contact:Not Set}'}")
    private String contact;

    @Value("#{'${invoicing.swagger.licence:Not Set}'}")
    private String licence;

    @Value("#{'${invoicing.swagger.licenceUrl:Not Set}'}")
    private String licenceUrl;

    /**
     * Configure static resources for swagger ui (only applies if those resources are there. See pom.xml for more details.
     *
     * @param registry default registry.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(getApiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo getApiInfo() {
        ApiInfoBuilder apiInfoBuilder = new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version)
                .termsOfServiceUrl(termsOfService)
                .contact(contact)
                .license(licence)
                .licenseUrl(licenceUrl);
        return apiInfoBuilder.build();
    }
}
