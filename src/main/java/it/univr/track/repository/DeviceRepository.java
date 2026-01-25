package it.univr.track.repository;

import it.univr.track.entity.Device;
import org.springframework.data.repository.CrudRepository;

public interface DeviceRepository extends CrudRepository<Device, Long> {

}