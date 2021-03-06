package isa.project;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication/*(scanBasePackages = "isa.project")*/
//@ComponentScan({"isa.project.service.impl"})
//@EntityScan("isa.project.domain")
//@EnableJpaRepositories("isa.project.repositories")
@EnableTransactionManagement
@EnableJpaRepositories
public class IsaProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(IsaProjectApplication.class, args);
	}
//		@Bean
//		  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
//			    return args -> {
//
//			      System.out.println("Let's inspect the beans provided by Spring Boot:");
//
//			      String[] beanNames = ctx.getBeanDefinitionNames();
//			      Arrays.sort(beanNames);
//			      for (String beanName : beanNames) {
//			        System.out.println(beanName);
//			      }
//			    };
//		  }
}
