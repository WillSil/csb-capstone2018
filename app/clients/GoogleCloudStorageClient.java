package clients;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import play.Logger;
import play.libs.Json;

import javax.xml.bind.DatatypeConverter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Collection;
import java.util.Collections;

public class GoogleCloudStorageClient {

    private static final Logger.ALogger logger = Logger.of(GoogleCloudStorageClient.class);
    private static final String STORAGE_SCOPE = "https://www.googleapis.com/auth/devstorage.read_write";

    private final String BUCKET_NAME = "automate-csb-165818.appspot.com";
    private Storage storage;


    public GoogleCloudStorageClient() {
        this.storage = StorageOptions.newBuilder()
                .setCredentials(getCredentialsFromFile())
                .build()
                .getService();
    }

    private Credentials getCredentialsFromFile() {
        JsonNode node;
        try {
            node = Json.parse(new FileInputStream("conf/cloud-storage-account.json"));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        String clientId = node.get("client_id").asText();
        String clientEmail = node.get("client_email").asText();
        String rawPrivateKey = node.get("private_key").asText();

        String privateKeyId = node.get("private_key_id").asText();
        Collection<String> scopes = Collections.singletonList(STORAGE_SCOPE);
        try {
            return ServiceAccountCredentials.fromPkcs8(clientId, clientEmail, rawPrivateKey, privateKeyId, scopes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Blob downloadFileFromBucket(String fileId) {
        try {
            return storage.get(BUCKET_NAME).get(fileId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Blob uploadFileToBucket(String fileId, byte[] bytes, String contentType) {
        try {
            return storage.get(BUCKET_NAME).create(fileId, bytes, contentType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}