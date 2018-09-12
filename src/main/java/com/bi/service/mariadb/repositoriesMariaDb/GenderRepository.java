package com.bi.service.mariadb.repositoriesMariaDb;

import com.bi.service.mariadb.models.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenderRepository extends JpaRepository<Gender, Long> {

    Gender findByName(String name);

}

