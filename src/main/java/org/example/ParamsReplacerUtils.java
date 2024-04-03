package org.example;

import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ParamsReplacerUtils {
    public static String generateAndReplace(String templateQuery, Map<String, DiscreteParam> params, int threadNum)  {
        Map<String, String> gen = generateValues(params, threadNum);
        return replace(templateQuery, gen);
    }

    public static String replace(String templateQuery, Map<String, String> values) {
        return new StringSubstitutor(values, "${", "}").replace(templateQuery);
    }

    public static Map<String, String> generateValues(Map<String, DiscreteParam> params, int threadNum) {
        // TODO: now threadNum is used when roundRobin is true, probably you want more general solution.
        // TODO: Also other types of params (not discrete) must be supported
        Map<String, String> generated = new HashMap<>();
        for (Map.Entry<String, DiscreteParam> entry : params.entrySet()) {
            String name = entry.getKey();
            DiscreteParam param = entry.getValue();
            generated.put(name, getValue(param, threadNum));
        }
        return generated;
    }

    private static String getValue(DiscreteParam param, int threadNum) {
        List<String> vals = param.values;
        if (param.roundRobin) {
            return vals.get(threadNum % vals.size());
        }
        // use ThreadLocalRandom to improve performance, classic Random class is poor when used in multithreaded env.
        return vals.get(ThreadLocalRandom.current().nextInt(0, vals.size()));
    }
}
