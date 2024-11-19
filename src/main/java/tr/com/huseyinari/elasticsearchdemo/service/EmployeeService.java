package tr.com.huseyinari.elasticsearchdemo.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import tr.com.huseyinari.elasticsearchdemo.documents.EmployeeDocument;
import tr.com.huseyinari.elasticsearchdemo.dto.EmployeeDto;
import tr.com.huseyinari.elasticsearchdemo.repository.EmployeeRepository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    //private final ElasticsearchOperations elasticsearchOperations;
//    private final ElasticsearchClient elasticsearchClient;
    private final EmployeeRepository employeeRepository;

    public List<EmployeeDocument> findAll() {
        Iterable<EmployeeDocument> iterable = employeeRepository.findAll();

        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    public EmployeeDocument findOne(String id) {
        Optional<EmployeeDocument> employee = employeeRepository.findById(id);
        if (!employee.isPresent()) {
            throw new RuntimeException("Personel bulunamadı !");
        }

        return employee.get();
    }

    public EmployeeDocument save(EmployeeDto dto) {
        EmployeeDocument employee = new EmployeeDocument();
        employee.setId(UUID.randomUUID().toString());
        employee.setName(dto.getName());
        employee.setAge(dto.getAge());
        employee.setSalary(dto.getSalary());
        employee.setStartDate(dto.getStartDate());
        employee.setActive(dto.isActive());

        return employeeRepository.save(employee);
    }

    public EmployeeDocument update(EmployeeDto dto) {
        EmployeeDocument employee = this.findOne(dto.getId());
        employee.setName(dto.getName());
        employee.setAge(dto.getAge());
        employee.setSalary(dto.getSalary());
        employee.setStartDate(dto.getStartDate());
        employee.setActive(dto.isActive());

        return employeeRepository.save(employee);
    }

    public boolean remove(String id) {
        employeeRepository.deleteById(id);
        return true;
    }


}
