package com.bi.service.service;

import com.bi.service.modelRestApi.PersonAggregatedByCountryDto;
import com.bi.service.modelRestApi.PersonAggregatetedByGenderDto;
import com.bi.service.modelRestApi.PersonDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface PersonService {

    public PersonDto addPerson(PersonDto person);

    public ResponseEntity deletePerson(PersonDto person);

    public PersonDto putPerson(PersonDto person);

    public PersonDto getPerson(Integer id);

    public List<PersonDto> getPersonCountry(String country);

    public List<PersonDto> getPersonGender(String gender);

    public Map<String, List<PersonAggregatedByCountryDto>> getByCountry();

    public Map<String, Map<String, List<PersonAggregatetedByGenderDto>>> getByCountryByGender();
}


