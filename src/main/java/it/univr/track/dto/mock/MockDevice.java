package it.univr.track.dto.mock;

public class MockDevice {
    public String id;
    public String name;
    public String status;
    public boolean provisioned = false;
    public String macAddress = null;

    public MockDevice(String id, String name, String status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }
}