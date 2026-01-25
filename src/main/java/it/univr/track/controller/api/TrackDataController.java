package it.univr.track.controller.api;

import it.univr.track.entity.TrackData;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Profile("gestione-dati-tracking")
public class TrackDataController {

    // write new data
    @PostMapping("/api/trackdata")
    public boolean writeData() {
        return true;
    }

    // read data
    @GetMapping("/api/trackdata")
    public TrackData readData() {
        return new TrackData();
    }

}
