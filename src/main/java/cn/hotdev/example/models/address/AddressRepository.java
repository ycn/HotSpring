package cn.hotdev.example.models.address;

import cn.hotdev.example.models.customer.Customer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends PagingAndSortingRepository<Address, Long> {
    @Cacheable(value = "addressesByCustomer", key = "#p0.id")
    List<Address> findByCustomer(@Param("customer") Customer customer);
}
