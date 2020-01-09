package com.example.distancetracker.Utilities;

public class Car implements Comparable<Car> {

    public String id;
    public String name;
    public String model;
    public String carNickname;

    public Car(String id, String name, String model, String carNickname) {
        this.id = id;
        this.name = name;
        this.carNickname = carNickname;
        this.model = model;

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getModel() {
        return model;
    }

    public String getCarNickname() {
        return carNickname;
    }

    @Override
    public int compareTo(Car car) {
        int result = name.compareTo(car.getName());

        return result;
    }
}

