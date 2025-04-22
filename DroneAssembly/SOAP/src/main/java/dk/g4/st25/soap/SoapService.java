package dk.g4.st25.soap;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

import kong.unirest.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class SoapService {
    public JSONObject getInventory() {
        try {
            HttpResponse<String> response = Unirest.post("http://localhost:8081/Service.asmx")
                    .header("Content-Type", "text/xml; charset=utf-8")
                    .header("SOAPAction", "http://tempuri.org/IEmulatorService/GetInventory")
                    .body("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n  " +
                            "<soap:Body>\n    " +
                            "<GetInventory xmlns=\"http://tempuri.org/\"/>\n  " +
                            "</soap:Body>\n</soap:Envelope>\n")
                    .asString();
            // Check status code
            System.out.println("HTTP Status: " + response.getStatus());
            // Print full response
            System.out.println("SOAP Response: " + response.getBody());
            if (response.getStatus() == 200) {
                String soapResponse = response.getBody();

                // We have to parse the XML in an XML DOM
                // This simplifies the XML file and makes it able to list the XML file much easier
                Document doc = DocumentBuilderFactory.newInstance()
                        .newDocumentBuilder()
                        .parse(new ByteArrayInputStream(soapResponse.getBytes()));
                doc.getDocumentElement().normalize();


                // We fetch the first place in the XML file that reads getInventory and lists the json object from that
                NodeList nl = doc.getElementsByTagName("GetInventoryResult");
                if (nl.getLength() > 0) {
                    String jsonString = nl.item(0).getTextContent();
                    // Return the parsed JSON
                    return new JSONObject(jsonString);

                } else {
                    // Error handling if getInventory is not found
                    JSONObject errorJson = new JSONObject();
                    errorJson.put("error", "GetInventoryResult element not found");
                    return errorJson;
                }
            } else {
                    // Error handling if the getStatus does not equal 200
                    JSONObject errorJson = new JSONObject();
                    errorJson.put("error", response.getStatusText());
                    return errorJson;
                }
        } catch (Exception e) {
            // catch block for the try-catch
            // Needed for handling JSON object
            JSONObject errorJson = new JSONObject();
            errorJson.put("exception", e.getMessage());
            return errorJson;
        }
    }

    public void pickItem(int trayId){
        HttpResponse<String> response = Unirest.post("http://localhost:8081/Service.asmx")
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("SOAPAction", "http://tempuri.org/IEmulatorService/PickItem")
                .body("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n  " +
                        "<soap:Body>\n    " +
                        "<PickItem xmlns=\"http://tempuri.org/\">\n      " +
                        // TrayId is the place it takes item in the warehouse (right now it picks up nr 1)
                        // Updated to pick on trayId
                        "<trayId>" + trayId + "</trayId>\n    " +
                        "</PickItem>\n  " +
                        "</soap:Body>\n</soap:Envelope>\n")
                .asString();
        if (response.getStatus() == 200) {
            System.out.println(response.getBody());
        } else {
            System.out.println(response.getStatusText());
        }
    }

    public void insertItem(int trayId, String name){
        HttpResponse<String> response = Unirest.post("http://localhost:8081/Service.asmx")
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("SOAPAction", "http://tempuri.org/IEmulatorService/InsertItem")
                .body("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n  " +
                        "<soap:Body>\n    " +
                        "<InsertItem xmlns=\"http://tempuri.org/\">\n      " +
                        // TrayId is the place it takes item in the warehouse (right now it inserts into nr 1 - IT HAS TO BE EMPTY)
                        // Updated to pick trayId
                        "<trayId>" + trayId + "</trayId>\n      " +
                        // name is interchangable
                        "<name>" + name + "</name>\n    " +
                        "</InsertItem>\n  " +
                        "</soap:Body>\n</soap:Envelope>\n")
                .asString();
        if(response.getStatus() == 200) {
            System.out.println(response.getBody());
        } else {
            System.out.println(response.getStatusText());
        }
    }
    public void refreshInventory() {
        SoapService soapService = new SoapService();
        for (int i = 0; i<10; i++){
            soapService.pickItem(i+1);
        }
        String[] newItems = {"Rollade", "Trøffel", "Cupcake", "Lagkage", "Chokolade kage",
                    "Cookie dough", "Ben and Jerry's", "Frysepizza", "Chips", "Brunsviger"};
        for (int i = 0; i< newItems.length; i++){
            soapService.insertItem(i + 1,newItems[i]);
        }
    }

    public static void main(String[] args) {
        SoapService soaptestforsoeg = new SoapService();
        soaptestforsoeg.refreshInventory();
        System.out.println(soaptestforsoeg.getInventory());
        soaptestforsoeg.pickItem(2);
        soaptestforsoeg.insertItem(2, "TOM");
        System.out.println(soaptestforsoeg.getInventory());
    }
}
