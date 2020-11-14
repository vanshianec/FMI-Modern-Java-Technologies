package bg.sofia.uni.fmi.mjt.smartcity.device;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class BaseDevice implements SmartDevice {

    private String name;
    private double powerConsumption;
    private LocalDateTime installationDateTime;

    protected BaseDevice(String name, double powerConsumption, LocalDateTime installationDateTime) {
        this.name = name;
        this.powerConsumption = powerConsumption;
        this.installationDateTime = installationDateTime;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPowerConsumption() {
        return powerConsumption;
    }

    @Override
    public LocalDateTime getInstallationDateTime() {
        return installationDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseDevice that = (BaseDevice) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
