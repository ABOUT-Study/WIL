package designPattern.facade.homeTheater;

public class Beverage {

    private String name="";

    public Beverage(String name)
    {
        this.name = name;
    }

    public void Prepare()
    {
        System.out.println(name+" 음료 준비 완료 ");
    }

}
