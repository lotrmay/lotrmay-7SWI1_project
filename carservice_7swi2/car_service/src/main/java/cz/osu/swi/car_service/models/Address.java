package cz.osu.swi.car_service.models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "address")
public class Address implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id",unique=true,nullable = false)
    private long id;

    @Column(name = "city",nullable = false,length = 30)
    private String city;

    @Column(name = "street",nullable = false,length = 20)
    private String street;

    @Column(name = "street_number",nullable = false,length = 15)
    private String streetNumber;

    @Column(name = "post_code",nullable = false,length = 15)
    private String postCode;

    @ManyToOne
    @JoinColumn(name = "id_state")
    private State state;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Address() {
    }

    public Address(String city, String street, String streetNumber, String postCode, State state) {
        this.city = city;
        this.street = street;
        this.streetNumber = streetNumber;
        this.postCode = postCode;
        this.state = state;
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", streetNumber='" + streetNumber + '\'' +
                ", postCode='" + postCode + '\'' +
                ", state=" + state +
                '}';
    }
}
