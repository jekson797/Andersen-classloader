package by.andersenlab.classloader;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

public class JarClassLoader extends URLClassLoader {

    private static final Logger logger = Logger.getLogger(JarClassLoader.class);
    private URL url;

    public JarClassLoader(URL url) {
        super(new URL[]{url});
        this.url = url;
    }

    public String getMainClassPath(String classNameContainsMainMethod) throws IOException, ClassNotFoundException {
        URL jarUrl = new URL("jar", "", url + "!/");
        logger.info("Захошу в указанный jar файл: " + jarUrl);
        JarURLConnection connection = (JarURLConnection) jarUrl.openConnection();
        logger.info("Поиск класса: " + classNameContainsMainMethod);
        JarFile jarFile = connection.getJarFile();
        return jarFile.stream().filter(entry -> entry.getName().contains(classNameContainsMainMethod)).
                map(entry -> entry.getName().replace("/", ".").replace(".class", "")).
                findFirst().orElseThrow(ClassNotFoundException::new);
    }

    public void invokeClass(String name, String[] args)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        logger.info("Загружаю класс: " + name);
        Class clazz = loadClass(name);
        logger.info("Ищу метод main внутри указанного класса");
        Method method = clazz.getMethod("main", args.getClass());
        method.setAccessible(true);
        logger.info("Проверяю метод main на соответствие требованиям");
        int modifiers = method.getModifiers();
        if (method.getReturnType() != void.class || !Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) {
            throw new NoSuchMethodException("main");
        }
        try {
            logger.info("Выполняю метод main");
            method.invoke(null, new Object[]{args});
        } catch (IllegalAccessException e) {
            logger.error(e);
        }
    }
}
