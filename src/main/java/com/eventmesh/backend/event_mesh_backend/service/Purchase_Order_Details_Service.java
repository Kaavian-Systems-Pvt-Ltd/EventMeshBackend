package com.eventmesh.backend.event_mesh_backend.service;

import com.eventmesh.backend.event_mesh_backend.model.MyPurchaseOrder;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.purchaseorder.PurchaseOrder;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.purchaseorder.PurchaseOrderByKeyFluentHelper;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.purchaseorder.PurchaseOrderItem;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultPurchaseOrderService;

import io.vavr.control.Option;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * We are using this service to get deliveryAddressCityName and supplierAddressCityName using
 * purchase Order ID
 */
@Service
public class Purchase_Order_Details_Service {

    /**Accessing the Destination*/
    final HttpDestination poDestination = DestinationAccessor.getDestination("s4OnPremisePurchaseOrder").asHttp();

    /**Here we check and set the Model as Null as default and get the useDestination to condition the call the of the Method*/
    public MyPurchaseOrder getPurchaseOrder(String purchaseOrderId, boolean useDestination) {

        MyPurchaseOrder myPurchaseOrder = null;

        /**it the Value of the useDestination is true then we will call the method which*/
        if (useDestination) {

            myPurchaseOrder = getPurchaseOrderDetailViaDestination(purchaseOrderId);

        } else {

            return myPurchaseOrder;

        }

        return myPurchaseOrder;
    }

    /**This is the method that will use the Purchase Order ID and get deliveryAddressCityName and supplierAddressCityName*/
    public MyPurchaseOrder getPurchaseOrderDetailViaDestination(String purchaseOrderId) {

        try {

            /**Using Purchase Order SDK method*/
            DefaultPurchaseOrderService defaultPurchaseOrderService = new DefaultPurchaseOrderService();
            PurchaseOrderByKeyFluentHelper purchaseOrderByKeyFluentHelper = defaultPurchaseOrderService.getPurchaseOrderByKey(purchaseOrderId).withHeader("sap-client", "400");
            purchaseOrderByKeyFluentHelper.select(PurchaseOrder.PURCHASE_ORDER, PurchaseOrder.SUPPLIER, PurchaseOrder.ADDRESS_CITY_NAME
                    , PurchaseOrder.TO_PURCHASE_ORDER_ITEM
            );
            PurchaseOrder purchaseOrder = purchaseOrderByKeyFluentHelper.executeRequest(poDestination);

            /**Accessing the Model and setting the value to it*/
            MyPurchaseOrder myPurchaseOrder = new MyPurchaseOrder();
            myPurchaseOrder.purchaseOrderId = purchaseOrderId;
            myPurchaseOrder.supplier = purchaseOrder.getSupplier();
            myPurchaseOrder.addressCityName = purchaseOrder.getAddressCityName();

            Option<List<PurchaseOrderItem>> purchaseOrderItemIfPresent = purchaseOrder.getPurchaseOrderItemIfPresent();

            PurchaseOrderItem purchaseOrderItem = purchaseOrderItemIfPresent.get().get(0);

            String deliveryCityName = purchaseOrderItem.getDeliveryAddressCityName();

            /**Getting the Supplier City Name*/
            myPurchaseOrder.deliveryAddressCityName = deliveryCityName;

            /**Logging the Data for Debugging*/
            System.out.println(myPurchaseOrder);

            return myPurchaseOrder;

        }catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException("Error initializing Purchase_Order_Details_Service", e);

        }

    }
}
