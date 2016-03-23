package server;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class MyIntTypeAdapter extends TypeAdapter<Integer>{
	
	@Override
    public Integer read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return new Integer(0);
        }
        String stringValue = reader.nextString();
        try {
            return Integer.valueOf(stringValue);
        } catch (NumberFormatException e) {
            return new Integer(0);
        }
    }

    @Override
    public void write(JsonWriter writer, Integer value) throws IOException {
        if (value == null) {
            writer.value(0);
            return;
        }
        writer.value(value);
    }

}
