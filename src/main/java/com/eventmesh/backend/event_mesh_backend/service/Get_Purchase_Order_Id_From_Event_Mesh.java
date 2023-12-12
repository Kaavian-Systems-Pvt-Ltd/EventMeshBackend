package com.eventmesh.backend.event_mesh_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Here we use the Purchase Order Data from event Mesh and get the Purchase order ID inside
 * it and return it
 */
@Service
public class Get_Purchase_Order_Id_From_Event_Mesh {

    /**Using this method we get the Purchase Order ID from the Message*/
    public String getPurchaseOrderIdFromEventMesh(String message) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(new String(message));

        String purchaseOrderId = jsonNode.path("data").path("PurchaseOrder").asText();

        return purchaseOrderId;
    }
}
