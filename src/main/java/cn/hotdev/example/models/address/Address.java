package cn.hotdev.example.models.address;

import cn.hotdev.example.models.customer.Customer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@EqualsAndHashCode(callSuper = false)
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JsonIgnore
    private Customer customer;
    @Column(nullable = false)
    private String addr;
    @Column(nullable = false)
    private String phone;
    @Column(nullable = false)
    protected long createdAt;
    @Column(nullable = false)
    protected long updatedAt;

    public void update(Address other) {
        if (other.addr != null)
            addr = other.addr;
        if (other.phone != null)
            phone = other.phone;
    }

    @Override
    public String toString() {
        return String.format(
                "Address[id=%d, addr='%s', phone='%s']",
                id, addr, phone);
    }
}
