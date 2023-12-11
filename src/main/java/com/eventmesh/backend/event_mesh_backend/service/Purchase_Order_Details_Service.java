package com.eventmesh.backend.event_mesh_backend.service;

import com.eventmesh.backend.event_mesh_backend.model.MyPurchaseOrder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.purchaseorder.PurchaseOrder;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.purchaseorder.PurchaseOrderByKeyFluentHelper;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.purchaseorder.PurchaseOrderItem;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultPurchaseOrderService;

import io.vavr.control.Option;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.List;

@Service
public class Purchase_Order_Details_Service {


    @Value("${URL.GET.PURCHASE.ORDER.SDK.DATA}")
    String update_Purchase_Order_Url;
    @Value("${SAP.SMART.LOGIN.USERNAME}")
    String userName;

    @Value("${SAP.SMART.LOGIN.PASSWORD}")
    String password;

    final HttpDestination poDestination = DestinationAccessor.getDestination("mys4hanapo").asHttp();


    public MyPurchaseOrder getPurchaseOrderDetail(String purchaseOrderId) {

        MyPurchaseOrder myPurchaseOrder = new MyPurchaseOrder();

        myPurchaseOrder.purchaseOrderId = purchaseOrderId;
        myPurchaseOrder.successFlag = false;

        String updatePurchaseOrderUrlDynamically = String.format(update_Purchase_Order_Url, purchaseOrderId);


        JsonParser jsonParser = new JsonParser();

        String credentials = userName + ":" + password;

        String encodeCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        try {
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(new URI(updatePurchaseOrderUrlDynamically))
                    .header("Authorization", "Basic " + encodeCredentials)
                    .header("Accept", "application/json")
                    .header("sap-client", "400")
                    .header("sap-usercontext", "sap-client%3D400")
                    .header("Accept-Encoding", "application/json")
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

                    myPurchaseOrder.deliveryAddressCityName = deliveryAddressCityName;
                    myPurchaseOrder.successFlag = true;

                }else {
                    System.out.println("there is no array named results is in the object");
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }catch (IOException | InterruptedException e){

            e.printStackTrace();
        } catch (URISyntaxException e) {

            throw new RuntimeException(e);

        }

        return myPurchaseOrder;

    }

    public MyPurchaseOrder getPurchaseOrder(String purchaseOrderId, boolean useDestination) {
        MyPurchaseOrder myPurchaseOrder = null;

        if (useDestination) {
            myPurchaseOrder = getPurchaseOrderDetailViaDestination(purchaseOrderId);
        } else {
            myPurchaseOrder = getPurchaseOrderDetail(purchaseOrderId);
        }

        return myPurchaseOrder;
    }

    public MyPurchaseOrder getPurchaseOrderDetailViaDestination(String purchaseOrderId) {

        DefaultPurchaseOrderService defaultPurchaseOrderService = new DefaultPurchaseOrderService();
        PurchaseOrderByKeyFluentHelper purchaseOrderByKeyFluentHelper = defaultPurchaseOrderService.getPurchaseOrderByKey(purchaseOrderId).withHeader("sap-client", "400");
        purchaseOrderByKeyFluentHelper.select(PurchaseOrder.PURCHASE_ORDER, PurchaseOrder.SUPPLIER, PurchaseOrder.ADDRESS_CITY_NAME
                , PurchaseOrder.TO_PURCHASE_ORDER_ITEM
                );
        PurchaseOrder purchaseOrder = purchaseOrderByKeyFluentHelper.executeRequest(poDestination);

        MyPurchaseOrder myPurchaseOrder = new MyPurchaseOrder();
        myPurchaseOrder.purchaseOrderId = purchaseOrderId;
        myPurchaseOrder.supplier = purchaseOrder.getSupplier();
        myPurchaseOrder.addressCityName = purchaseOrder.getAddressCityName();
        Option<List<PurchaseOrderItem>> purchaseOrderItemIfPresent = purchaseOrder.getPurchaseOrderItemIfPresent();

        PurchaseOrderItem purchaseOrderItem = purchaseOrderItemIfPresent.get().get(0);

        String deliveryCityName = purchaseOrderItem.getDeliveryAddressCityName();
        myPurchaseOrder.deliveryAddressCityName = deliveryCityName;

        System.out.println(myPurchaseOrder);

        return myPurchaseOrder;

    }
}
