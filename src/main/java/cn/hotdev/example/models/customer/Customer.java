package cn.hotdev.example.models.customer;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "serial") // postgresql fix
    private Long id;
    @Column(nullable = true, length = 100)
    private String firstName;
    @Column(nullable = true, length = 100)
    private String lastName;

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }
}
