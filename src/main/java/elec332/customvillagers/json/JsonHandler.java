package elec332.customvillagers.json;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.util.List;

/**
 * Created by Elec332 on 1-7-2015.
 */
public class JsonHandler {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    private static final JsonParser parser = new JsonParser();

    public static Gson getGson(){
        return gson;
    }

    public static JsonParser getJsonParser(){
        return parser;
    }

    public static JsonWriter newJsonWriter(File file) throws IOException{
        if (!file.exists())
            throw new FileNotFoundException();
        JsonWriter jsonWriter = new JsonWriter(new FileWriter(file));
        jsonWriter.setIndent("  ");
        return jsonWriter;
    }

    public static JsonObject newJsonObject(){
        return new JsonObject();
    }

    public static JsonObject newJsonObject(JsonElement jsonElement, String s){
        JsonObject jsonObject = newJsonObject();
        jsonObject.add(s, jsonElement);
        return jsonObject;
    }

    public static JsonObject newJsonObject(JsonEntryData... data){
        JsonObject jsonObject = newJsonObject();
        for (JsonEntryData entryData : data)
            jsonObject.add(entryData.name, entryData.element);
        return jsonObject;
    }

    public static JsonEntryData newJsonEntry(String identifier, JsonElement jsonElement){
        return new JsonEntryData(jsonElement, identifier);
    }

    public static JsonObject addToObject(JsonObject main, JsonElement element, String s){
        main.add(s, element);
        return main;
    }

    public static JsonArray newJsonArray(){
        return new JsonArray();
    }

    public static JsonObject getMainFileObject(File file) throws FileNotFoundException{
        return (new JsonParser()).parse(new FileReader(file)).getAsJsonObject();
    }

    public static void toFile(File file, JsonEntryData... data){
        toFile(file, newJsonObject(data));
    }

    public static void toFile(File file, JsonObject jsonObject){
        try {
            if (!file.exists())
                createFile(file);
            toFile(newJsonWriter(file), jsonObject);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void toFile(JsonWriter writer, JsonEntryData... data) throws IOException{
        toFile(writer, newJsonObject(data));
    }

    public static void toFile(JsonWriter writer, JsonObject jsonObject) throws IOException{
        gson.toJson(jsonObject, writer);
        writer.close();
    }

    public static <T extends Serializable> List<T> getDataAsList(JsonObject jsonObject, String s, Class<T> clazz){
        List<T> ret = Lists.newArrayList();
        if (jsonObject.has(s) && jsonObject.get(s).isJsonArray()){
            for (JsonElement element : ((JsonArray)jsonObject.get(s))){
                ret.add(gson.fromJson(element, clazz));
            }
        }
        return ret;
    }

    public static boolean createFile(File file){
        try {
            return file.createNewFile();
        } catch (IOException e){
            return false;
        }
    }

    public static <T extends Serializable> JsonElement toJsonElement(T t, Class<T> clazz){
        return parser.parse(gson.toJson(t, clazz));
    }

    public static <T extends Serializable> JsonArray toJsonArray(List<T> l, Class<T> clazz){
        JsonArray jsonArray = newJsonArray();
        for (T t : l)
            jsonArray.add(toJsonElement(t, clazz));
        return jsonArray;
    }

    public static class JsonEntryData{
        private JsonEntryData(JsonElement element, String name){
            this.element = element;
            this.name = name;
        }

        private String name;
        private JsonElement element;
    }

}
