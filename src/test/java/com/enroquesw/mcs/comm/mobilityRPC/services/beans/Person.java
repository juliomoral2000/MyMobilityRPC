package com.enroquesw.mcs.comm.mobilityRPC.services.beans;

import java.util.List;

/**
 * Created by Julio on 19/01/2016.
 */
public class Person implements Comparable<Person> {
    long personId;
    String firstName;
    String lastName;
    List<String> phoneNumbers;
    int houseNumber;
    String street;
    String city;
    String country;

    public Person(long personId, String firstName, String lastName, List<String> phoneNumbers, int houseNumber, String street, String city, String country) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumbers = phoneNumbers;
        this.houseNumber = houseNumber;
        this.street = street;
        this.city = city;
        this.country = country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return personId == person.personId;
    }

    @Override
    public int hashCode() {
        return (int) (personId ^ (personId >>> 32));
    }

    @Override
    public String toString() {
        return "Person{".concat("personId=").concat(String.valueOf(personId)).concat("}");
    }

    @Override
    public int compareTo(Person o) {
        return (personId > o.personId)? 1 : (personId < o.personId) ? -1 : 0;
    }
}

