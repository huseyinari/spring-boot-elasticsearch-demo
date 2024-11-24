package tr.com.huseyinari.elasticsearchdemo.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class EmployeeDto {
    private String id;
    private String name;
    private Float salary;
    private Integer age;
    private LocalDate startDate;
    private boolean active;
}
