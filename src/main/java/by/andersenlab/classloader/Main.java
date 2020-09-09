package by.andersenlab.classloader;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.URL;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            URL url = new URL("file:C:/Users/jekso/IdeaProjects/Andersen-classloader/src/main/java/" +
                    "by/andersenlab/classloader/jar/andersen-shop.jar");
            JarClassLoader classLoader = new JarClassLoader(url);
            String mainClassPath = classLoader.getMainClassPath("Main");
            classLoader.invokeClass(mainClassPath, new String[]{});
        } catch (ClassNotFoundException ex) {
            logger.error(ex + " Не удалось найти указанный класс");
        } catch (ReflectiveOperationException | IOException e) {
            logger.error(e);
        }
    }
}
