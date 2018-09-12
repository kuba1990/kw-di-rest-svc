package com.bi.service.mariadb.repositoriesMariaDb;

import com.bi.service.mariadb.models.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Country findByName(String name);


}
