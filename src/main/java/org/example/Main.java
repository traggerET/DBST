package org.example;


public class Main {
    public static void main(String[] args) {
        try {
            BaseConfig cfg = ConfigUtils.loadConfig(args[0]);
            TesterEngine testerEngine = new TesterEngine(cfg);
            testerEngine.run();
        }
        catch (Exception e) {
            System.out.println();
        }
    }
}
