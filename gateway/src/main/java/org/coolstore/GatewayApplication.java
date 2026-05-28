package org.coolstore;

//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class GatewayApplication {
//    public static void main(String[] args) {
//        SpringApplication.run(GatewayApplication.class, args);
//    }
//}

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class  GatewayApplication {
    public static void main(String[] args) {
        Quarkus.run(args);
    }
}

