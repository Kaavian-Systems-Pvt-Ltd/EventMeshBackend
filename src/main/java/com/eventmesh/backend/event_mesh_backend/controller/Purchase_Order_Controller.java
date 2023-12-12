package com.eventmesh.backend.event_mesh_backend.controller;

import com.eventmesh.backend.event_mesh_backend.model.MyPurchaseOrder;
import com.eventmesh.backend.event_mesh_backend.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("api")
public class Purchase_Order_Controller {

    @Autowired
    Get_Purchase_Order_Id_From_Event_Mesh get_purchase_order_id_from_event_mesh;

    @Autowired
    Purchase_Order_Details_Service purchase_order_details_service;

    @Autowired
    Fright_Vendor_Data_Service fright_vender_data_service;

    @Autowired
    Create_Inbound_Delivery_Service createInboundDeliveryService;

    /** A Method to get the Purchase Order Data From Event Mesh */
    @PostMapping("receiveData")
    public ResponseEntity<String> getPurchaseOrderIdFromEventMesh (@RequestBody String message) throws JsonProcessingException {

        String purchaseOrderId;

        // Checking if whether we are getting the Message
        if (message !=null && message.startsWith("{")) {

            /**Using this method to get the Purchase Order ID from the Purchase Order Data */
            purchaseOrderId =  get_purchase_order_id_from_event_mesh.getPurchaseOrderIdFromEventMesh(message);

        }else {

            purchaseOrderId =  message;

        }

        /**If we got the Purchase Order ID */
        if (purchaseOrderId != null)

            /** We call this Method and send the Data as parameter*/
         processPurchaseOrder(purchaseOrderId);

        return ResponseEntity.ok(purchaseOrderId);

    }

    /** In this method we are processing all the process we need to create an Inbound Document
     * So first we are getting the Purchase Order ID through parameter
     */
    public String processPurchaseOrder(String purchaseOrderId) throws JsonProcessingException {

        /**Here we are checking whether the purchase Order variable has value or not*/
        if (purchaseOrderId != null) {

                /**here we are Calling a Method which will use the Purchase Order ID and get us entier Data of the ID
                 * And get us Supplier Address Data also
                 */
                MyPurchaseOrder myPurchaseOrder = purchase_order_details_service.getPurchaseOrder(purchaseOrderId, true);

                /**Getting the Delivery Address City Name from the myPurchaseOrder Model*/
                String deliveryAddressCityName = myPurchaseOrder.deliveryAddressCityName;

                /**Getting the Supplier Address City Name from the myPurchaseORder Model*/
                String supplierAddressData = myPurchaseOrder.addressCityName;

                /**Logging the Data for Debugging*/
                System.out.println(deliveryAddressCityName + supplierAddressData);

                /** checking if the both deliveryAddressCityName and supplierAddressData has Variable*/
                if(!deliveryAddressCityName.isEmpty() && !supplierAddressData.isEmpty()) {

                    /**
                     * Here we are calling another method which will use both deliveryAddressCityName and supplierAddressData
                     * as parameter and get as the frightVendor Data from DB.
                     * This method is A Url based method and based on a different java application and Hana Database.
                     * so in order to make this method work both the application and Hana Database need to be working
                     */
                    String frightVendorData = fright_vender_data_service.getFrightVendorData(deliveryAddressCityName, supplierAddressData);

                    /**Logging the Data for Debugging*/
                    System.out.println("frightVendorData  " + frightVendorData);

                    /**Checking if the frightVendorData Variable has the Data or not*/
                    if (!frightVendorData.isEmpty()){

                        /**
                         * Here, By using this Method we are creating the inbound Doc in s4Hana
                         * we send both purchase Order ID, frightVendorData and a boolean value for setting a flag
                         */
                        String InboundDelivery = createInboundDeliveryService.createInboundDeliveryViaDestination(purchaseOrderId, frightVendorData,true);

                        /**Logging the Data for Debugging*/
                        System.out.println(InboundDelivery);
                    }

                }

//            }

            return "Process Completed Successfully";

        }else {

            return "error : Id not available";
        }
    }

}
