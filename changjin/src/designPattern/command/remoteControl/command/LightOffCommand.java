package designPattern.command.remoteControl.command;

import designPattern.command.simpleCommand.Command;
import designPattern.command.simpleCommand.Light;

public class LightOffCommand implements Command {
    Light light;

    public LightOffCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.off();
    }
}
