package me.ericjohns55.cryptography.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Provides an adapter to serialize MacroItems when saving full Macros to GSON
 * This can be applied to other abstract classes, but for the purpose of this project
 * it will only be used for serializing and deserializing MacroItems
 *
 * @author Eric Johns
 */

public class GsonMacroAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    @Override
    public final JsonElement serialize(final T object, final Type interfaceType,
                                       final JsonSerializationContext context)  {
        JsonObject result = new JsonObject();

        // saving the exact class name and its properties in a separate attribute
        // ensures that there will be no issues in deserializing with the abstract
        // MacroItem class
        result.add("type", new JsonPrimitive(object.getClass().getSimpleName()));
        result.add("properties", context.serialize(object, object.getClass()));
        return result;
    }

    @Override
    public final T deserialize(final JsonElement elem, final Type interfaceType, final JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject object = elem.getAsJsonObject();
        String type = object.get("type").getAsString();
        JsonElement element = object.get("properties");

        try {
            return context.deserialize(element,
                    Class.forName("me.ericjohns55.cryptography.macros." + type));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
