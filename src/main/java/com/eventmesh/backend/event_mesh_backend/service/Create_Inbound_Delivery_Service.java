package com.eventmesh.backend.event_mesh_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
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

    @Value("${URL.POST.TO.CREATE.INBOUND.DELIVERY.SDK.DOCUMENT}")
    String create_inbound_delivery_url;

    @Value("${SAP.SMART.LOGIN.USERNAME}")
    String userName;

    @Value("${SAP.SMART.LOGIN.PASSWORD}")
    String password;

    public String CreateInboundDocument( String purchaseOrderId, String frightVendorData) {

        System.out.println(purchaseOrderId);

        Gson gson = new Gson();

        String credentials = userName + ":" + password;

        String encodeCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        java.util.Map<String, Object> frightVendorJsonData = gson.fromJson(frightVendorData, java.util.Map.class);

        System.out.println(frightVendorJsonData);

        JsonObject reqJson = new JsonObject();

        reqJson.addProperty("Supplier", "17300001");

//        JsonObject toDeliveryDocumentText = new JsonObject();
//        JsonArray toDeliveryDocumentTextArray = new JsonArray();
//        JsonObject toDeliveryDocumentTextData = new JsonObject();
//
//        toDeliveryDocumentTextData.addProperty("deliveryindays", frightVendorJsonData.get("deliveryindays").toString());
//        toDeliveryDocumentTextData.addProperty("freightvendorname", frightVendorJsonData.get("freightvendorname").toString());
//        toDeliveryDocumentTextData.addProperty("contact", frightVendorJsonData.get("contact").toString());
//        toDeliveryDocumentTextData.addProperty("pickuplocation", frightVendorJsonData.get("pickuplocation").toString());
//        toDeliveryDocumentTextData.addProperty("destinationlocation", frightVendorJsonData.get("destinationlocation").toString());
//        toDeliveryDocumentTextData.addProperty("price", frightVendorJsonData.get("price").toString());
//        toDeliveryDocumentTextData.addProperty("freightvendorid", frightVendorJsonData.get("freightvendorid").toString());
//        toDeliveryDocumentTextArray.add(toDeliveryDocumentTextData);
//        toDeliveryDocumentText.add("results", toDeliveryDocumentTextArray);
//
//        reqJson.add("to_DeliveryDocumentText", toDeliveryDocumentText);

        JsonObject reqToDocumentItemEntry = new JsonObject();
        JsonObject reqToDocumentItemData = new JsonObject();
        JsonArray reqToDocumentItemArray = new JsonArray();

        reqToDocumentItemData.addProperty("ActualDeliveryQuantity", "1");
        reqToDocumentItemData.addProperty("DeliveryQuantityUnit", "PC");
        reqToDocumentItemData.addProperty("Material", "TG0011");
        reqToDocumentItemData.addProperty("Plant", "1710");
        reqToDocumentItemData.addProperty("ReferenceSDDocument", purchaseOrderId);
        reqToDocumentItemData.addProperty("ReferenceSDDocumentItem", "000010");

        reqToDocumentItemArray.add(reqToDocumentItemData);
        reqToDocumentItemEntry.add("results", reqToDocumentItemArray);

        JsonObject toDocumentFlowEntry = new JsonObject();
        JsonObject toDocumentFlowData = new JsonObject();
        JsonArray toDocumentFlowArray = new JsonArray();

        toDocumentFlowData.addProperty("QuantityInBaseUnit", "1");
        toDocumentFlowArray.add(toDocumentFlowData);
        toDocumentFlowEntry.add("results", toDocumentFlowArray);
        reqToDocumentItemData.add("to_DocumentFlow", toDocumentFlowEntry);

        reqJson.add("to_DeliveryDocumentItem", reqToDocumentItemEntry);

        JsonObject toDeliveryDocumentPartner = new JsonObject();
        JsonObject toDeliveryDocumentPartnerData = new JsonObject();
        JsonArray toDeliveryDocumentPartnerArray = new JsonArray();

        JsonObject toAddress = new JsonObject();

        toDeliveryDocumentPartnerData.add("to_Address", toAddress);
        toDeliveryDocumentPartnerArray.add(toDeliveryDocumentPartnerData);
        toDeliveryDocumentPartner.add("results", toDeliveryDocumentPartnerArray);

        reqJson.add("to_DeliveryDocumentPartner", toDeliveryDocumentPartner);

        String reqBody = reqJson.toString();

        System.out.println(reqBody);

        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(create_inbound_delivery_url))
                    .header("Authorization", "Basic " + encodeCredentials)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("sap-client", "400")
                    .header("X-REQUESTED-WITH", "x")
                    .header("sap-usercontext", "sap-client%3D400")
                    .header("Accept-Encoding", "application/json")
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