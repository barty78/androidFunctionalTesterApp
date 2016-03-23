package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

class MyFloatTypeAdapter extends TypeAdapter<Float>{
	
	@Override
    public Float read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return new Float(0);
        }
        String stringValue = reader.nextString();
        try {
            return Float.valueOf(stringValue);
        } catch (NumberFormatException e) {
            return new Float(0);
        }
    }

    @Override
    public void write(JsonWriter writer, Float value) throws IOException {
        if (value == null) {
            writer.value(0);
            return;
        }
        writer.value(value);
    }

}
