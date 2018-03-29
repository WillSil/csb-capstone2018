package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.Example;
import models.ControllerComponent;
import play.mvc.Controller;
import utilities.Validation;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;

public abstract class BaseController extends Controller {

    static final String KEY_PAGE = "page";
    static final String KEY_ASCENDING = "ascending";
    static final String KEY_SORT_BY = "sort_by";
    static final String KEY_IS_OR_FILTER = "is_or_filter";

    public static final String CONTENT_JSON = "application/json";

    static final String KEY_GET = "GET";
    static final String KEY_POST = "POST";

    private final ControllerComponent controllerComponent;

    private static final String KEY_SERIALIZER = "serializer";

    @Inject
    public BaseController(ControllerComponent controllerComponent) {
        this.controllerComponent = controllerComponent;
    }

    public ControllerComponent getControllerComponent() {
        return controllerComponent;
    }

    public Executor getContext() {
        return getControllerComponent().getContext();
    }

    static <T> CompletionStage<T> wrapInFuture(T t) {
        return CompletableFuture.completedFuture(t);
    }

    /**
     * @param key         The key that will be used to extract the given data from the query params
     * @param queryParams The query params from which the key's value will be extracted
     * @return The value that maps to the provided key or null
     */
    String getStringFromQueryParams(String key, Map<String, String[]> queryParams) {
        return Optional.ofNullable(Validation.string(key, queryParams))
                .map(String::trim)
                .orElse(null);
    }

    /**
     * @param key        The key that will be used to extract the given data from the JSON
     * @param jsonNode   The JSON from which the key's value will be extracted
     * @param shouldTrim True to force trimming on the value and setting it to null if it's just whitespace, or false to
     *                   set any string (even whitespace) as non-null
     * @return The value that maps to the provided key or null
     */
    String getStringFromJson(String key, JsonNode jsonNode, boolean shouldTrim) {
        return Optional.ofNullable(Validation.string(key, jsonNode, shouldTrim))
                .map(String::trim)
                .orElse(null);
    }


}
