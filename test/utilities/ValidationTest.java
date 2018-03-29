package utilities;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import play.libs.Json;

import static org.junit.Assert.*;

/**
 * Created by Corey Caplan on 12/4/17.
 */
public class ValidationTest {
    @Test
    public void page() throws Exception {
    }

    @Test
    public void integer() throws Exception {
    }

    @Test
    public void integer1() throws Exception {
    }

    @Test
    public void integer2() throws Exception {
    }

    @Test
    public void string() throws Exception {
    }

    @Test
    public void getLong() throws Exception {
        long value = 123123L;
        JsonNode node = Json.newObject()
                .put("hello", value);

        assertEquals(value, Validation.getLong("hello", node));
    }

    @Test
    public void bool() throws Exception {
    }

    @Test
    public void bool1() throws Exception {
    }

    @Test
    public void bool2() throws Exception {
    }

    @Test
    public void bool3() throws Exception {
    }

    @Test
    public void string1() throws Exception {
    }

    @Test
    public void isEmpty() throws Exception {
    }

    @Test
    public void isEmailValid() throws Exception {
    }

}