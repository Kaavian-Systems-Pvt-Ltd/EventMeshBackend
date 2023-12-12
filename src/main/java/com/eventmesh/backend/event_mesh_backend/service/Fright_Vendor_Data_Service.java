package com.eventmesh.backend.event_mesh_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/***
 * Here, In this Method we are using both deliveryAddressCityName and supplierAddressData to get
 * frightVendorData from Hana Database through another java application.
 */
@Service
public class Fright_Vendor_Data_Service {

    /**Getting the URL of the Deployed Java Application from application.properties*/
    @Value("${URL.POST.TO.GET.FRIGHT.VENDOR.DATA}")
    String GetFrightVendorDataURL;

    public String getFrightVendorData(String purchaseOrderData, String supplierAddressData) {

        /**Creating the JSON Body*/
        String reqBody = "{\"pickUpLocation\" : \"" + supplierAddressData +"\", \"destinationLocation\" : \"" + purchaseOrderData + "\"}";

        try {

            /**Using HttpClient Method to access the other Deployed Java application to get
             * FrightVendor Data
             */
            HttpClient httpClient = HttpClient.newHttpClient();

            /** Creating the Req with required header and URL*/
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(GetFrightVendorDataURL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(reqBody))
                    .build();

            /**Sending the Req with body*/
            HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

            int StatusCode = res.statusCode();

            /**Getting the Response*/
            String reqRes = res.body();

            /**Logging the Data for Debugging*/
            System.out.println(reqRes);

            return reqRes;

        }catch (Exception e){

            return  e.getMessage();

        }

    }
}
