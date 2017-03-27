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
    private String nombre;
    private String apellido;
    private String email;
    private String token; //es la clave hasheada



    public Director(String nombre, String apellido, String email, String token) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.token = token;
    }



    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
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
        String encryptedToken = HMACEncrypter.encrypt(token, EnvConfiguration.configuration.getString("encryption-key"));
        this.token = encryptedToken;
    }

}
