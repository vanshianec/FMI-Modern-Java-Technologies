package bg.sofia.uni.fmi.mjt.smartcity.device;

import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.LocalDateTime;

public class SmartTrafficLight extends BaseDevice {

    private static int number = -1;

    public SmartTrafficLight(String name, double powerConsumption, LocalDateTime installationDateTime) {
        super(name, powerConsumption, installationDateTime);
        number++;
    }

    @Override
    public String getId() {
        return String.format("%s-%s-%d", getType().getShortName(), getName(), number);
    }

    @Override
    public DeviceType getType() {
        return DeviceType.TRAFFIC_LIGHT;
    }
}
