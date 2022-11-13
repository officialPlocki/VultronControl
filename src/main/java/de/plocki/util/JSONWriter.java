package de.plocki.util;

import com.google.gson.Gson;
import de.plocki.ai.objects.SupportType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;

public class JSONWriter {

    public void write(Object info) throws IOException {
        String everything = "";
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
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();
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
        array.put(new JSONObject(new Gson().toJson(info)));
        object.put("array", array);
        PrintWriter writer = new PrintWriter("brain");
        writer.write(object.toString());
        writer.flush();
        writer.close();
    }

    public HashMap<String, SupportType> read() throws IOException {
        HashMap<String, SupportType> map = new HashMap<>();
        String everything = "";
        BufferedReader br = new BufferedReader(new FileReader("brain"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();
        } finally {
            br.close();
        }
        JSONObject object = new JSONObject(everything);
        JSONArray array = object.getJSONArray("array");
        for(int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String oT = obj.getString("objectType");




            if(oT.equalsIgnoreCase("category")) {
                String words = obj.getString("text");
                SupportType type = SupportType.valueOf(obj.getString("type"));
                map.put(words, type);
            }



        }
        return map;
    }

}
