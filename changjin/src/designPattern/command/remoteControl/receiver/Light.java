package designPattern.command.remoteControl.receiver;

public class Light {
    private String name;

    public Light(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void on() {
        System.out.printf("%s on", name);
    }

    public void off() {
        System.out.printf("%s off", name);
    }
}
