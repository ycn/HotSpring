package cn.hotdev.example.models.customer;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CustomerRepository extends PagingAndSortingRepository<Customer, Long> {
    List<Customer> findByLastName(@Param("name") String name);

    @Override
    @Cacheable(value = "customer", key = "#p0")
    Customer findOne(Long id);

    @Override
    @CacheEvict(value = "customer", key = "#p0.id")
    <S extends Customer> S save(S customer);

    @Override
    @CacheEvict(value = "customer", key = "#p0")
    void delete(Long id);

    @Override
    @CacheEvict(value = "customer", allEntries = true)
    void deleteAll();
}
