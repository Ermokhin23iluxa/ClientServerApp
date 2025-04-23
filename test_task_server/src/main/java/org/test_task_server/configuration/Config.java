package org.test_task_server.configuration;

import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();

    static {
        try(InputStream in = Config.class.getClassLoader().getResourceAsStream("server.properties")){
            if (in!=null){
                props.load(in);

            }else{
                System.err.println("server.properties не найден! Используются настройки по умолчанию.");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static int getPort(){
        try{
            return Integer.parseInt(props.getProperty("port","8288"));
        }catch (NumberFormatException e){
            return 8288;
        }
    }

}