package designPattern.command.simpleCommand;
// concreteCommand
public class LightOnCommand implements Command{
    Light light;

    /*
    생성자에 이 커맨드 객체로 제어할 특정 조명의 정보가 전달된다.
    그 객체는 light라는 인스턴스 변수에 저장된다.
    execute() 메소드가 호출되면 light 객체가 요청의 리시버가 된다.
     */
    public LightOnCommand(Light light) {
        this.light = light;
    }

    @Override
    public void execute() {
        light.on();
    }
}
