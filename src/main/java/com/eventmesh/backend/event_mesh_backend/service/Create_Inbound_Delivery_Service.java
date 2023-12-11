package com.eventmesh.backend.event_mesh_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.helper.ModificationResponse;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddelivery.batch.DefaultInboundDeliveryServiceBatch;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddeliveryv2.*;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultInboundDeliveryService;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultInboundDeliveryV2Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class Create_Inbound_Delivery_Service {

    @Value("${URL.POST.TO.CREATE.INBOUND.DELIVERY.SDK.DOCUMENT}")
    String create_inbound_delivery_url;

    @Value("${SAP.SMART.LOGIN.USERNAME}")
    String userName;

    @Value("${SAP.SMART.LOGIN.PASSWORD}")
    String password;

    final HttpDestination delDestination = DestinationAccessor.getDestination("s4Onpremise").asHttp();


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
        reqToDocumentItemData.addProperty("ReferenceSDDocumentItem", "10");

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

    public String createInboundDeliveryViaDestination(String purchaseOrderId, String frightVendorData) {

        String returnDocument = "N/A";
        DefaultInboundDeliveryV2Service service = new DefaultInboundDeliveryV2Service();

        InbDeliveryHeader inbDeliveryHeader = new InbDeliveryHeader();
        String supplier = "17300001";
        inbDeliveryHeader.setSupplier(supplier);
        List<InbDeliveryPartner> inbDeliveryPartners = new ArrayList<>();
        InbDeliveryPartner partner = new InbDeliveryPartner();
        partner.setAddress(new InbDeliveryAddress());

        inbDeliveryPartners.add(partner);
        inbDeliveryHeader.setDeliveryDocumentPartner(inbDeliveryPartners);
        List<InbDeliveryItem> inbDeliveryItems = new ArrayList<>();
        InbDeliveryItem inbDeliveryItem = new InbDeliveryItem();
        inbDeliveryItem.setActualDeliveryQuantity(new BigDecimal(5));
        inbDeliveryItem.setMaterial("TG0011");
        inbDeliveryItem.setPlant("1710");
        inbDeliveryItem.setReferenceSDDocument(purchaseOrderId);
        inbDeliveryItem.setReferenceSDDocumentItem("10");
        inbDeliveryItem.setActualDeliveryQuantity(new BigDecimal(1));
        inbDeliveryItem.setDeliveryQuantityUnit("PC");
        List<InbDeliveryDocFlow> inbDeliveryDocFlows = new ArrayList<>();
        InbDeliveryDocFlow inbDeliveryDocFlow = new InbDeliveryDocFlow();
        inbDeliveryDocFlow.setQuantityInBaseUnit(new BigDecimal(1));
        inbDeliveryDocFlows.add(inbDeliveryDocFlow);
        inbDeliveryItem.setDocumentFlow(inbDeliveryDocFlows);
        inbDeliveryItems.add(inbDeliveryItem);

        inbDeliveryHeader.setDeliveryDocumentItem(inbDeliveryItems);
        InbDeliveryHeaderCreateFluentHelper inbDeliveryHeader1 = service.createInbDeliveryHeader(inbDeliveryHeader).withHeader("X-REQUESTED-WITH","x").withHeader("sap-client","400").withoutCsrfToken();

        ModificationResponse<InbDeliveryHeader> inbDeliveryHeaderModificationResponse = inbDeliveryHeader1.executeRequest(delDestination);
        if (inbDeliveryHeaderModificationResponse !=null
                && inbDeliveryHeaderModificationResponse.getResponseStatusCode() >0
                && inbDeliveryHeaderModificationResponse.getRequestEntity() != null ) {
            System.out.println( inbDeliveryHeaderModificationResponse.getRequestEntity().getDeliveryDocument());
            returnDocument = inbDeliveryHeaderModificationResponse.getRequestEntity().getDeliveryDocument();

        }

        return returnDocument;
    }

    public String createInboundDeliveryViaDestination(String purchaseOrderId, String frightVendorData, boolean versionFlag) {

        String returnDocument = "N/A";
        DefaultInboundDeliveryService service = new DefaultInboundDeliveryService();

        com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddelivery.InbDeliveryHeader inbDeliveryHeader = new com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddelivery.InbDeliveryHeader();
        String supplier = "17300001";
        inbDeliveryHeader.setSupplier(supplier);
        List<com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddelivery.InbDeliveryPartner> inbDeliveryPartners = new ArrayList<>();
        com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddelivery.InbDeliveryPartner partner = new com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddelivery.InbDeliveryPartner();
        partner.setAddress(new com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddelivery.InbDeliveryAddress());

        inbDeliveryPartners.add(partner);
        inbDeliveryHeader.setDeliveryDocumentPartner(inbDeliveryPartners);
        List<com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddelivery.InbDeliveryItem> inbDeliveryItems = new ArrayList<>();
        com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddelivery.InbDeliveryItem inbDeliveryItem = new com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddelivery.InbDeliveryItem();
        inbDeliveryItem.setActualDeliveryQuantity(new BigDecimal(1));
        inbDeliveryItem.setMaterial("TG0011");
        inbDeliveryItem.setPlant("1710");
        inbDeliveryItem.setReferenceSDDocument(purchaseOrderId);
        inbDeliveryItem.setReferenceSDDocumentItem("10");
        inbDeliveryItem.setActualDeliveryQuantity(new BigDecimal(1));
        inbDeliveryItem.setDeliveryQuantityUnit("PC");
        List<com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddelivery.InbDeliveryDocFlow> inbDeliveryDocFlows = new ArrayList<>();
        com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddelivery.InbDeliveryDocFlow inbDeliveryDocFlow = new com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddelivery.InbDeliveryDocFlow();
        inbDeliveryDocFlow.setQuantityInBaseUnit(new BigDecimal(1));
        inbDeliveryDocFlows.add(inbDeliveryDocFlow);
        inbDeliveryItem.setDocumentFlow(inbDeliveryDocFlows);
        inbDeliveryItems.add(inbDeliveryItem);

        inbDeliveryHeader.setDeliveryDocumentItem(inbDeliveryItems);
        com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddelivery.InbDeliveryHeaderCreateFluentHelper inbDeliveryHeader1
                = service.createInbDeliveryHeader(inbDeliveryHeader).withHeader("X-REQUESTED-WITH","x").withHeader("sap-client","400").withoutCsrfToken();

        ModificationResponse<com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddelivery.InbDeliveryHeader> inbDeliveryHeaderModificationResponse = inbDeliveryHeader1.executeRequest(delDestination);
        if (inbDeliveryHeaderModificationResponse !=null
                && inbDeliveryHeaderModificationResponse.getResponseStatusCode() >0
                && inbDeliveryHeaderModificationResponse.getRequestEntity() != null ) {
            System.out.println( inbDeliveryHeaderModificationResponse.getRequestEntity().getDeliveryDocument());
            returnDocument = inbDeliveryHeaderModificationResponse.getRequestEntity().getDeliveryDocument();

        }

        return returnDocument;
    }
}