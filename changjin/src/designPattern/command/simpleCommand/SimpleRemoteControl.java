package designPattern.command.simpleCommand;

// 인보커
public class SimpleRemoteControl {

    // 인보커와 리시버(커맨드의 구현체)와의 의존성이 제거된다.

    Command slot;

    public SimpleRemoteControl() {
    }

    public void setCommand(Command command) {
        slot = command;
    }

    public void buttonWasPressed() {
        slot.execute();
    }
}