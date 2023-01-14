package designPattern.adapter.duck;

import java.util.List;

public class DuckTestDrive {
    public static void main(String[] args) {
        MallardDuck duck = new MallardDuck();
        WildTurkey turkey = new WildTurkey();

        // 터키 어댑터 클래스는 어댑티(turkey)의 기능을 수정하고 확장함
        Duck turkeyAdapter = new TurkeyAdapter(turkey);

        System.out.println("The Turkey says…");
        turkey.gobble();
        turkey.fly();
        System.out.println("\nThe Duck says…");
        testDuck(duck);
        System.out.println("\nThe TurkeyAdapter says…");
        testDuck(turkeyAdapter);
        List list = List.of(1, 2);
        list.add(1);


//        DuckQuack duckQuack = new DuckQuack(turkeyAdapter);
//        DuckQuack duckQuack2 = new DuckQuack(duck);
//        duckQuack.quack();
//        duckQuack2.quack();

    }

    static void testDuck(Duck duck) {
        /*
            1. 클라이언트에서 타겟 인터페이스를 사용하여 메소드를 호출함으로써 어댑터에 요청을 한다.
            2. 어댑터에서는 어댑티 인터페이스를 사용하여 그 요청을 어댑티 에 대한 하나 이상의 메소드를 호출로 변환한다.
            3. 클라이언트에서는 호출 결과를 받긴 하지만 중간에 어댑터가 껴 있는지는 전혀 알지 못한다.
         */
        duck.quack();
        duck.fly();
    }
}
