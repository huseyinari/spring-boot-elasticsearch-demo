package tr.com.huseyinari.elasticsearchdemo.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

import java.time.LocalDate;

@Getter
@Setter
@Document(indexName = "employees", createIndex = true, writeTypeHint = WriteTypeHint.FALSE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeDocument {
    @Id
    private String id;

    @Field(type = FieldType.Text, name = "name")
    private String name;

    @Field(type = FieldType.Float, name = "salary")
    private Float salary;

    @Field(type = FieldType.Integer, name = "age")
    private Integer age;

    @Field(type = FieldType.Date, name = "start_date")
    private LocalDate startDate;

    @Field(type = FieldType.Boolean, name = "active")
    private boolean active;
}
