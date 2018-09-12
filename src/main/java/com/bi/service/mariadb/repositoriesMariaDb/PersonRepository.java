package com.bi.service.mariadb.repositoriesMariaDb;

import com.bi.service.mariadb.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Integer> {

    List<Person> findByCountriesName(String name);

    List<Person> findByGenderName(String name);

}

