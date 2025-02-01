package com.example.reactiverestservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.time.Duration;

@SpringBootApplication
public class ReactiveRestServiceApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ReactiveRestServiceApplication.class, args);
        GreetingClient greetingClient = context.getBean(GreetingClient.class);
        System.out.println(">> message = " + greetingClient.getMessage().block());
        System.out.println(">> message = " + greetingClient.getMessage().block());
    }

    @Component
    public static class Greeting {

        private String message;

        public Greeting() {
        }

        public Greeting(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "Greeting{" + "message='" + message + '\'' + '}';
        }
    }

    @Component
    public static class GreetingHandler {

        public Mono<ServerResponse> hello(ServerRequest request) {
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(new Greeting("Hello, Spring!"))).delayElement(Duration.ofSeconds(2L));
        }
    }

    @Configuration(proxyBeanMethods = false)
    public static class GreetingRouter {

        @Bean
        public RouterFunction<ServerResponse> route(GreetingHandler greetingHandler) {
            return RouterFunctions
                    .route(org.springframework.web.reactive.function.server.RequestPredicates.GET("/hello")
                            .and(org.springframework.web.reactive.function.server.RequestPredicates.accept(MediaType.APPLICATION_JSON)), greetingHandler::hello);
        }
    }

    @Component
    public static class GreetingClient {

        private final WebClient client;

        public GreetingClient(WebClient.Builder builder) {
            this.client = builder.baseUrl("http://localhost:8080").build();
        }

        public Mono<String> getMessage() {
            return this.client.get().uri("/hello").accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Greeting.class)
                    .map(Greeting::getMessage);
        }
    }
}
