package cafe.adriel.nmsalphabet.model;

import com.parse.ParseUser;

import java.io.Serializable;

import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.util.Util;

public class User extends ParseUser implements Serializable {

    public String getName(){
        return getString("name");
    }

    public void setName(String name){
        put("name", name);
    }

    public String getGender(){
        String gender = getString("gender");
        return Util.isEmpty(gender) ? Constant.GENDER_MALE : gender;
    }

    public void setGender(String gender){
        put("gender", gender);
    }

}