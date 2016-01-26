package server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.pietrantuono.ioioutils.Units;

import java.lang.reflect.Type;

/**
 * Created by Maurizio Pietrantuono, maurizio.pietrantuono@gmail.com.
 */
@SuppressWarnings("unused")
public class CurrentDeserializer implements JsonDeserializer<Integer> {
    @Override
    public @Units
    Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jobject = (JsonObject) json;
        String units=jobject.get("units").getAsString();
        if(units==null)return Units.NULL;
        switch (units){
            case "mA":
                return Units.mA;
            case "uA":
                return Units.uA;
            case "nA":
                return Units.nA;
            case "V":
                return Units.V;
            case "%":
                return Units.percent;
            default:
                return Units.NULL;
        }
    }
}
