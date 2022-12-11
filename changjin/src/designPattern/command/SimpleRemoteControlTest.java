package designPattern.command;

import designPattern.singleton.Singleton;

public class SimpleRemoteControlTest {

    public static void main(String[] args) {
        SimpleRemoteControl remoteControl = new SimpleRemoteControl(); // 2
        Light light = new Light(); // 3
        LightOnCommand lightOn = new LightOnCommand(light); // 4

        remoteControl.setCommand(lightOn); // 5
        remoteControl.buttonWasPressed();
    }
}
