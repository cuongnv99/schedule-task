package rmhub.mod.schedule;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@EnableScheduling
@SpringBootApplication
public class ScheduleApplication {

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Autowired
  private RestTemplateBuilder builder;

  @Bean
  public RestTemplate restTemplate() {
    return builder.build();
  }

  public static void main(String[] args) {
    SpringApplication.run(ScheduleApplication.class, args);
  }
}
