package unq.api.model;

import com.despegar.integration.mongo.entities.IdentifiableEntity;
import unq.api.security.HMACEncrypter;
import unq.utils.EnvConfiguration;

import java.io.Serializable;

/**
 * Created by mtejeda on 27/03/17.
 */
public class Director implements IdentifiableEntity, Serializable {


    private String id;
    private String name;
    private String lastName;
    private String email;
    private String token;


    public Director(String id, String name, String lastName, String email, String token) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.token = token;
    }
    private Director(){

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
