package tr.com.huseyinari.elasticsearchdemo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinari.elasticsearchdemo.documents.EmployeeDocument;
import tr.com.huseyinari.elasticsearchdemo.dto.EmployeeDto;
import tr.com.huseyinari.elasticsearchdemo.service.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public List<EmployeeDocument> getAll() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public EmployeeDocument getById(@PathVariable(name = "id") String id) {
        return employeeService.findOne(id);
    }

    @PostMapping
    public EmployeeDocument save(@RequestBody EmployeeDto dto) {
        return this.employeeService.save(dto);
    }

    @PutMapping
    public EmployeeDocument update(@RequestBody EmployeeDto dto) {
        return this.employeeService.update(dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        this.employeeService.remove(id);
    }
}
