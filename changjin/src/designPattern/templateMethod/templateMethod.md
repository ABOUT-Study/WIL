## 템플릿 메소드 패턴

### 템플릿 메소드 패턴이란
 - 메소드에서 알고리즘의 골격을 정의한다.
 - 알고리즘의 여러 단계 중 일부는 서브클래스에서 구현할 수 있다.
 - 템플릿 메소드를 이용하면 알고리즘의 구조는 그대로 유지하면서 서브클래스에서 특정 단계를 재정의 할 수 있다.

### 클래스 다이어그램
![img.png](img.png)

### 커피와 차를 만드는 방법
```java
1. 커피 만드는 법

1) 물을 끓인다.

2) 끓는 물에 커피를 우려낸다.

3) 커피를 컵에 따른다.

4) 설탕과 우유를 추가한다.

        
2. 홍차 만드는 법

1) 물을 끓인다.

2) 끓는 물에 차를 우려낸다.

3) 차를 컵에 따른다.

4) 레몬을 추가한다.
```

```java
public class Coffee {

	void prepareRecipe() {
		boilWater();
		brewCoffeeGrinds();
		pourInCup();
		addSugarAndMilk();
	}

	public void boilWater() {
		System.out.println("물 끓이는 중");
	}

	public void breqCoffeeGrinds() {
		System.out.println("필터를 통해 커피를 우려내는 중");
	}

	public void pourInCup() {
		System.out.println("컵에 따르는 중");
	}

	public void addSugarAndMilk() {
		System.out.println("설탕과 우유를 추가하는 중");
	}

}
```

```java
public class Tea {

	void prepareRecipe() {
		boilWater();
		steepTeaBag();
		pourInCup();
		addLemon();
	}

	public void boilWater() {
		System.out.println("물 끓이는 중");
	}

	public void steepTeaBag() {
		System.out.println("차를 우려내는 중");
	}

	public void pourInCup() {
		System.out.println("컵에 따르는 중");
	}

	public void addLemon() {
		System.out.println("레몬을 추가하는 중");
	}
}
```

```java
void prepareRecipe() {
    // brew와 condiments를 추상화해 구상 클래스에서 정의하도록 한다.

	boilWater();
	brew();

	pourInCup();

	addCondiments();

}
```

```java
1. Tea 객체를 만들고.
Tea myTea = new Tea();

2. 템플릿 메소드를 호출한다.
myTea.prepareRecipe(); 카페인 음료를 만들기 위한 알고리즘이 돌아간다.

3. 물을 끓인다.
boilWater(); 이 단계는 CaffeineBeverage 에서 처리된다.

4. 이제 차를 우려낸다.
brew(); 이방법은 서브클래스만 알고있다.

5. 차를 컵에 따른다.
pourInCup(); 이 단계도 공통적인 부분이기 때문에 Caffeinebeverage 에서 맡아서 처리된다.

6. 마지막으로 첨가물을 추가한다.
addCondiments(); 첨가물은 음료마다 다르기때문에 서브클래스에서 처리된다.
```

### 템플릿 메소드와 후크

- 후크(hook)는 추상클래스에서 선언되는 메소드긴 하지만 기본적인 내용만 구현되어 있거나 아무 코드도 들어있지 않은 메소드 이다.
- 이렇게 하면 서브클래스 입장에서는 다양한 위치에서 알고리즘에 끼어들수 있다.

