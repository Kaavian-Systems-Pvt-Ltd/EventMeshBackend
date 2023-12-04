package com.eventmesh.backend.event_mesh_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class Get_Purchase_Order_Id_From_Event_Mesh {

    public String getPurchaseOrderIdFromEventMesh(String message) throws JsonProcessingException {

        System.out.println(message + "inside service");

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = objectMapper.readTree(new String(message));

        String purchaseOrderId = jsonNode.path("data").path("PurchaseOrder").asText();

        System.out.println("PurchaseOrderId"+purchaseOrderId);

        return purchaseOrderId;
    }
}
