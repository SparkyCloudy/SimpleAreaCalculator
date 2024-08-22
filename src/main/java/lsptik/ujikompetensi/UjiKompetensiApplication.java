package lsptik.ujikompetensi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class UjiKompetensiApplication {

  public static void main(String[] args) {
    SpringApplication.run(UjiKompetensiApplication.class, args);
  }

}
