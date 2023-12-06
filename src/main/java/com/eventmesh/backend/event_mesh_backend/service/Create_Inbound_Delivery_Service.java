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
public class Create_Inbound_Delivery_Service {

    @Value("${URL.POST.TO.CREATE.INBOUND.DELIVERY.DOCUMENT}")
    String create_inbound_delivery_url;

    @Value("${SAP.LOGIN.USERNAME}")
    String userName;

    @Value("${SAP.LOGIN.PASSWORD}")
    String password;

    public String CreateInboundDocument() {

        JsonParser jsonParser = new JsonParser();

        String credentials = userName + ":" + password;

        String encodeCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        String reqBody = "{"
                + "\"Supplier\":\"17300001\","
                + "\"to_DeliveryDocumentItem\": {"
                + "\"results\": ["
                + "{"
                + "\"ActualDeliveryQuantity\":\"1\","
                + "\"DeliveryQuantityUnit\": \"PC\","
                + "\"Material\": \"TG0011\","
                + "\"Plant\": \"1710\","
                + "\"ReferenceSDDocument\": \"4500000002\","
                + "\"ReferenceSDDocumentItem\": \"000010\","
                + "\"to_DocumentFlow\": {"
                + "\"results\": ["
                + "{"
                + "\"QuantityInBaseUnit\": \"1\""
                + "}"
                + "]"
                + "}"
                + "}"
                + "]"
                + "},"
                + "\"to_DeliveryDocumentPartner\": {"
                + "\"results\": ["
                + "{"
                + "\"to_Address\": {}"
                + "}"
                + "]"
                + "}"
                + "}";
        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(create_inbound_delivery_url))
                    .header("Authorization", "Basic " + encodeCredentials)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();
            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            int StatusCode = res.statusCode();

            String reqRes = res.body();

            System.out.println(reqRes + StatusCode);

            return reqRes;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e){

            return  e.getMessage();

        }
    }
}