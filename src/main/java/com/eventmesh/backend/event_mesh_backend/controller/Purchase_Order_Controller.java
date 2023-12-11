package com.eventmesh.backend.event_mesh_backend.controller;

import com.eventmesh.backend.event_mesh_backend.model.MyPurchaseOrder;
import com.eventmesh.backend.event_mesh_backend.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("api")
public class Purchase_Order_Controller {



    @Value("${URL.GET.SUPPLIER.ADDRESS.SDK.DATA}")
    String update_Supplier_Address_Url;

    @Value("${URL.GET.PURCHASE.ORDER.SDK.DATA}")
    String update_Purchase_Order_Url;
    @Value("${URL.POST.TO.CREATE.INBOUND.DELIVERY.SDK.DOCUMENT}")
    String create_inbound_delivery_url;

    @Autowired
    Get_Purchase_Order_Id_From_Event_Mesh get_purchase_order_id_from_event_mesh;

    @Autowired
    Purchase_Order_Details_Service purchase_order_details_service;

    @Autowired
    Supplier_Address_Service supplier_address_service;

    @Autowired
    Fright_Vendor_Data_Service fright_vender_data_service;

    @Autowired
    Create_Inbound_Delivery_Service createInboundDeliveryService;

    @PostMapping("receiveData")
    public ResponseEntity<String> getPurchaseOrderIdFromEventMesh (@RequestBody String message) throws JsonProcessingException {
        String purchaseOrderId;
        if (message !=null && message.startsWith("{")) {
            purchaseOrderId =  get_purchase_order_id_from_event_mesh.getPurchaseOrderIdFromEventMesh(message);
        }else {
            purchaseOrderId =  message;
        }

        if (purchaseOrderId != null)
         processPurchaseOrder(purchaseOrderId);

        return ResponseEntity.ok(purchaseOrderId);

    }




    public String processPurchaseOrder(String purchaseOrderId) {

        if (purchaseOrderId != null) {

            String updateSupplierOrderAddressUrlDynamically = String.format(update_Supplier_Address_Url, purchaseOrderId);

                MyPurchaseOrder myPurchaseOrder = purchase_order_details_service.getPurchaseOrder(purchaseOrderId, true);

                String deliveryAddressCityName = myPurchaseOrder.deliveryAddressCityName;

                String supplierAddressData = myPurchaseOrder.addressCityName;

                System.out.println(deliveryAddressCityName);

                System.out.println(supplierAddressData);

                if(!deliveryAddressCityName.isEmpty() && !supplierAddressData.isEmpty()) {

                    String frightVendorData = fright_vender_data_service.getFrightVendorData(deliveryAddressCityName, supplierAddressData);

                    System.out.println(frightVendorData + "la la boi");

                    if (!frightVendorData.isEmpty()){

                       // String InboundDelivery = createInboundDeliveryService.CreateInboundDocument(purchaseOrderId, frightVendorData);
                        String InboundDelivery = createInboundDeliveryService.createInboundDeliveryViaDestination(purchaseOrderId, frightVendorData,true);

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
