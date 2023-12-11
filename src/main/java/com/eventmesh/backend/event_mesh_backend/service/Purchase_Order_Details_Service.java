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

    final HttpDestination poDestination = DestinationAccessor.getDestination("s4Onpremise").asHttp();

    public MyPurchaseOrder getPurchaseOrder(String purchaseOrderId, boolean useDestination) {

        MyPurchaseOrder myPurchaseOrder = null;

        if (useDestination) {
            myPurchaseOrder = getPurchaseOrderDetailViaDestination(purchaseOrderId);
        } else {
//            myPurchaseOrder = getPurchaseOrderDetail(purchaseOrderId);
            return null;
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
