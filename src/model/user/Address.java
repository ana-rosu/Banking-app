package model.user;

import java.util.Scanner;

public class Address {
    private String street, city, county, country;

    public Address(String street, String city, String county, String country) {
        this.street = street;
        this.city = city;
        this.county = county;
        this.country = country;
    }

    public Address(Scanner in) {
        System.out.println("Street: ");
        this.street = in.nextLine();
        System.out.println("City: ");
        this.city = in.nextLine();
        System.out.println("County: ");
        this.county = in.nextLine();
        System.out.println("Country: ");
        this.country = in.nextLine();
    }

    @Override
    public String toString() {
        return street + ", " + city + ", "  + county + ", " + country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
