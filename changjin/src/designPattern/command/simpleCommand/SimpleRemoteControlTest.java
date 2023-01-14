package designPattern.command.simpleCommand;

public class SimpleRemoteControlTest {

    public static void main(String[] args) {

        SimpleRemoteControl remoteControl = new SimpleRemoteControl(); // 인보커
        Light light = new Light("test"); //
        LightOnCommand lightOn = new LightOnCommand(light); // 4

        remoteControl.setCommand(lightOn); // 인보커에 커맨드 전달
        remoteControl.buttonWasPressed(); // 인보커가 작업 실행



    }
}
