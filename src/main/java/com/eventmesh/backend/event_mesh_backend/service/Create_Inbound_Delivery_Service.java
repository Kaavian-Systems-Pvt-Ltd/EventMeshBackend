package com.eventmesh.backend.event_mesh_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;
import com.sap.cloud.sdk.datamodel.odata.helper.ModificationResponse;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.inbounddeliveryv2.*;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultInboundDeliveryV2Service;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * By using this Service we are creating the Inbound Document in s4Hana
 */
@Service
public class Create_Inbound_Delivery_Service {

    /**Getting the Destination For inbound delivery Document from ENV and setting in a variable*/
    final HttpDestination delDestination = DestinationAccessor.getDestination("s4OnPremiseInBoundDelivery").asHttp();

    /**
     * In this Method we Create a Logic which will create Inbound Delivery Document.
     * We are using V2 of the API here
     * */
    public String createInboundDeliveryViaDestination(String purchaseOrderId, String frightVendorData, boolean versionFlag) throws JsonProcessingException {

        /**Getting individual frightVendor Data from Json*/
        Type type = new TypeToken<Map<String, Object>>() {}.getType();

        Map<String, Object> frightVendor = new Gson().fromJson(frightVendorData, type);

        String frightVendorName = (String) frightVendor.get("freightvendorname");

        String returnDocument = "N/A";

        /**Using V2 of the Inbound Delivery SDK Service*/
        DefaultInboundDeliveryV2Service service = new DefaultInboundDeliveryV2Service();

        /**Accessing the Header*/
        InbDeliveryHeader inbDeliveryHeader = new InbDeliveryHeader();

        /**Adding Supplier Data*/
        String supplier = "17300001";

        inbDeliveryHeader.setSupplier(supplier);

        /**Adding to_DeliveryHeaderText data*/
        List<InbDeliveryHeaderText> inbDeliveryHeaderText = new ArrayList<>();

        InbDeliveryHeaderText inbDeliveryHeaderTexts = new InbDeliveryHeaderText();
        inbDeliveryHeaderTexts.setTextElementText(frightVendorName);
        inbDeliveryHeaderTexts.setTextElementDescription("Transport Information");
        inbDeliveryHeaderTexts.setDeliveryLongTextIsFormatted(false);
        inbDeliveryHeaderTexts.setLanguage("EN");
        inbDeliveryHeaderText.add(inbDeliveryHeaderTexts);

        inbDeliveryHeader.setDeliveryDocumentText(inbDeliveryHeaderText);

        /**Adding to_DeliveryPartner Data*/
        List<InbDeliveryPartner> inbDeliveryPartners = new ArrayList<>();
        InbDeliveryPartner partner = new InbDeliveryPartner();
        partner.setAddress(new InbDeliveryAddress());
        inbDeliveryPartners.add(partner);
        inbDeliveryHeader.setDeliveryDocumentPartner(inbDeliveryPartners);

        /**Adding to_DeliveryDocumentItem Data*/
        List<InbDeliveryItem> inbDeliveryItems = new ArrayList<>();
        InbDeliveryItem inbDeliveryItem = new InbDeliveryItem();
        inbDeliveryItem.setActualDeliveryQuantity(new BigDecimal(1));
        inbDeliveryItem.setMaterial("TG0011");
        inbDeliveryItem.setPlant("1710");
        inbDeliveryItem.setReferenceSDDocument(purchaseOrderId);
        inbDeliveryItem.setReferenceSDDocumentItem("10");
        inbDeliveryItem.setActualDeliveryQuantity(new BigDecimal(1));
        inbDeliveryItem.setDeliveryQuantityUnit("PC");

        /**Adding to_DeliveryDocFlow Data which us inside the to_DeliveryDocumentItem*/
        List<InbDeliveryDocFlow> inbDeliveryDocFlows = new ArrayList<>();
        InbDeliveryDocFlow inbDeliveryDocFlow = new InbDeliveryDocFlow();
        inbDeliveryDocFlow.setQuantityInBaseUnit(new BigDecimal(1));
        inbDeliveryDocFlows.add(inbDeliveryDocFlow);

        /**Adding the to_DocumentFlow data to to_DeliveryDocumentItem*/
        inbDeliveryItem.setDocumentFlow(inbDeliveryDocFlows);
        inbDeliveryItems.add(inbDeliveryItem);

        /**then we are adding the to_DeliveryDocumentItem to header*/
        inbDeliveryHeader.setDeliveryDocumentItem(inbDeliveryItems);

        /**Setting the Header*/
        InbDeliveryHeaderCreateFluentHelper inbDeliveryHeader1 = service.createInbDeliveryHeader(inbDeliveryHeader).withHeader("X-REQUESTED-WITH","x").withHeader("sap-client","400").withoutCsrfToken();

        /**Executing the Request*/
        ModificationResponse<InbDeliveryHeader> inbDeliveryHeaderModificationResponse = inbDeliveryHeader1.executeRequest(delDestination);

        if (inbDeliveryHeaderModificationResponse !=null
                && inbDeliveryHeaderModificationResponse.getResponseStatusCode() >0
                && inbDeliveryHeaderModificationResponse.getRequestEntity() != null ) {

            /**Logging the Data for Debugging*/
            System.out.println( inbDeliveryHeaderModificationResponse.getRequestEntity().getDeliveryDocument());

            returnDocument = inbDeliveryHeaderModificationResponse.getRequestEntity().getDeliveryDocument();

        }

        return returnDocument;
    }
}