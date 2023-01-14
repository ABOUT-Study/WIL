package designPattern.adapter.duck;

public class DuckQuack {

    Duck duck;

    public DuckQuack(Duck duck) {
        this.duck = duck;
    }

    public void quack() {
        this.duck.quack();
    }
}
