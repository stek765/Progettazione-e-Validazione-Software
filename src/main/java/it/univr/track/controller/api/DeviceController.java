package it.univr.track.controller.api;

import it.univr.track.entity.Device;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@Profile("gestione-dispositivi")
public class DeviceController {

    // add new device
    @PostMapping("/api/device")
    public boolean addDevice() {
        return true;
    }

    // read the device configuration
    @GetMapping("/api/device/{deviceId}")
    public Device readDeviceConfig(@PathVariable("deviceId") Long id) {
        return new Device();
    }

    // update device configuration
    @PutMapping("/api/device")
    public boolean editDevice() {
        return true;
    }

    // decommission a device
    @DeleteMapping("/api/device")
    public boolean deleteDevice() {
        return true;
    }

    // list all the devices that are visible for this user
    @GetMapping("/api/devices")
    public Device[] devices() {
        return new Device[0];
    }


}