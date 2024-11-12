package tr.com.huseyinari.elasticsearchdemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tr.com.huseyinari.elasticsearchdemo.dto.EmployeeDto;
import tr.com.huseyinari.elasticsearchdemo.service.EmployeeService;

import java.time.LocalDate;

@SpringBootApplication
public class ElasticSearchDemoApplication {
	@Autowired
	private EmployeeService employeeService;

	public static void main(String[] args) {
		SpringApplication.run(ElasticSearchDemoApplication.class, args);
	}

	CommandLineRunner clr() {
		return args -> {
			for (int i = 0; i < 80; i++) {
				EmployeeDto dto = new EmployeeDto();
				dto.setName("Yeni Personel " + i);
				dto.setAge(i + 10);
				dto.setSalary((long) (i * 100 + 100));
				dto.setActive(i % 2 == 0);
				dto.setStartDate(LocalDate.now().minusDays(i * 10));

				employeeService.save(dto);
			}
		};
	}

}
