package it.univr.track.controller.api;

import it.univr.track.entity.Shipment;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Profile("gestione-spedizioni")
public class ShipmentController {

    // create a shipment
    @PostMapping("/shipment")
    public boolean createShipment() {
        return true;
    }

    // read a shipment
    @GetMapping("/shipment/{shipmentId}")
    public Shipment readShipment(@PathVariable("shipmentId") Long id) {
        return new Shipment();
    }

    // update a shipment
    @PutMapping("/api/shipment")
    public boolean editShipment() {
        return true;
    }

    // update a shipment
    @DeleteMapping("/api/shipment")
    public boolean deleteShipment() {
        return true;
    }

    // list all the shipments
    @GetMapping("/api/shipments")
    public Shipment[] readShipments() {
        return new Shipment[0];
    }

    // start/stop tracking
    @PostMapping("/api/shipment/tracking")
    public boolean setShipmentTracking() {
        return true;
    }

    // allocate a device to a shipment
    @PostMapping("/api/shipment/allocate")
    public boolean allocateDevice() {
        return true;
    }

    // deallocate a device to a shipment
    @PostMapping("/api/shipment/allocate")
    public boolean deallocateDevice() {
        return true;
    }
}
