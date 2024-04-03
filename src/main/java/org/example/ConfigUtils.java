package org.example;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * HEre
 */
public class ConfigUtils {
    public static BaseConfig loadConfig(String pathToConfig) throws FileNotFoundException {
        Yaml yaml = new Yaml(new Constructor(BaseConfig.class, new LoaderOptions()));
        InputStream inputStream = new FileInputStream(pathToConfig);

        BaseConfig cfg = yaml.load(inputStream);
        return cfg;
    }
}
