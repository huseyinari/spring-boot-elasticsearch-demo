package tr.com.huseyinari.elasticsearchdemo.documents;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class Employee {
    private String id;
    private String name;
    private Float salary;
    private Integer age;
    private Date start_date;
    private boolean active;
}