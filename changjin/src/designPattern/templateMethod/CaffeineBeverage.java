package designPattern.templateMethod;

public abstract class CaffeineBeverage {

    // 알고리즘을 담고 있는 메소드는 오버라이드하지 못하도록 final 처리
    final void prepareRecipe() {

        boilWater();

        brew();

        pourInCup();

        addCondiments();

        if (customerWantsCondiments()) {
            addCondiments();
        }
    }

    protected abstract void brew();

    public abstract void addCondiments();

    void boilWater() {

        System.out.println("물 끓이는 중");

    }

    void pourInCup() {
        System.out.println("컵에 따르는 중");
    }

    boolean customerWantsCondiments() { // 이 메소드는 서브클래스에서 필요에 따라
        return true; // 오버라이드 할수 있는 메소드이므로 후크이다.
    }
}