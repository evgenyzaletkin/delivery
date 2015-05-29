package com.home.delivery.app;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Objects;

/**
 * Created by evgeny on 29.05.15.
 */
public class Waypoint {
    final List<DeliveryPart> parts;
    final String address;

    public Waypoint(List<DeliveryPart> parts) {
        Preconditions.checkNotNull(parts);
        Preconditions.checkArgument(parts.size() > 0);

        address = Utils.mapToAddress(parts.get(0).getDelivery());

        parts.forEach(p -> Preconditions.checkArgument(address.equals(Utils.mapToAddress(p.getDelivery()))));

        this.parts = parts;
    }

    public List<DeliveryPart> getParts() {
        return parts;
    }

    public String getAddress() {
        return address;
    }

    public Float getDownloadVolume() {
        return  (float) parts.stream().mapToDouble(DeliveryPart::getDownloadVolume).sum();
    }

    public Float getUploadVolume() {
        return  (float) parts.stream().mapToDouble(DeliveryPart::getUploadVolume).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Waypoint waypoint = (Waypoint) o;
        return Objects.equals(address, waypoint.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }
}
