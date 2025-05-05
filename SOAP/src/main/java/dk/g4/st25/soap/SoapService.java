package dk.g4.st25.soap;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import kong.unirest.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class SoapService {
    private String headerSoapActionURL = "http://tempuri.org/IEmulatorService/";

    private HttpResponse<String> buildAndPostHttpResponse(String headerSoapAction, String bodyInput, String endpoint) {
        HttpResponse<String> httpResponse = Unirest.post(endpoint)
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("SOAPAction", headerSoapActionURL + headerSoapAction)
                .body("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n  " +
                        "<soap:Body>\n    " + bodyInput +
                        "</soap:Body>\n</soap:Envelope>\n")
                .asString(); {
        }
        return httpResponse;
    }
    public JSONObject getInventory(String endpoint) {
        try {
            HttpResponse<String> response = buildAndPostHttpResponse("GetInventory",
                    "<GetInventory xmlns=\"http://tempuri.org/\"/>\n  ", endpoint);
            // Check status code
            System.out.println("HTTP Status: " + response.getStatus());
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

    public void pickItem(int trayId, String endpoint){
        HttpResponse<String> response = buildAndPostHttpResponse("PickItem",
                "<PickItem xmlns=\"http://tempuri.org/\">\n      " +
                        "<trayId>" + trayId + "</trayId>\n    " +
                        "</PickItem>\n  ", endpoint);
        if (response.getStatus() == 200) {
            System.out.println(response.getBody());
        } else {
            System.out.println(response.getStatusText());
        }
    }

    public void insertItem(int trayId, String name, String endpoint){
        HttpResponse<String> response = buildAndPostHttpResponse("InsertItem",
                "<InsertItem xmlns=\"http://tempuri.org/\">\n      " +
                        "<trayId>" + trayId + "</trayId>\n      " +
                        "<name>" + name + "</name>\n    " +
                        "</InsertItem>\n  ", endpoint);
        if(response.getStatus() == 200) {
            System.out.println(response.getBody());
        } else {
            System.out.println(response.getStatusText());
        }
    }
    public void refreshInventory(String endpoint) {
        SoapService soapService = new SoapService();
        for (int i = 0; i<10; i++){
            soapService.pickItem(i+1, endpoint);
        }
        String[] newItems = {"Rollade" , "Trøffel", "Cupcake", "Lagkage", "Chokolade kage",
                "Cookie dough", "Ben and Jerry's", "Frysepizza", "Chips", "Brunsviger"};
        for (int i = 0; i< newItems.length; i++){
            soapService.insertItem(i + 1,newItems[i], endpoint);
        }
    }

    public static void main(String[] args) {
        SoapService soaptestforsoeg = new SoapService();
        String endpoint = "http://localhost:8081/Service.asmx";
        soaptestforsoeg.refreshInventory(endpoint);
        System.out.println(soaptestforsoeg.getInventory(endpoint));
        //soaptestforsoeg.pickItem(2);
        //soaptestforsoeg.insertItem(2, "TOM");
        //System.out.println(soaptestforsoeg.getInventory(endpoint));
    }
}