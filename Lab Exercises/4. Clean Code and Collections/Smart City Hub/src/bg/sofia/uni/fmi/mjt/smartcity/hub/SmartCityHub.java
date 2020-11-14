package bg.sofia.uni.fmi.mjt.smartcity.hub;

import bg.sofia.uni.fmi.mjt.smartcity.device.SmartDevice;
import bg.sofia.uni.fmi.mjt.smartcity.enums.DeviceType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class SmartCityHub {

    private int smartCamerasCount;
    private int smartLampsCount;
    private int smartTrafficLightsCount;
    private Map<String, SmartDevice> devicesById;
    private Set<SmartDevice> devicesInOrderOfRegistration;
    private Set<SmartDevice> devicesSortedByConsumption;

    public SmartCityHub() {
        smartCamerasCount = 0;
        smartLampsCount = 0;
        smartTrafficLightsCount = 0;
        devicesById = new HashMap<>();
        devicesInOrderOfRegistration = new LinkedHashSet<>();
        devicesSortedByConsumption = new TreeSet<>(totalConsumptionComparator());
    }

    private Comparator<? super SmartDevice> totalConsumptionComparator() {
        return new Comparator<>() {
            @Override
            public int compare(SmartDevice o1, SmartDevice o2) {
                return Double.compare(getTotalConsumption(o1), getTotalConsumption(o2));
            }

            private double getTotalConsumption(SmartDevice device) {
                long hours = Duration.between(device.getInstallationDateTime(), LocalDateTime.now()).toHours();
                return hours * device.getPowerConsumption();
            }
        };
    }

    /**
     * Adds a @device to the SmartCityHub.
     *
     * @throws IllegalArgumentException         in case @device is null.
     * @throws DeviceAlreadyRegisteredException in case the @device is already registered.
     */
    public void register(SmartDevice device) throws DeviceAlreadyRegisteredException {
        if (device == null) {
            throw new IllegalArgumentException("Device cannot be null!");
        }

        if (devicesById.containsKey(device.getId())) {
            throw new DeviceAlreadyRegisteredException("Device is already registered");
        }

        devicesById.put(device.getId(), device);
        devicesInOrderOfRegistration.add(device);
        devicesSortedByConsumption.add(device);
        increaseDeviceCount(device.getType());
    }

    /**
     * Removes the @device from the SmartCityHub.
     *
     * @throws IllegalArgumentException in case null is passed.
     * @throws DeviceNotFoundException  in case the @device is not found.  b
     */
    public void unregister(SmartDevice device) throws DeviceNotFoundException {
        if (device == null) {
            throw new IllegalArgumentException("Device cannot be null!");
        }

        if (!devicesById.containsKey(device.getId())) {
            throw new DeviceNotFoundException("Device is not found");
        }

        devicesById.remove(device.getId());
        devicesInOrderOfRegistration.remove(device);
        devicesSortedByConsumption.remove(device);
        decreaseDeviceCount(device.getType());
    }

    /**
     * Returns a SmartDevice with an ID @id.
     *
     * @throws IllegalArgumentException in case @id is null.
     * @throws DeviceNotFoundException  in case device with ID @id is not found.
     */
    public SmartDevice getDeviceById(String id) throws DeviceNotFoundException {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null!");
        }

        if (!devicesById.containsKey(id)) {
            throw new DeviceNotFoundException("Device is not found");
        }

        return devicesById.get(id);
    }

    /**
     * Returns the total number of devices with type @type registered in SmartCityHub.
     *
     * @throws IllegalArgumentException in case @type is null.
     */
    public int getDeviceQuantityPerType(DeviceType type) {
        return getDeviceCount(type);
    }

    /**
     * Returns a collection of IDs of the top @n devices which consumed
     * the most power from the time of their installation until now.
     * <p>
     * The total power consumption of a device is calculated by the hours elapsed
     * between the two LocalDateTime-s: the installation time and the current time (now)
     * multiplied by the stated nominal hourly power consumption of the device.
     * <p>
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     *
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<String> getTopNDevicesByPowerConsumption(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("@n can't be negative");
        }

        Collection<String> firstNDevices = new TreeSet<>();
        Iterator<SmartDevice> iterator = devicesSortedByConsumption.iterator();
        while (n > 0 && iterator.hasNext()) {
            firstNDevices.add(iterator.next().getId());
            n--;
        }

        return firstNDevices;
    }

    /**
     * Returns a collection of the first @n registered devices, i.e the first @n that were added
     * in the SmartCityHub (registration != installation).
     * <p>
     * If @n exceeds the total number of devices, return all devices available sorted by the given criterion.
     *
     * @throws IllegalArgumentException in case @n is a negative number.
     */
    public Collection<SmartDevice> getFirstNDevicesByRegistration(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("@n can't be negative");
        }

        if (n > devicesInOrderOfRegistration.size()) {
            return devicesInOrderOfRegistration;
        }

        Collection<SmartDevice> firstNDevices = new LinkedHashSet<>();
        Iterator<SmartDevice> iterator = devicesInOrderOfRegistration.iterator();
        while (n > 0) {
            firstNDevices.add(iterator.next());
            n--;
        }

        return firstNDevices;
    }

    private void increaseDeviceCount(DeviceType type) {
        switch (type) {
            case CAMERA -> smartCamerasCount++;
            case TRAFFIC_LIGHT -> smartTrafficLightsCount++;
            case LAMP -> smartLampsCount++;
        }
    }

    private void decreaseDeviceCount(DeviceType type) {
        switch (type) {
            case CAMERA -> smartCamerasCount--;
            case TRAFFIC_LIGHT -> smartTrafficLightsCount--;
            case LAMP -> smartLampsCount--;
        }
    }

    private int getDeviceCount(DeviceType type) {
        return switch (type) {
            case CAMERA -> smartCamerasCount;
            case LAMP -> smartLampsCount;
            case TRAFFIC_LIGHT -> smartTrafficLightsCount;
        };
    }
}