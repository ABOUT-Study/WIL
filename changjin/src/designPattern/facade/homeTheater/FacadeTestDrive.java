package designPattern.facade.homeTheater;

public class FacadeTestDrive {

    /*
        사용자 입장에서는 이제 서브 클래스에 대해서 알 필요가 없습니다
        단지 Facde 객체의 view_Movie() 메서드를 호출하면서 서브 클래스들의 복잡한 기능을 수행 할 수 있기 떄문입니다.
     */

    public static void main(String[] args) {
//        퍼사드 패턴을 통해 구현과 서브시스템을 분리하여 클라이언트는 퍼사드 객체 하나만 참조하므로 결합도가 낮아졌다.
        Facade facade = new Facade("콜라", "영화이름");
        facade.view_Movie();

    }
}
