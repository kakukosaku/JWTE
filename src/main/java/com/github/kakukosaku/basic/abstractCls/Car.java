package com.github.kakukosaku.basic.abstractCls;

import java.util.HashMap;
import java.util.Map;

public interface Car {
    void run();

    void stop();

    void echo();
}

abstract class SUVCar implements Car {

    @Override
    public void run() {
        System.out.println("start running!");
    }

    @Override
    public void stop() {
        System.out.println("stopped!");
    }

}

class SuzukiCar extends SUVCar {

    @Override
    public void echo() {
        System.out.println("biu~ from suzuki!");
    }

    public static void main(String[] args) {
        Map<Integer, String> m = new HashMap<>();
    }

}


