package com.example.temp;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * @author suhai
 * @since 2023-05-10
 */
public class ReadPropertiesTest {

    /**
     * 1. 方式一
     * 从当前的类加载器的getResourcesAsStream来获取
     * InputStream inputStream = this.getClass().getResourceAsStream(name)
     *
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream("jdbc.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        properties.list(System.out);
        System.out.println("==============================================");
        String property = properties.getProperty("jdbc.url");
        System.out.println("property = " + property);
    }

    /**
     * 2. 方式二
     * 从当前的类加载器的getResourcesAsStream来获取
     * InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(name)
     *
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config/application.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        properties.list(System.out);
        System.out.println("==============================================");
        String property = properties.getProperty("minio.endpoint");
        System.out.println("property = " + property);
    }

    /**
     * 3. 方式三
     * 使用Class类的getSystemResourceAsStream方法 和使用当前类的ClassLoader是一样的
     * InputStream inputStream = ClassLoader.getSystemResourceAsStream(name)
     *
     * @throws IOException
     */
    @Test
    public void test3() throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("config/application.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        properties.list(System.out);
        System.out.println("==============================================");
        String property = properties.getProperty("minio.endpoint");
        System.out.println("property = " + property);
    }

    /**
     * 4. 方式四
     * 使用Spring-core包中的ClassPathResource读取
     * Resource resource = new ClassPathResource(path)
     *
     * @throws IOException
     */
    @Test
    public void test4() throws IOException {
        Resource resource = new ClassPathResource("config/application.properties");
        Properties properties = PropertiesLoaderUtils.loadProperties(resource);
        properties.list(System.out);
        System.out.println("==============================================");
        String property = properties.getProperty("minio.endpoint");
        System.out.println("property = " + property);
    }

    /**
     * 5. 方式五
     * 从文件中获取,使用InputStream字节,主要是需要加上当前配置文件所在的项目src目录地址。路径配置需要精确到绝对地址级别
     * BufferedInputStream继承自InputStream
     * InputStream inputStream = new BufferedInputStream(new FileInputStream(name)
     * 这种方法读取需要完整的路径，优点是可以读取任意路径下的文件，缺点是不太灵活
     * @throws IOException
     */
    @Test
    public void test5() throws IOException {
        InputStream inputStream = new BufferedInputStream(new FileInputStream("src/main/resources/config/application.properties"));
        Properties properties = new Properties();
        properties.load(inputStream);
        properties.list(System.out);
        System.out.println("==============================================");
        String property = properties.getProperty("minio.endpoint");
        System.out.println("property = " + property);
    }

    /**
     * 6. 方式六
     * 从文件中获取,使用InputStream字节,主要是需要加上当前配置文件所在的项目src目录地址。路径配置需要精确到绝对地址级别
     * FileInputStream继承自InputStream
     * InputStream inputStream = new FileInputStream(name)
     * 这种方法读取需要完整的路径，优点是可以读取任意路径下的文件，缺点是不太灵活
     * @throws IOException
     */
    @Test
    public void test6() throws IOException {
        InputStream inputStream = new FileInputStream("src/main/resources/config/application.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        properties.list(System.out);
        System.out.println("==============================================");
        String property = properties.getProperty("minio.endpoint");
        System.out.println("property = " + property);
    }

    /**
     * 7. 方式七
     * 使用InputStream流来进行操作ResourceBundle，获取流的方式由以上几种。
     * ResourceBundle resourceBundle = new PropertyResourceBundle(inputStream);
     * @throws IOException
     */
    @Test
    public void test7() throws IOException {
        InputStream inputStream = ClassLoader.getSystemResourceAsStream("config/application.properties");
        ResourceBundle resourceBundle = new PropertyResourceBundle(inputStream);
        Enumeration<String> keys = resourceBundle.getKeys();
        while (keys.hasMoreElements()) {
            String s = keys.nextElement();
            System.out.println(s + " = " + resourceBundle.getString(s));
        }
    }
}
