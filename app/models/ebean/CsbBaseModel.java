package models.ebean;

import com.google.gson.*;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import controllers.BaseController;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.UpdatedTimestamp;
import io.swagger.annotations.ApiModelProperty;
import models.AutomateCsbPagedList;
import models.ebean.business.Client;
import play.mvc.Result;
import utilities.Validation;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static play.mvc.Results.ok;

/**
 * Created by Corey Caplan on 10/2/17.
 */
@MappedSuperclass
public abstract class CsbBaseModel extends Model {

    /*
     * Fields for CsbBaseModel class
     */

    @ApiModelProperty(value = "The date at which this entity was created in the database")
    @CreatedTimestamp
    @Column(columnDefinition = "TIMESTAMP default current_timestamp")
    @Expose
    private Timestamp dateCreated;

    @ApiModelProperty(value = "The date at which this entity was last updated in the database")
    @UpdatedTimestamp
    @Column(columnDefinition = "TIMESTAMP default current_timestamp")
    @Expose
    private Timestamp dateLastUpdated;

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public Timestamp getDateLastUpdated() {
        return dateLastUpdated;
    }

    public static final String KEY_PRETTY_PRINT = "pretty_print";

    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .registerTypeAdapter(Timestamp.class, new TimestampSerializer())
            .registerTypeAdapter(Timestamp.class, new TimestampDeserializer())
            .registerTypeAdapter(Date.class, new DateSerializer())
            .registerTypeAdapter(Date.class, new DateDeserializer())
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private static final Gson GSON_PRETTY_PRINT = new GsonBuilder()
            .serializeNulls()
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .registerTypeAdapter(Timestamp.class, new TimestampSerializer())
            .registerTypeAdapter(Timestamp.class, new TimestampDeserializer())
            .registerTypeAdapter(Date.class, new DateSerializer())
            .registerTypeAdapter(Date.class, new DateDeserializer())
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    public static Serializer getSerializer(Map<String, String[]> queryString) {
        return new Serializer(Validation.bool(KEY_PRETTY_PRINT, queryString, false));
    }

    public static Deserializer getDeserializer() {
        return new Deserializer();
    }

    public static final class Serializer {

        private final boolean isPrettyPrintEnabled;

        private Serializer(boolean isPrettyPrintEnabled) {
            this.isPrettyPrintEnabled = isPrettyPrintEnabled;
        }

        public <T extends CsbBaseModel> String print(T t) {
            return isPrettyPrintEnabled ? GSON_PRETTY_PRINT.toJson(t) : GSON.toJson(t);
        }

        public <T extends CsbBaseModel> String print(List<T> t) {
            Type type = new ListTypeToken<T>().getType();
            return isPrettyPrintEnabled ? GSON_PRETTY_PRINT.toJson(t, type) : GSON.toJson(t, type);
        }

        public <T> Result printWithResult(T t) {
            return ok(isPrettyPrintEnabled ? GSON_PRETTY_PRINT.toJson(t) : GSON.toJson(t))
                    .as(BaseController.CONTENT_JSON);
        }

        public <T extends CsbBaseModel> Result printWithResult(List<T> t) {
            Type type = new ListTypeToken<T>().getType();
            return ok(isPrettyPrintEnabled ? GSON_PRETTY_PRINT.toJson(t, type) : GSON.toJson(t, type))
                    .as(BaseController.CONTENT_JSON);
        }

    }

    public static final class Deserializer {

        // No instance
        private Deserializer() {
        }

        public <T> T fromJson(String json, Class<T> clazz) {
            return GSON.fromJson(json, clazz);
        }

        public <T> List<T> fromJsonAsList(String json, Class<T[]> clazz) {
            return Arrays.asList(GSON.fromJson(json, clazz));
        }

        public <T> AutomateCsbPagedList<T> fromJsonAsPagedList(String json, Class<T> clazz) {
            return GSON.fromJson(json, new PagedListTypeToken<T>(clazz));
        }

    }

    private static final class PagedListTypeToken<T> implements ParameterizedType {

        private final Class<T> clazz;

        private PagedListTypeToken(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return AutomateCsbPagedList.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

    private static final class ListTypeToken<T> extends TypeToken<List<T>> {
    }

    private static final class ObjectTypeToken<T> extends TypeToken<T> {
    }

    private static final class ClientSerializer implements JsonSerializer<Client> {

        @Override
        public JsonElement serialize(Client src, Type typeOfSrc, JsonSerializationContext context) {
            return null;
        }

    }

    private static final class TimestampDeserializer implements JsonDeserializer<Timestamp> {

        @Override
        public Timestamp deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Timestamp(json.getAsJsonPrimitive().getAsLong());
        }

    }

    private static final class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return new Date(json.getAsJsonPrimitive().getAsLong());
        }

    }

    private static final class DateSerializer implements JsonSerializer<Date> {

        @Override
        public JsonElement serialize(Date date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.getTime());
        }

    }

    private static final class TimestampSerializer implements JsonSerializer<Timestamp> {

        @Override
        public JsonElement serialize(Timestamp timestamp, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(timestamp.getTime());
        }

    }

}
