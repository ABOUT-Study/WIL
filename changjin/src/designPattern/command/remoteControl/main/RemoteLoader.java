package designPattern.command.remoteControl.main;

import designPattern.command.remoteControl.command.LightOffCommand;
import designPattern.command.remoteControl.invoker.RemoteControl;
import designPattern.command.simpleCommand.Light;
import designPattern.command.simpleCommand.LightOnCommand;

public class RemoteLoader {
    public static void main(String[] args) {
        RemoteControl remoteControl = new RemoteControl();

        Light livingRoomLight = new Light("livingRoom");

//        remoteControl.setCommand(0,
//                new LightOnCommand(livingRoomLight),
//                new LightOffCommand(livingRoomLight));

        remoteControl.setCommand(1,
                () -> System.out.println("112"),
                ()-> livingRoomLight.off()
        );

        System.out.println(remoteControl);

        remoteControl.onButtonWasPushed(1);
        System.out.println();
        remoteControl.offButtonWasPushed(1);
    }
}
