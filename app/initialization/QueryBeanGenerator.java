package initialization;

import io.ebean.typequery.generator.Generator;
import io.ebean.typequery.generator.GeneratorConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * Created by Corey Caplan on 10/29/17.
 */
public class QueryBeanGenerator {

    private static final String BASE_DIRECTORY = "./target/scala-2.11/routes/main/";
    private static final String BASE_PACKAGE_AS_PATH = "models" + File.separator + "ebean";

    public static void main(String[] args) {
        try {
            generateQueryBeans();
        } catch (Exception e) {
            e.printStackTrace();
        }

        copyQueryBeansToGeneratedOutputDirectory();
    }

    private static void generateQueryBeans() throws Exception {
        File directory = new File("./app/models/ebean");

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        Arrays.stream(files)
                .forEach(file -> {
                    if (file.isDirectory()) {
                        GeneratorConfig config = new GeneratorConfig();
                        config.setClassesDirectory("./target/scala-2.11/classes");
                        config.setDestDirectory("./target/scala-2.11/classes");
                        config.setDestResourceDirectory("./conf");
                        config.setEntityBeanPackage("models.ebean." + file.getName());

                        try {
                            Generator generator = new Generator(config);
                            generator.generateQueryBeans();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    private static void copyQueryBeansToGeneratedOutputDirectory() {
        File directory = new File("./target/scala-2.11/classes/models/ebean");
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        listFiles(files);
    }

    private static void listFiles(File[] files) {
        Arrays.stream(files).forEach(file -> {
            if (file.isDirectory()) {
                File copiedDirectory = new File(getFileName(file.getAbsolutePath()));
                copiedDirectory.mkdirs();
                File[] children = file.listFiles();
                if (children != null) {
                    listFiles(children);
                }
            }

            if (file.getPath().endsWith(".java")) {
                File destination = new File(getFileName(file.getAbsolutePath()));

                writeFile(file, destination);
                file.delete();
            }
        });
    }

    private static String getFileName(String absoluteName) {
        int index = absoluteName.lastIndexOf(BASE_PACKAGE_AS_PATH);
        return BASE_DIRECTORY + absoluteName.substring(index).replace("\\", "/");
    }

    private static void writeFile(File originalFile, File destinationFile) {
        FileOutputStream stream = null;
        try {
            if(destinationFile.exists() && !destinationFile.delete()) {
                throw new IOException("Could not delete old file: " + destinationFile.getName());
            }
            if (!destinationFile.createNewFile()) {
                throw new IOException("Could not create new file: " + destinationFile.getName());
            }
            stream = new FileOutputStream(destinationFile, false);
            stream.write(Files.readAllBytes(originalFile.toPath()));
            stream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
