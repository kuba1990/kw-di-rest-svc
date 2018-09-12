package com.bi.service.service;

import com.bi.service.exception.ResourceBadRequestException;
import com.bi.service.exception.ResourceNotFoundException;
import com.bi.service.mariadb.models.Country;
import com.bi.service.mariadb.models.Gender;
import com.bi.service.mariadb.models.Person;
import com.bi.service.mariadb.repositoriesMariaDb.CountryRepository;
import com.bi.service.mariadb.repositoriesMariaDb.GenderRepository;
import com.bi.service.mariadb.repositoriesMariaDb.PersonRepository;
import com.bi.service.modelRestApi.PersonAggregatedByCountryDto;
import com.bi.service.modelRestApi.PersonAggregatetedByGenderDto;
import com.bi.service.modelRestApi.PersonDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    private CountryRepository countryRepository;
    private GenderRepository genderRepository;
    private PersonRepository personRepository;


    public PersonServiceImpl(CountryRepository countryRepository, GenderRepository genderRepository, PersonRepository personRepository) {
        this.countryRepository = countryRepository;
        this.genderRepository = genderRepository;
        this.personRepository = personRepository;
    }

    @Override
    public PersonDto addPerson(PersonDto personDto) {
        Person person = new Person();

        if (!countriesAreCorrect(personDto)) {
            throw new ResourceBadRequestException("duplicated value for country or empty input");
        }

        checkIfNullOrEmpty(personDto, "check input again, one of field is null ");

        gendervalidator(personDto);

        person.setName(personDto.getName());
        person.setLastName(personDto.getLast_name());
        person.setAdditionalInfo(personDto.getAdditonal_info());

        for (String country : personDto.getCountry()) {
            person.getCountries().add(storeCountryIfNotExist(country));

        }
        person.setGender(storeGenderIfNotExist(personDto.getGender()));
        person = personRepository.saveAndFlush(person);
        personDto.setId(person.getId());
        return personDto;
    }

    private void gendervalidator(PersonDto personDto) {
        String genderTemp = personDto.getGender();
        if (!genderTemp.equals("Male") && !genderTemp.equals("Female") && !genderTemp.equals("Unknown")) {
            throw new ResourceBadRequestException("wrong gender, use Male, Female or Unknown");
        }
    }

    @Override
    public ResponseEntity deletePerson(PersonDto personDto) {
        Optional<Person> person = Optional.ofNullable(personRepository.findOne(personDto.getId()));
        if (!person.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        personRepository.delete(personDto.getId());
        return ResponseEntity.noContent().build();
    }

    @Override
    public PersonDto putPerson(PersonDto personDto) {

        if (!countriesAreCorrect(personDto)) {
            throw new ResourceBadRequestException("bad request exception, check message: " + personDto.toString());
        }

        checkIfNullOrEmpty(personDto, "check input again, one of field is null or empty");

        Person personFind = personRepository.findOne(personDto.getId());

        Optional<Person> person = Optional.ofNullable(personFind);
        if (!person.isPresent())
            throw new ResourceNotFoundException();

        personFind.setName(personDto.getName());
        personFind.setLastName(personDto.getLast_name());
        personFind.setAdditionalInfo(personDto.getAdditonal_info());

        gendervalidator(personDto);

        personFind.setGender(storeGenderIfNotExist(personDto.getGender()));

        personFind.getCountries().clear();
        for (String country : personDto.getCountry()) {
            personFind.getCountries().add(storeCountryIfNotExist(country));
        }

        if (isDuplicatedInListOfCountry(personDto.getId()) != false) {
            personRepository.saveAndFlush(personFind);
            return personDto;
        }
        throw new InvalidParameterException("duplicated country ");

    }

    private void checkIfNullOrEmpty(PersonDto personDto, String s) {
        if (personDto.getName() == null || personDto.getName().isEmpty() ||
                personDto.getLast_name() == null || personDto.getLast_name().isEmpty() ||
                personDto.getAdditonal_info() == null || personDto.getAdditonal_info().isEmpty() ||
                personDto.getCountry() == null || personDto.getCountry().size() == 0 ||
                personDto.getGender() == null || personDto.getGender().isEmpty()
        ) {
            throw new InvalidParameterException(s);
        }
    }

    @Override
    public PersonDto getPerson(Integer id) {

        Optional<Person> person = Optional.ofNullable(personRepository.findOne(id));
        if (!person.isPresent()) {
            throw new com.bi.service.exception.ResourceNotFoundException();
        }

        Person personFind = personRepository.findOne(id);
        PersonDto personDto = new PersonDto();
        personDto.setId(personFind.getId());
        personDto.setName(personFind.getName());
        personDto.setLast_name(personFind.getLast_name());
        personDto.setAdditonal_info(personFind.getAdditional_info());
        personDto.setGender(personFind.getGender().getName());

        for (Country country : personFind.getCountries()) {
            personDto.getCountry().add(country.getName());

        }

        return personDto;
    }

    @Override
    public List<PersonDto> getPersonCountry(String country) {
        List<Person> persons = personRepository.findByCountriesName(country);

        if (persons.size() <= 0) {
            throw new com.bi.service.exception.ResourceNotFoundException();
        }

        List<PersonDto> personDtos = new ArrayList<>();

        getListOfPerson(persons, personDtos);

        return personDtos;
    }

    private void getListOfPerson(List<Person> persons, List<PersonDto> personDtos) {
        for (Person person : persons) {
            PersonDto personDto = new PersonDto();
            personDto.setId(person.getId());

            personDto.setGender(person.getGender().getName());
            personDto.setName(person.getName());

            personDto.setLast_name(person.getLast_name());
            personDto.setAdditonal_info(person.getAdditional_info());

            for (Country countryPerson : person.getCountries()) {
                personDto.getCountry().add(countryPerson.getName());

            }

            personDtos.add(personDto);

        }
    }

    @Override
    public List<PersonDto> getPersonGender(String gender) {

        List<Person> persons = personRepository.findByGenderName(gender);
        if (persons.size() <= 0) {
            throw new com.bi.service.exception.ResourceNotFoundException();
        }

        List<PersonDto> personDtos = new ArrayList<>();

        getListOfPerson(persons, personDtos);
        return personDtos;
    }


    public Map<String, List<PersonAggregatedByCountryDto>> getByCountry() {

        List<Country> countries = countryRepository.findAll();
        Map<String, List<PersonAggregatedByCountryDto>> personByCountry = new HashMap<>();
        for (Country country : countries) {
            personByCountry.put(country.getName(), getPersonWithoutCountry(country.getName()));

        }

        return personByCountry;
    }

    public Map<String, Map<String, List<PersonAggregatetedByGenderDto>>> getByCountryByGender() {

        List<Gender> genders = genderRepository.findAll();
        List<Country> countries = countryRepository.findAll();

        Map<String, Map<String, List<PersonAggregatetedByGenderDto>>> personByGenderCountry = new HashMap<>();

        for (Gender gender : genders) {

            Map<String, List<PersonAggregatetedByGenderDto>> personByCountry = new HashMap<>();


            for (Country country : countries) {

                List<PersonDto> personDtoList = getPersonCountry(country.getName()).stream().filter(person -> person.getGender().equals(gender.getName())).collect(Collectors.toList());
                List<PersonAggregatetedByGenderDto> personAggregatetedByGenderDtoList = new ArrayList<>();


                for (PersonDto personDto : personDtoList) {
                    PersonAggregatetedByGenderDto personAggregatetedByGenderDto = new PersonAggregatetedByGenderDto();

                    personAggregatetedByGenderDto.setId(personDto.getId());
                    personAggregatetedByGenderDto.setName(personDto.getName());
                    personAggregatetedByGenderDto.setLast_name(personDto.getLast_name());
                    personAggregatetedByGenderDto.setAdditonal_info(personDto.getAdditonal_info());

                    personAggregatetedByGenderDtoList.add(personAggregatetedByGenderDto);

                }

                if (personAggregatetedByGenderDtoList.size() > 0) {
                    personByCountry.put(country.getName(), personAggregatetedByGenderDtoList);
                }

            }
            personByGenderCountry.put(gender.getName(), personByCountry);
        }
        return personByGenderCountry;
    }


    private Gender storeGenderIfNotExist(String gender) {

        Gender genderFromRepo = genderRepository.findByName(gender);

        if (genderFromRepo != null) {
            return genderFromRepo;
        }

        Gender newGender = new Gender();
        newGender.setName(gender);

        return genderRepository.save(newGender);
    }


    private Country storeCountryIfNotExist(String country) {
        Country countryFromRepo = countryRepository.findByName(country);

        if (countryFromRepo != null) {
            return countryFromRepo;
        }
        Country newCountry = new Country();
        newCountry.setName(country);

        return countryRepository.save(newCountry);

    }


    private boolean countriesAreCorrect(PersonDto personDto) {
        Set<String> uniqueCountries = new HashSet<>();
        for (String country : personDto.getCountry()) {
            uniqueCountries.add(country);
            if (country == "") {
                return false;
            }

        }
        if (uniqueCountries.size() == personDto.getCountry().size()) {
            return true;
        }
        return false;
    }

    private List<PersonAggregatedByCountryDto> getPersonWithoutCountry(String country) {
        List<Person> persons = personRepository.findByCountriesName(country);

        if (persons.size() <= 0) {
            throw new com.bi.service.exception.ResourceNotFoundException();
        }

        List<PersonAggregatedByCountryDto> personAggregatedByCountryDto = new ArrayList<>();

        getListOfPersonWithoutCountry(persons, personAggregatedByCountryDto);

        return personAggregatedByCountryDto;
    }


    private void getListOfPersonWithoutCountry(List<Person> persons, List<PersonAggregatedByCountryDto> personDtos) {
        for (Person person : persons) {
            PersonAggregatedByCountryDto personAggregatedByCountryDto = new PersonAggregatedByCountryDto();
            personAggregatedByCountryDto.setId(person.getId());

            personAggregatedByCountryDto.setGender(person.getGender().getName());
            personAggregatedByCountryDto.setName(person.getName());

            personAggregatedByCountryDto.setLast_name(person.getLast_name());
            personAggregatedByCountryDto.setAdditonal_info(person.getAdditional_info());


            personDtos.add(personAggregatedByCountryDto);

        }

    }

    public List<PersonAggregatetedByGenderDto> getPersonCountryAggregatedGender(String country) {
        List<Person> persons = personRepository.findByCountriesName(country);

        if (persons.size() <= 0) {
            throw new com.bi.service.exception.ResourceNotFoundException();
        }

        List<PersonAggregatetedByGenderDto> personAggregatetedByGenderDto = new ArrayList<>();

        getListOfPersonAggregatedGender(persons, personAggregatetedByGenderDto);

        return personAggregatetedByGenderDto;
    }


    private void getListOfPersonAggregatedGender(List<Person> persons, List<PersonAggregatetedByGenderDto> personAggregatedByGenderDtos) {

        for (Person person : persons) {

            PersonAggregatetedByGenderDto personAggregatetedByGenderDto = new PersonAggregatetedByGenderDto();

            personAggregatetedByGenderDto.setId(person.getId());
            personAggregatetedByGenderDto.setName(person.getName());
            personAggregatetedByGenderDto.setLast_name(person.getLast_name());
            personAggregatetedByGenderDto.setAdditonal_info(person.getAdditional_info());

            personAggregatedByGenderDtos.add(personAggregatetedByGenderDto);

        }
    }


    private boolean isDuplicatedInListOfCountry(Integer id) {
        Person personFind = personRepository.findOne(id);
        PersonDto personDto = new PersonDto();

        for (Country country : personFind.getCountries()) {
            personDto.getCountry().add(country.getName());

        }

        Set<String> uniqueSetCountry = new HashSet<String>(personDto.getCountry());

        if (uniqueSetCountry.size() != personDto.getCountry().size()) {
            return false;
        }

        return true;
    }

}

