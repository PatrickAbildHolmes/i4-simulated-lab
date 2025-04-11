package dk.g4.st25.soap;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class SOAPtestforsoeg {
    public void getInventory() {
        HttpResponse<String> response = Unirest.post("http://localhost:8081/Service.asmx")
                .header("Content-Type", "text/xml; charset=utf-8")
                .header("SOAPAction", "http://tempuri.org/IEmulatorService/GetInventory")
                .body("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n  " +
                        "<soap:Body>\n    " +
                        "<GetInventory xmlns=\"http://tempuri.org/\"/>\n  " +
                        "</soap:Body>\n</soap:Envelope>\n")
                .asString();
        if (response.getStatus() == 200) {
            System.out.println(response.getBody());
        } else {
            System.out.println(response.getStatusText());
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
        soaptestforsoeg.getInventory();
        soaptestforsoeg.pickItem();
        soaptestforsoeg.insertItem();
    }
}
