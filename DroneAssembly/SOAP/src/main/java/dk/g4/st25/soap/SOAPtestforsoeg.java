package dk.g4.st25.soap;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;

import kong.unirest.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class SOAPtestforsoeg {
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

            System.out.println("HTTP Status: " + response.getStatus());  // Check status code
            System.out.println("SOAP Response: " + response.getBody()); // Print full response
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

    public void pickItem(){
        HttpResponse<String> response = Unirest.post("http://localhost:8081/Service.asmx")
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("SOAPAction", "http://tempuri.org/IEmulatorService/PickItem")
                .body("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n  " +
                        "<soap:Body>\n    " +
                        "<PickItem xmlns=\"http://tempuri.org/\">\n      " +
                        // TrayId is the place it takes item in the warehouse (right now it picks up nr 1)
                        "<trayId>1</trayId>\n    " +
                        "</PickItem>\n  " +
                        "</soap:Body>\n</soap:Envelope>\n")
                .asString();
        if (response.getStatus() == 200) {
            System.out.println(response.getBody());
        } else {
            System.out.println(response.getStatusText());
        }
    }

    public void insertItem(){
        HttpResponse<String> response = Unirest.post("http://localhost:8081/Service.asmx")
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("SOAPAction", "http://tempuri.org/IEmulatorService/InsertItem")
                .body("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n  " +
                        "<soap:Body>\n    " +
                        "<InsertItem xmlns=\"http://tempuri.org/\">\n      " +
                        // TrayId is the place it takes item in the warehouse (right now it inserts into nr 1 - IT HAS TO BE EMPTY)
                        "<trayId>1</trayId>\n      " +
                        // name is interchangable
                        "<name>rollade</name>\n    " +
                        "</InsertItem>\n  " +
                        "</soap:Body>\n</soap:Envelope>\n")
                .asString();
        if(response.getStatus() == 200) {
            System.out.println(response.getBody());
        } else {
            System.out.println(response.getStatusText());
        }
    }

    public static void main(String[] args) {
        SOAPtestforsoeg soaptestforsoeg = new SOAPtestforsoeg();
        System.out.println(soaptestforsoeg.getInventory());
        //soaptestforsoeg.pickItem();
        //soaptestforsoeg.insertItem();
    }
}
