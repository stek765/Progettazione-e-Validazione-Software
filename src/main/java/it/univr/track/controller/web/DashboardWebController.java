package it.univr.track.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashboardWebController {

    // Dashboard of devices on the map
    @RequestMapping("/web/map")
    public String map() {
        return "map";
    }

    /*
     * //visualize the status of all devices
     * 
     * @RequestMapping("/web/devices")
     * public String devices() {
     * return "devices";
     * }
     * 
     * //visualize the status of all shipments
     * 
     * @RequestMapping("/web/shipments")
     * public String shipments() {
     * return "shipments";
     * }
     */

    // visualize the status of a single shipment
    @RequestMapping("/web/shipment")
    public String shipment() {
        return "shipment";
    }

}
