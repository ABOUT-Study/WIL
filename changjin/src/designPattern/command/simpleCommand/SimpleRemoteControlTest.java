package designPattern.command.simpleCommand;

public class SimpleRemoteControlTest {

    public static void main(String[] args) {
        SimpleRemoteControl remoteControl = new SimpleRemoteControl(); // 2
        Light light = new Light("test"); // 3
        LightOnCommand lightOn = new LightOnCommand(light); // 4

        remoteControl.setCommand(lightOn);
        remoteControl.buttonWasPressed();


    }
}
