package designPattern.singleton;

public class ChocolateBoiler {
    private boolean empty;
    private boolean boiled;

    public static ChocolateBoiler uniqueInstance;

    private ChocolateBoiler() {
        this.empty = empty;
        this.boiled = boiled;
    }

    private static ChocolateBoiler getInstance() { // threadSafety 하지 않다.
        if (uniqueInstance == null) {
            uniqueInstance = new ChocolateBoiler();
        }
        return uniqueInstance;
    }

    public boolean isEmpty() {
        return empty;
    }

    public boolean isBoiled() {
        return boiled;
    }

    public void fill() {
        if (isEmpty()) {
            empty = false;
            boiled = false;
        }
    }

    public void drain() {
        if (!isEmpty() && !isBoiled()) {
            empty = true;
        }
    }

    public void boil() {
        if (!isEmpty() && !isBoiled()) {
            boiled = true;
        }
    }
}
