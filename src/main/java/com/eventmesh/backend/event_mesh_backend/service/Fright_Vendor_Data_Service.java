package com.eventmesh.backend.event_mesh_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class Fright_Vendor_Data_Service {

    @Value("${URL.POST.TO.GET.FRIGHT.VENDOR.DATA}")
    String postForFrightVendorData;

    public String getFrightVendorData(String purchaseOrderData, String supplierAddressData) {

        String reqBody = "{\"pickUpLocation\" : \"" + supplierAddressData +"\", \"destinationLocation\" : \"" + purchaseOrderData + "\"}";

        try {

            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(postForFrightVendorData))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();

            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            int StatusCode = res.statusCode();

            String reqRes = res.body();

            return reqRes;

        }catch (Exception e){

            return  e.getMessage();

        }

    }
}
