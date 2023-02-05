package designPattern.templateMethod;

public class CoffeeWithHook extends CaffeineBeverage {

    @Override
    public void brew() {
        System.out.println("필터를 통해 커피를 우려내는 중");
    }

    @Override
    public void addCondiments() {
        System.out.println("설탕과 우유를 추가하는 중");
    }

    @Override
    public boolean customerWantsCondiments() {
        String answer = getUserInput();
        if (answer.toLowerCase().startsWith("Y")) {
            return true;
        } else {
            return false;
        }
    }

    private String getUserInput() {
        // 입력받는 로직
        return "Y";
    }
}
