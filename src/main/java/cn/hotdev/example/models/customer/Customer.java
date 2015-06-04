package cn.hotdev.example.models.customer;

import cn.hotdev.example.models.address.Address;
import cn.hotdev.example.models.base.EntityBase;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@EqualsAndHashCode(callSuper = false)
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer extends EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
//    @Column(columnDefinition = "serial") // postgresql fix
    private Long id;
    @Column(nullable = true, length = 100)
    private String firstName;
    @Column(nullable = false, length = 100)
    private String lastName;
    @Column(nullable = false)
    private Integer age;
    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Address> addresses = new LinkedHashSet<Address>();

    public void update(Customer other) {
        if (other.firstName != null)
            firstName = other.firstName;
        if (other.lastName != null)
            lastName = other.lastName;
        if (other.age != null && other.age > 0)
            age = other.age;
    }

    @Override
    public String toString() {
        return String.format(
                "Customer[id=%d, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }
}
