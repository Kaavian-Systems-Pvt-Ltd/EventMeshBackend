package com.eventmesh.backend.event_mesh_backend.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Service
public class Purchase_Order_Details_Service {

    @Value("${SAP.LOGIN.USERNAME}")
    String userName;

    @Value("${SAP.LOGIN.PASSWORD}")
    String password;

    public String getPurchaseOrderDetail(String updatePurchaseOrderUrlDynamically) {

        JsonParser jsonParser = new JsonParser();

        String credentials = userName + ":" + password;

        String encodeCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(updatePurchaseOrderUrlDynamically))
                    .header("Authorization", "Basic " + encodeCredentials)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("x-csrf-token", "Fetch")
                    .GET()
                    .build();

            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            int statusCode = res.statusCode();

            String resData = res.body();

            try {
                JsonObject jsonObject = jsonParser.parse(resData).getAsJsonObject();

                JsonArray jsonArray = jsonObject.getAsJsonObject("d").getAsJsonArray("results");

                if(!jsonArray.isEmpty()){

                    JsonObject result = jsonArray.get(0).getAsJsonObject();

                    String deliveryAddressCityName = result.get("DeliveryAddressCityName").getAsString();

                    System.out.println(result);
                    return deliveryAddressCityName;

                }else {
                    return "there is no array named results is in the object";
                }

            }catch (Exception e){
                return e.getMessage();
            }

        }catch (IOException | InterruptedException e){

            return e.getMessage();

        } catch (URISyntaxException e) {

            throw new RuntimeException(e);

        }

    }
}
