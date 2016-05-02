package cafe.adriel.nmsalphabet.model;

import com.parse.ParseUser;

import java.io.Serializable;

public class User extends ParseUser implements Serializable {

    public String getName(){
        return getString("name");
    }

    public void setName(String name){
        put("name", name);
    }

    public String getFacebookUserId() {
        return getString("facebookUserId");
    }

    public void setFacebookUserId(String facebookUserId){
        put("facebookUserId", facebookUserId);
    }

}