package com.szj.demo.repository;

import com.szj.demo.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT a FROM Address a WHERE a.country= :country AND a.street = :street AND a.city = :city AND a.zipCode = :zipCode")
    Optional<Address> findByDetails(@Param("country")String country, @Param("city") String city, @Param("street") String street, @Param("zipCode") String zipCode);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId")
    List<Address> findAddressesByUserId(@Param("userId") Long userId);
}
