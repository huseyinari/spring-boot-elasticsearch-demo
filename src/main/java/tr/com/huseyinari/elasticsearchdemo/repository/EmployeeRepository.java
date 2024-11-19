package tr.com.huseyinari.elasticsearchdemo.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import tr.com.huseyinari.elasticsearchdemo.documents.EmployeeDocument;

public interface EmployeeRepository extends ElasticsearchRepository<EmployeeDocument, String> {
}
