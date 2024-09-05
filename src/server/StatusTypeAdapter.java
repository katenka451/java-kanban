package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import model.Status;

import java.io.IOException;

public class StatusTypeAdapter extends TypeAdapter<Status> {

    @Override
    public void write(final JsonWriter jsonWriter, final Status status) throws IOException {
        jsonWriter.value(status.toString());
    }

    @Override
    public Status read(final JsonReader jsonReader) throws IOException {
        return Status.valueOf(jsonReader.nextString());
    }
}
