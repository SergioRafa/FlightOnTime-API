package com.flightontime.api; // O pacote PRECISA ser este para encontrar o Controller

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);

        System.out.println("\n\n=========================================");
        System.out.println("   FLIGHT ON TIME - BACKEND ONLINE!      ");
        System.out.println("   URL: http://localhost:5000/predict!              ");
        System.out.println("   Pronto para a apresentação!           ");
        System.out.println("=========================================\n");
    }
}
