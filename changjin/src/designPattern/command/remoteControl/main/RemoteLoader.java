package designPattern.command.remoteControl.main;

import designPattern.command.remoteControl.command.LightOffCommand;
import designPattern.command.remoteControl.invoker.RemoteControl;
import designPattern.command.simpleCommand.Light;
import designPattern.command.simpleCommand.LightOnCommand;

public class RemoteLoader {
    public static void main(String[] args) {
        RemoteControl remoteControl = new RemoteControl();

        Light livingRoomLight = new Light("livingRoom");

        remoteControl.setCommand(0,
                new LightOnCommand(livingRoomLight),
                new LightOffCommand(livingRoomLight));

        /* lambda style
            구상 커맨드 객체를 람다로 바꾸면 구상 커맨드 클래스(lightOnCommand...)가 필요없다.
            하지만 이 방법은 Command 인터페이스에 추상 메소드가 하나인 경우에만 사용할 수 있다.
         */
        remoteControl.setCommand(1,
                () -> livingRoomLight.on(),
                ()-> livingRoomLight.off()
        );

        System.out.println(remoteControl);

        remoteControl.onButtonWasPushed(0);
        System.out.println();
        remoteControl.offButtonWasPushed(0);
        System.out.println();
        remoteControl.onButtonWasPushed(1);
        System.out.println();
        remoteControl.offButtonWasPushed(1);
    }
}
