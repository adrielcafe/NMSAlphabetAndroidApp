package cafe.adriel.nmsalphabet.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.io.Serializable;

@ParseClassName("AlienRace")
public class AlienRace extends ParseObject implements Serializable {

    public String getName(){
        return getString("name");
    }

    public void setName(String name){
        put("name", name);
    }

    public String getColor(){
        return getString("color");
    }

    public void setColor(String color){
        put("color", color);
    }

}