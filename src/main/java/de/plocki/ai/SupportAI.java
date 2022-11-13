package de.plocki.ai;

import com.google.gson.Gson;
import de.plocki.ai.objects.CategoryObject;
import de.plocki.ai.objects.CategoryObjectResult;
import de.plocki.ai.objects.SupportType;
import de.plocki.util.JSONWriter;
import info.debatty.java.stringsimilarity.JaroWinkler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class SupportAI {

    public HashMap<String, CategoryObjectResult> results = new HashMap<>();
    private HashMap<String, SupportType> read;

    public SupportAI() throws IOException {
        read = new JSONWriter().read();
        System.gc();

        File file2 = new File("learn.txt");
        List<CategoryObject> objects = new ArrayList<>();
        List<String> sb = new ArrayList<>();
        if(file2.exists()) {
            try {
                BufferedReader br;
                try {
                    br = new BufferedReader(new FileReader(file2));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                try {
                    String line;
                    try {
                        line = br.readLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    while (line != null) {
                        sb.add(line);
                        line = br.readLine();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                JaroWinkler sim = new JaroWinkler();
                int i = 0;
                int size = sb.size();
                for(String str : sb) {
                    System.out.println(i++ + " / " + size);
                    HashMap<SupportType, Integer> points = new HashMap<>();
                    for(SupportType type : SupportType.values()) {
                        points.put(type, 0);
                    }
                    for(String s : read.keySet()) {
                        if(sim.similarity(s, str) > 0.70) {
                            points.put(read.get(s), points.getOrDefault(read.get(s), 0) + 1);
                        } else if(sim.similarity(s, str) < 0.4) {
                            points.put(SupportType.no_support, 50);
                        }
                    }
                    int l = Collections.max(points.values());
                    for(SupportType type : points.keySet()) {
                        if(points.get(type) == l) {
                            objects.add(new CategoryObject(str, type));
                        }
                    }
                }
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }




            String everything;
            File file = new File("brain");
            if(!file.exists()) {
                file.createNewFile();
                JSONObject object = new JSONObject();
                object.put("array", new JSONArray());

                String src = object.toString();
                PrintWriter writer = new PrintWriter("brain");
                writer.write(src);
                writer.flush();
                writer.close();
            }
            BufferedReader br = new BufferedReader(new FileReader("brain"));
            try {
                StringBuilder sbe = new StringBuilder();
                String line = br.readLine();

                while (line != null) {
                    sbe.append(line);
                    sbe.append(System.lineSeparator());
                    line = br.readLine();
                }
                everything = sbe.toString();
            } finally {
                br.close();
            }
            JSONObject object = new JSONObject(everything);
            JSONArray array;
            if(!object.has("array")) {
                array = new JSONArray();
            } else {
                array = object.getJSONArray("array");
                object.remove("array");
            }

            for(CategoryObject obj : objects) {
                array.put(new JSONObject(new Gson().toJson(obj)));
            }
            object.put("array", array);
            PrintWriter writer = new PrintWriter("brain");
            writer.write(object.toString());
            writer.flush();
            writer.close();
        }
    }

    public SupportType find(String words) {
        JaroWinkler sim = new JaroWinkler();
        HashMap<SupportType, Integer> points = new HashMap<>();
        for(SupportType type : SupportType.values()) {
            points.put(type, 0);
        }
        for(String str : read.keySet()) {
            if(sim.similarity(words, str) > 0.50) {
                points.put(read.get(str), points.getOrDefault(read.get(str), 0) + 1);
            }
        }
        int l = Collections.max(points.values());
        for(SupportType type : points.keySet()) {
            if(points.get(type) == l) {

                try {
                    new JSONWriter().write(new CategoryObject(words, type));
                    read = new JSONWriter().read();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                points.clear();
                results.clear();
                return type;
            }
        }
        results.clear();
        points.clear();
        return SupportType.general;
    }

}
