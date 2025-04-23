package org.test_task_client.configuration;

import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();

    static {
        // try with resources
        try(InputStream in = Config.class.getClassLoader().getResourceAsStream("client.properties")){
            if (in!=null){
                props.load(in);

            }else{
                System.err.println("client.properties не найден! Используются настройки по умолчанию.");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public static String getHost(){
        return props.getProperty("host","localhost");
    }
    public static int getPort(){
        try{
            return Integer.parseInt(props.getProperty("port","8288"));
        }catch (NumberFormatException e){
            return 8288;
        }
    }

}
