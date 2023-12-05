package com.eventmesh.backend.event_mesh_backend.controller;

import com.eventmesh.backend.event_mesh_backend.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("api")
public class Purchase_Order_Controller {

    @Value("${URL.GET.PURCHASE.ORDER.DATA}")
    String update_Purchase_Order_Url;

    @Value("${URL.GET.SUPPLIER.ADDRESS.DATA}")
    String update_Supplier_Address_Url;

    @Value("${URL.POST.TO.CREATE.INBOUND.DELIVERY.DOCUMENT}")
    String create_inbound_delivery_url;

    ResponseEntity<String> purchaseOrderId;

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

    @PostMapping("recieveData")
    public ResponseEntity<String> getPurchaseOrderIdFromEventMesh (@RequestBody String message) throws JsonProcessingException {

        ResponseEntity<String> purchaseOrderIdFromEventMesh =  ResponseEntity.ok(get_purchase_order_id_from_event_mesh.getPurchaseOrderIdFromEventMesh(message));

        purchaseOrderId = purchaseOrderIdFromEventMesh;

        getPurchaseOrderId();

        return purchaseOrderIdFromEventMesh;

    }

    @GetMapping("getPurchaseOrderId")
    public ResponseEntity<String> getPurchaseOrderId () {

        if (purchaseOrderId != null) {

            String updatePurchaseOrderUrlDynamically = String.format(update_Purchase_Order_Url, purchaseOrderId.getBody());

            String updateSupplierOrderAddressUrlDynamically = String.format(update_Supplier_Address_Url, purchaseOrderId.getBody());

            if(!update_Purchase_Order_Url.equals(updatePurchaseOrderUrlDynamically) && !update_Supplier_Address_Url.equals(updateSupplierOrderAddressUrlDynamically) ){

                String purchaseOrderData = purchase_order_details_service.getPurchaseOrderDetail(updatePurchaseOrderUrlDynamically);

                String supplierAddressData = supplier_address_service.getSupplierAddress(updateSupplierOrderAddressUrlDynamically);

                if(!purchaseOrderData.isEmpty() && !supplierAddressData.isEmpty()) {

                    String frightVendorData = fright_vender_data_service.getFrightVendorData(purchaseOrderData, supplierAddressData);

                    System.out.println(frightVendorData);

                    if (!frightVendorData.isEmpty()){

                        String InboundDelivery = createInboundDeliveryService.CreateInboundDocument();

                        System.out.println(InboundDelivery);
                    }

                    return ResponseEntity.ok(frightVendorData);

                }

            }

            return ResponseEntity.ok("Success got the Id");

        }else {
            return ResponseEntity.ok("error : Id not available");
        }
    }

}
