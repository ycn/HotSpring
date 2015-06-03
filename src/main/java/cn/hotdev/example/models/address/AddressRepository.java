package cn.hotdev.example.models.address;

import cn.hotdev.example.models.customer.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends PagingAndSortingRepository<Address, Long> {
    List<Address> findByCustomer(@Param("customer") Customer customer);
}
