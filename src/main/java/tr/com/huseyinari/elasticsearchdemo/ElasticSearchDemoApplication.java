package tr.com.huseyinari.elasticsearchdemo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tr.com.huseyinari.elasticsearchdemo.documents.EmployeeDocument;
import tr.com.huseyinari.elasticsearchdemo.dto.EmployeeDto;
import tr.com.huseyinari.elasticsearchdemo.service.EmployeeService;

import java.time.LocalDate;
import java.util.UUID;

@SpringBootApplication
public class ElasticSearchDemoApplication {
	private final Logger logger = LoggerFactory.getLogger(ElasticSearchDemoApplication.class);

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private ElasticsearchClient elasticsearchClient;

	public static void main(String[] args) {
		SpringApplication.run(ElasticSearchDemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner clr() {
		return args -> {
			GetResponse<EmployeeDocument> response = elasticsearchClient.get(g -> g.index("employees").id("549106a2-be1a-415d-b356-7e703a74399d"), EmployeeDocument.class);

			if (response.found()) {
				EmployeeDocument result = response.source();

				if (result != null) {
					logger.debug(result.toString());
				}
			}
		};
	}

//	@Bean
//	public CommandLineRunner clr() {
//		return args -> {
//			for (int i = 1; i <= 80; i++) {
//				EmployeeDto dto = new EmployeeDto();
//				dto.setId(UUID.randomUUID().toString());
//				dto.setName("Personel " + i);
//				dto.setSalary((long) (i * 1000 + 500));
//				dto.setAge(i + 10);
//				dto.setStartDate(LocalDate.now().minusDays(i * 10));
//				dto.setActive(i % 2 == 0);
//
//				employeeService.save(dto);
//			}
//		};
//	}
}
