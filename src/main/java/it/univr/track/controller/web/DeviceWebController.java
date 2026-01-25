package it.univr.track.controller.web;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Collections;

@Controller
@Profile("gestione-dispositivi")
public class DeviceWebController {

    // provisioning of a new device (QR-code?)
    @RequestMapping("/web/provision")
    public String provision() {
        return "provision";
    }

    // decommissioning of an old device
    @RequestMapping("/web/decommission")
    public String decommission() {
        return "decommission";
    }

    // list devices
    @RequestMapping("/web/devices")
    public String devices(Model model) {
        model.addAttribute("devices", Collections.emptyList());
        return "devices";
    }

    // view device configuration
    @RequestMapping("/web/configDevice")
    public String configDevice() {
        return "configDevice";
    }

    // edit device configuration
    @RequestMapping("/web/editConfigDevice")
    public String editConfigDevice() {
        return "editConfigDevice";
    }

    // send configuration to device
    @RequestMapping("/web/sendConfigDevice")
    public String sendConfigDevice() {
        return "sendConfigDevice";
    }

}
