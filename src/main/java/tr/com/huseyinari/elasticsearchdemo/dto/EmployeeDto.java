package tr.com.huseyinari.elasticsearchdemo.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeDto {
    private String id;
    private String name;
    private Long salary;
    private Integer age;
    private LocalDate startDate;
    private boolean active;
}
