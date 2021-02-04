package backoffice.back;

import backoffice.back.handleimages.FileStorageProperties;
import backoffice.back.securitycontroller.AuthController;
import backoffice.back.securitymodels.ERole;
import backoffice.back.securitymodels.Role;
import backoffice.back.securitymodels.Rolerepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
@SpringBootApplication
@EnableConfigurationProperties({
        FileStorageProperties.class
})

public class BackApplication  implements CommandLineRunner {
  @Autowired
  private Rolerepo repr;
  @Autowired
  private AuthController u;

    public static void main(String[] args) {
        SpringApplication.run(BackApplication.class, args);
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }




  @Override
  public void run(String... args) throws Exception {
    Role r1=new Role(1,ERole.ROLE_USER);
    Role r2=new Role(2, ERole.ROLE_ADMIN);
    repr.save(r1);
    repr.save(r2);
    u.ajouteradmin();

  }
}
