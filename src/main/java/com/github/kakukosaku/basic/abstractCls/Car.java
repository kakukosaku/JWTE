package com.github.kakukosaku.basic.abstractCls;


public interface Car {
    Car run();

    Car stop();

    Car echo();
}

abstract class SUVCar implements Car {

    @Override
    public Car run() {
        System.out.println("start running!");
        return this;
    }

    @Override
    public Car stop() {
        System.out.println("stopped!");
        return this;
    }

}

class SuzukiCar extends SUVCar {

    @Override
    public Car echo() {
        System.out.println("biu~ from suzuki!");
        return this;
    }

    public static void main(String[] args) {
        Car c = new SuzukiCar();
        c.run().stop().echo();
    }

}


