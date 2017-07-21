package com.github.athieriot;

import akka.actor.ActorSystem;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.any;
import static springfox.documentation.builders.RequestHandlerSelectors.withClassAnnotation;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@EnableSwagger2
@SpringBootApplication
public class KalahApi {

	public static void main(String[] args) {
		SpringApplication.run(KalahApi.class, args);
	}

	@Bean
    public ActorSystem system() {
        return ActorSystem.create("kalah");
    }

	@Bean
	public Docket swaggerSpringMvcPlugin() {
		return new Docket(SWAGGER_2)
				.groupName("kalah-api")
                .apiInfo(apiInfo())
				.select()
				//Ignores controllers not annotated with @RestController
				.apis(withClassAnnotation(RestController.class))
                .paths(any())
                .build();
	}

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Kalah API")
                .description("API service to play games of Kalah")
                .license("MIT")
                .licenseUrl("https://opensource.org/licenses/MIT")
                .build();
    }
}
