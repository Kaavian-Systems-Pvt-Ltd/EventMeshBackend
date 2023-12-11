package com.eventmesh.backend.event_mesh_backend.service;

import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.helper.ModificationResponse;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddeliveryv2.*;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultInboundDeliveryV2Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
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

//    public String createInboundDeliveryViaDestination(String purchaseOrderId, String frightVendorData) {
//
//        String returnDocument = "N/A";
//        DefaultInboundDeliveryV2Service service = new DefaultInboundDeliveryV2Service();
//
//        InbDeliveryHeader inbDeliveryHeader = new InbDeliveryHeader();
//        String supplier = "17300001";
//        inbDeliveryHeader.setSupplier(supplier);
//        List<InbDeliveryPartner> inbDeliveryPartners = new ArrayList<>();
//        InbDeliveryPartner partner = new InbDeliveryPartner();
//        partner.setAddress(new InbDeliveryAddress());
//
//        inbDeliveryPartners.add(partner);
//        inbDeliveryHeader.setDeliveryDocumentPartner(inbDeliveryPartners);
//        List<InbDeliveryItem> inbDeliveryItems = new ArrayList<>();
//        InbDeliveryItem inbDeliveryItem = new InbDeliveryItem();
//        inbDeliveryItem.setActualDeliveryQuantity(new BigDecimal(5));
//        inbDeliveryItem.setMaterial("TG0011");
//        inbDeliveryItem.setPlant("1710");
//        inbDeliveryItem.setReferenceSDDocument(purchaseOrderId);
//        inbDeliveryItem.setReferenceSDDocumentItem("10");
//        inbDeliveryItem.setActualDeliveryQuantity(new BigDecimal(1));
//        inbDeliveryItem.setDeliveryQuantityUnit("PC");
//        List<InbDeliveryDocFlow> inbDeliveryDocFlows = new ArrayList<>();
//        InbDeliveryDocFlow inbDeliveryDocFlow = new InbDeliveryDocFlow();
//        inbDeliveryDocFlow.setQuantityInBaseUnit(new BigDecimal(1));
//        inbDeliveryDocFlows.add(inbDeliveryDocFlow);
//        inbDeliveryItem.setDocumentFlow(inbDeliveryDocFlows);
//        inbDeliveryItems.add(inbDeliveryItem);
//
//        inbDeliveryHeader.setDeliveryDocumentItem(inbDeliveryItems);
//        InbDeliveryHeaderCreateFluentHelper inbDeliveryHeader1 = service.createInbDeliveryHeader(inbDeliveryHeader).withHeader("X-REQUESTED-WITH","x").withHeader("sap-client","400").withoutCsrfToken();
//
//        ModificationResponse<InbDeliveryHeader> inbDeliveryHeaderModificationResponse = inbDeliveryHeader1.executeRequest(delDestination);
//        if (inbDeliveryHeaderModificationResponse !=null
//                && inbDeliveryHeaderModificationResponse.getResponseStatusCode() >0
//                && inbDeliveryHeaderModificationResponse.getRequestEntity() != null ) {
//            System.out.println( inbDeliveryHeaderModificationResponse.getRequestEntity().getDeliveryDocument());
//            returnDocument = inbDeliveryHeaderModificationResponse.getRequestEntity().getDeliveryDocument();
//
//        }
//
//        return returnDocument;
//    }

    public String createInboundDeliveryViaDestination(String purchaseOrderId, String frightVendorData, boolean versionFlag) {

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
        inbDeliveryItem.setActualDeliveryQuantity(new BigDecimal(1));
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
}