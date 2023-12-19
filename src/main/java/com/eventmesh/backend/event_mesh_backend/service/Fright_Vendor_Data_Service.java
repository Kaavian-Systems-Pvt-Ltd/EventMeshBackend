package com.eventmesh.backend.event_mesh_backend.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.JSONObject;
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

        String frightVendorData = null;
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

            if (StatusCode == 200) {
                String reqRes = res.body();
                try {
                    // Validate if we received JSON
                    JsonElement jsonObject = JsonParser.parseString(reqRes);
                    frightVendorData = reqRes;
                } catch (Exception ex) {
                    frightVendorData = "{\"pickuplocation\":\"Muncie\",\"destinationlocation\":\"Palo Alto\",\"price\":1350,\"deliveryindays\":1,\"freightvendorname\":\"Das and Co\",\"contact\":116,\"freightvendorid\":27}";
                }
            } else {
                frightVendorData = "{\"pickuplocation\":\"Muncie\",\"destinationlocation\":\"Palo Alto\",\"price\":1350,\"deliveryindays\":1,\"freightvendorname\":\"Das and Co\",\"contact\":116,\"freightvendorid\":27}";
            }

        }catch (Exception e){

            frightVendorData = "{\"pickuplocation\":\"Muncie\",\"destinationlocation\":\"Palo Alto\",\"price\":1350,\"deliveryindays\":1,\"freightvendorname\":\"Das and Co\",\"contact\":116,\"freightvendorid\":27}";

        }

        return frightVendorData;

    }
}
