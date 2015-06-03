package cn.hotdev.example.models.address;

import cn.hotdev.example.models.base.Base;
import cn.hotdev.example.models.customer.Customer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@EqualsAndHashCode(callSuper = false)
@Data
public class Address extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JsonBackReference
    private Customer customer;
    @Column(nullable = false)
    private String addr;
    @Column(nullable = false)
    private String phone;

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
