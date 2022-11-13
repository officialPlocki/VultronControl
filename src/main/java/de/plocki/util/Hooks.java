package de.plocki.util;

import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.application.entities.PteroApplication;
import de.plocki.util.files.FileBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public class Hooks {

    private final PteroApplication pteroApplication;
    private final FileBuilder builder;

    public Hooks() {
        builder = new FileBuilder("data.yml");
        if(getFileBuilder().getYaml().isSet("panelURL") && getFileBuilder().getYaml().isSet("panelToken")) {
            pteroApplication = PteroBuilder.createApplication(builder.getYaml().getString("panelURL"), builder.getYaml().getString("panelToken"));
        } else {
            pteroApplication = null;
        }
    }

    public void deleteSubdomain(String id) throws IOException {
        Request request = Request.Delete("https://api.cloudflare.com/client/v4/zones/" + fromFile("cloudflareZoneID") + "/dns_records/" + id);
        request.setHeader("Authorization", "Bearer " + fromFile("cloudflareKey"));
        request.setHeader("Content-Type", "application/json");
        request.execute();
    }

    public String createSubdomain(String subdomain, int port, String ip) throws IOException {
        Request request = Request.Post("https://api.cloudflare.com/client/v4/zones/" + fromFile("cloudflareZoneID") + "/dns_records");
        JSONObject object = new JSONObject();
        object.put("type", "SRV");

        JSONObject data = new JSONObject();
        data.put("service", "_minecraft");
        data.put("proto", "_tcp");
        data.put("name", subdomain);
        data.put("priority", 10);
        data.put("weight", 10);
        data.put("port", port);
        data.put("target", ip);

        object.put("data", data);
        request.bodyString(object.toString(), ContentType.APPLICATION_JSON);
        request.setHeader("Authorization", "Bearer " + fromFile("cloudflareKey"));
        request.setHeader("Content-Type", "application/json");
        HttpResponse response = request.execute().returnResponse();
        JSONObject obj = new JSONObject(new BasicResponseHandler().handleResponse(response));
        if(obj.getBoolean("success")) {
            JSONObject result = obj.getJSONObject("result");
            return result.getString("id");
        } else return null;
    }

    public PteroApplication getPteroApplication() {
        return pteroApplication;
    }

    public FileBuilder getFileBuilder() {
        return builder;
    }

    public String fromFile(String key) {
        return builder.getYaml().getString(key);
    }

    public void toFile(String key, String val) {
        builder.getYaml().set(key, val);
        builder.save();
    }
}
