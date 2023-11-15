package api.user.management;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class Application {

  public static void main(String[] args) {
    printDisclaimer();
    SpringApplication.run(Application.class, args);
  }

  private static void printDisclaimer() {
    // Print your disclaimer to the console
    log.info("=================================================");
    log.info("User Management API - College Project Disclaimer");
    log.info("=================================================");
    log.info(
        "This software is part of a college project and is intended for educational purposes only.");
    log.info(
        "It is not to be sold, distributed, or utilized for any commercial purposes.");
    log.info(
        "All rights to this software are reserved, and any unauthorized use or reproduction is strictly prohibited.");
    log.info(
        "By using this software, you acknowledge and agree to adhere to these terms.");
    log.info("=================================================");
  }

}
