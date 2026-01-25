package it.univr.track.controller.web;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Profile("gestione-spedizioni")
public class ShipmentWebController {

    //create new shipment
    @RequestMapping("/web/newShipment")
    public String newShipment() {
        return "newShipment";
    }

    //list shipments
    @RequestMapping("/web/shipments")
    public String shipments() {
        return "shipments";
    }

    //activate/deactivate tracking
    @RequestMapping("/web/tracking")
    public String tracking() {
        return "tracking";
    }

    //allocate a device to a shipment
    @RequestMapping("/web/shipmentAllocate")
    public String shipmentAllocate() {
        return "shipmentAllocate";
    }

}
