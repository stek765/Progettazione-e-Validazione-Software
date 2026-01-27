package it.univr.track.device;

import it.univr.track.entity.Device;
import org.springframework.data.repository.CrudRepository;

// this file-repository provides CRUD operations for Device entities
// for example, it allows saving, deleting, and finding devices in the database.
public interface DeviceRepository extends CrudRepository<Device, Long> {

}