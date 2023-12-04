package com.eventmesh.backend.event_mesh_backend.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Service
public class Supplier_Address_Service {

    @Value("${SAP.LOGIN.USERNAME}")
    String userName;

    @Value("${SAP.LOGIN.PASSWORD}")
    String password;

    public String getSupplierAddress(String updateSupplierOrderAddressUrlDynamically) {

        JsonParser jsonParser = new JsonParser();

        String credentials = userName + ":" + password;

        String encodeCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        try {

            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(updateSupplierOrderAddressUrlDynamically))
                    .header("Authorization", "Basic " + encodeCredentials)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("x-csrf-token", "Fetch")
                    .GET()
                    .build();

            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            int StatusCode = res.statusCode();

            String resData = res.body();

            try {
                JsonObject jsonObject = jsonParser.parse(resData).getAsJsonObject();

                if(jsonObject.has("d")){

                    JsonObject dObject = jsonObject.getAsJsonObject("d");

                    if(dObject.has("AddressCityName")){

                        String supplierAddress = dObject.get("AddressCityName").getAsString();

                        return supplierAddress;
                    }else {
                        return "No Supplier City is been Found";
                    }
                }else {
                    return "property d is not available";
                }
            }catch (Exception e) {
                return  e.getMessage();
            }

        }catch (IOException | InterruptedException e){

            return e.getMessage();

        } catch (URISyntaxException e) {

            throw new RuntimeException(e);

        }

    }
}
