package tr.com.huseyinari.elasticsearchdemo;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import tr.com.huseyinari.elasticsearchdemo.documents.Employee;
import tr.com.huseyinari.elasticsearchdemo.documents.EmployeeDocument;
import tr.com.huseyinari.elasticsearchdemo.dto.EmployeeDto;
import tr.com.huseyinari.elasticsearchdemo.service.EmployeeService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableElasticsearchRepositories
public class ElasticSearchDemoApplication {
	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private ElasticsearchClient elasticsearchClient;

	public static void main(String[] args) {
		SpringApplication.run(ElasticSearchDemoApplication.class, args);

	// ------------------ SPRING FRAMEWORK KULLANMAKDAN ELASTICSEARCH CLIENT OLUŞTURMAK ------------------
//		RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
//		ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
//		ElasticsearchClient esClient = new ElasticsearchClient(transport);
	}

//	@Bean
	public CommandLineRunner clr1() {
		return args -> {
			for (int i = 1; i <= 80; i++) {
				EmployeeDto dto = new EmployeeDto();
				dto.setId(UUID.randomUUID().toString());
				dto.setName("Personel " + i);
				dto.setSalary((float)(i * 1000 + 500));
				dto.setAge(i + 10);
				dto.setStartDate(LocalDate.now().minusDays(i * 10));
				dto.setActive(i % 2 == 0);

				employeeService.save(dto);
			}
		};
	}

//	@Bean
	public CommandLineRunner clr2() {
		return args -> {
			String indexName = "employee-index";

			// ------------------------------------ TÜM EMPLOYEE'LERİ GETİR ------------------------------------

			// tek seferde tüm kayıtları çekemiyoruz.varsayılan olarak 10 kayıt getiriyor.
			Query matchAllQuery = new MatchAllQuery.Builder().build()._toQuery();
			SearchResponse<Employee> allResponse = elasticsearchClient.search(s -> s.index(indexName).query(matchAllQuery), Employee.class);

			int sizePerScroll = 20;	// Her scroll'da kaç veri çekilecek ?
			List<Employee> allEmployees = new ArrayList<>();

			SearchRequest searchAllRequest = SearchRequest.of(
				s -> s.index(indexName)
						.query(matchAllQuery)
						.scroll(Time.of(t -> t.time("2m")))
						.size(sizePerScroll)
			);
			SearchResponse<Employee> searchAllResponse = elasticsearchClient.search(searchAllRequest, Employee.class);

			List<Employee> result = searchAllResponse.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
			allEmployees.addAll(result);

			String scrollId = searchAllResponse.scrollId();
			while (true) {
				ScrollRequest request = new ScrollRequest.Builder().scrollId(scrollId).scroll(Time.of(t -> t.time("2m"))).build();
				ScrollResponse<Employee> scrollResponse = elasticsearchClient.scroll(request, Employee.class);

				List<Employee> scrollResult = scrollResponse.hits().hits().stream().map(Hit::source).collect(Collectors.toList());

				if (scrollResult.isEmpty()) {
					break;	// sorgudan hiçbir kayıt gelmeyene kadar döngüye devam
				} else {
					allEmployees.addAll(scrollResult);
					scrollId = scrollResponse.scrollId();
				}
			}

			for (int i = 1; i <= allEmployees.size(); i++) {
//				System.out.println(i + ". kayıt: " + allEmployees.get(i - 1));
			}

			// ------------------------------------ ID'ye göre Employee Sorgulama ------------------------------------
			String employeeId = "5024dabd-9c87-43b8-8bb6-e81a7135cb0d";

			// Hedef sınıfa jackson kütüphanesinin @JsonIgnoreProperties(ignoreUnknown = true) özelliğini ekle. Çünkü aksi taktirde elastic tarafından gelen veride sınıfta olmayan bir property
			// varsa hata fırlatılıyor. Bunun yanında dönüştürecek bir sınıf oluşturmak istemiyorsan sonucu Map veri yapısı olarak alabilirsin.
			GetRequest getRequest = GetRequest.of(g -> g.index(indexName).id(employeeId));
			GetResponse<Employee> findByIdResponse = elasticsearchClient.get(getRequest, Employee.class);

			if (findByIdResponse.found()) {
				Employee employee = findByIdResponse.source();

				if (employee != null) {
					System.out.println("ID'ye Göre Arama Sonucu: " + result);
				}
			}

			// ------------------------------------ Yeni Employee Kaydetme ------------------------------------
			String id = UUID.randomUUID().toString();

			Employee employee = new Employee();
			employee.setId(id);
			employee.setName("Hüseyin Arı");
			employee.setAge(25);
			employee.setActive(true);
			employee.setStart_date(new Date());
			employee.setSalary(40000f);

			CreateRequest<Employee> createRequest = CreateRequest.of(c -> c.index(indexName).id(id).document(employee));
			CreateResponse createResponse = elasticsearchClient.create(createRequest);

			if ("Created".equals(createResponse.result().name())) {
				System.out.println("Kaydetme işlemi başarılı : " + createResponse);
			}

			// ------------------------------------ Mevcut Employee Güncelleme ------------------------------------
			Employee updatedEmployee = new Employee();
			updatedEmployee.setId(id);
			updatedEmployee.setName("Hüseyin Arı Güncellendi");
			updatedEmployee.setAge(26);
			// Elasticsearch güncelleme işleminde null alanları görmezden gelir. Örneğin employee'nin name alanını null setlersek güncelleme işleminden sonra kaydın name alanı değişmeyecektir.
			// Bir alanı null olarak güncellemek istiyorsak bunu update API'deki script'i  kullanarak yapmalıyız.
			UpdateRequest updateRequest = UpdateRequest.of(u -> u.index(indexName).id(id).doc(updatedEmployee));
			UpdateResponse<Employee> updateResponse = elasticsearchClient.update(updateRequest, Employee.class);

			if ("Updated".equals(updateResponse.result().name())) {
				System.out.println("Güncelleme işlemi başarılı : " + updateResponse);
			}

			// ------------------------------------ Employee Silme ------------------------------------
			DeleteRequest deleteRequest = DeleteRequest.of(d -> d.index(indexName).id(id));
			DeleteResponse deleteResponse = elasticsearchClient.delete(deleteRequest);

			if ("Deleted".equals(deleteResponse.result().name())) {
				System.out.println("Silme işlemi başarılı : " + deleteResponse);
			}

			// ------------------------------------ Spesifik Sorgular ------------------------------------

			Query query = Query.of(
				q -> q.bool(
						b -> b.must(MatchQuery.of(m -> m.field("name").query("Hüseyin"))._toQuery())
							  .should(RangeQuery.of(r -> r.field("salary").gt(JsonData.of(30000)).lt(JsonData.of(60000)))._toQuery())
							  .should(RangeQuery.of(r -> r.field("age").lt(JsonData.of(25)))._toQuery())
							  .filter(Query.of(f -> f.term(t -> t.field("active").value(true))))
				)
			);

			SearchRequest searchRequest = SearchRequest.of(s -> s.index(indexName).query(query));
			SearchResponse<Employee> searchResponse = elasticsearchClient.search(searchRequest, Employee.class);

			System.out.println(searchResponse);
		};
	}

}
