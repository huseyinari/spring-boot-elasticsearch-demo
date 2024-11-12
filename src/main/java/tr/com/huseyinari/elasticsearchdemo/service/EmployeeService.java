package tr.com.huseyinari.elasticsearchdemo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import tr.com.huseyinari.elasticsearchdemo.documents.EmployeeDocument;
import tr.com.huseyinari.elasticsearchdemo.dto.EmployeeDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final ElasticsearchOperations elasticsearchOperations;

    public EmployeeDocument findOne(String id) {
        EmployeeDocument employee = elasticsearchOperations.get(id, EmployeeDocument.class);
        if (employee == null) {
            throw new RuntimeException("Personel bulunamadı !");
        }

        return employee;
    }

    public EmployeeDocument save(EmployeeDto dto) {
        EmployeeDocument employee = new EmployeeDocument();
        employee.setId(UUID.randomUUID().toString());
        employee.setName(dto.getName());
        employee.setAge(dto.getAge());
        employee.setSalary(dto.getSalary());
        employee.setStartDate(dto.getStartDate());
        employee.setActive(dto.isActive());

        return elasticsearchOperations.save(employee);
    }

    public EmployeeDocument update(EmployeeDto dto) {
        EmployeeDocument employee = this.findOne(dto.getId());
        employee.setName(dto.getName());
        employee.setAge(dto.getAge());
        employee.setSalary(dto.getSalary());
        employee.setStartDate(dto.getStartDate());
        employee.setActive(dto.isActive());

        return elasticsearchOperations.save(employee);
    }

    public boolean remove(String id) {
        elasticsearchOperations.delete(id, EmployeeDocument.class);
        return true;
    }


}
