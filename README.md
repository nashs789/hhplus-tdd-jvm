# 📌 동시성 문제는 무엇 일까?
프로세스(Process)나 스레드(Thread)가 공유 자원(Shared Resource) 에 접근해 데이터 조작할 때 발생하는 문제 입니다.  
(동시에 접근(조회) 하는 것은 문제가 되지 않고, 데이터 값 변경이 문제가 된다.)

※ 공유 자원: 변수, 파일 등 

|          문제점          |                                       상태                                        |
|:---------------------:|:-------------------------------------------------------------------------------:|
| 경합 상태(Race Condition) | 여러 스레드 자원에 대한 접근 및 변경을 동시에 하려고 할 때 동시성을 제어하지 않으면 순서에 관계 없이 실행되며 순서를 보장 받을 수 없다. |
|    교착 상태(DeadLock)    |               서로 다른 스레드가 점유하고 있는 자원을 원할 때 자원에 접근을 하지 못하게 교착되는 상태                |
|   기아 상태(Starvation)   |              하나의 스레드가 우선 순위가 낮아 작업에 필요한 자원에 대해서 지속적으로 접근하지 못할 때 상태              |
|        데이터 무결성        |               여러 스레드가 동시에 자원에 접근해서 변경이 가능 하다면 데이터 무결성이 보장될 수 없다.                |

---

# 📌 동시성 제어의 목적은?

여러 스레드가 자원에 대해서 동시에 접근해 변경하려는 시도가 있어도 순차적으로 실행되어 위에서 설명한 문제점들이 발생하지 않도록 하는데 있다.  
하나의 스레드가 임계 구역(Critical Section) 에 접근 하도록 하는 것이 최종 목적이다.

운영체제의 스케줄링 정책에 의해서 스레드의 자원 할당이 정해지지만 그 안에는 순차적 제어를 위한 Lock, Semaphore 를 사용하며, 우선 순위에  
따라서 자원 획득 순서가 달라지며 그 안에는 다양한 알고리즘들에 따라서 변할 수 있다.

---

# 📌 코드로 보는 경쟁 상태(Race Condition)

```java
@Test
@DisplayName("Race Condition 으로 원치 않는 결과를 만드는 테스트 (비교용)")
void notRunSequentialExecution() throws InterruptedException {
    // given
    final int CHARGE_POINT = 100;
    final int JOB_COUNT = 20;
    int[] amount = {0};
    final CountDownLatch latch = new CountDownLatch(JOB_COUNT);
    ExecutorService executor = Executors.newFixedThreadPool(20);

    // when
    for(int currentJob = 0; currentJob < JOB_COUNT; currentJob++) {
        executor.submit(() -> {
            try {
                int gotAmount = amount[0];
                Thread.sleep(10);
                latch.countDown();
                amount[0] = gotAmount + CHARGE_POINT;
            } catch (InterruptedException e) {}
        });
    }

    latch.await();

    // then
    assertEquals(0, latch.getCount());
    log.info("Total amount = {}", amount[0]);
    assertNotEquals(CHARGE_POINT * JOB_COUNT, amount[0]);
}
```

Race Condition 을 유발하기 위해서 작성한 테스트 코드이다.   
amount[0] 에 100 원씩 충전을 20번 하는 코드 이지만 실제로 결과는 다르게 2,000 이 아닌 값이 나온다.

--- 결과 사진 ---

#### 🤔 왜 매번 실행마다 상이한 값이 나올까?

amount[0] 에 있는 값을 읽은 후 sleep 에 의해서 스레드가 잠시 멈춘 사이에 다른 스레드도 공유 자원(amount[0]) 에 접근해서 값을 읽어서 작업을 진행했기 때문에 이러한 현상이 나타났다.

```
EX)

1. A 가 amount[0] 에서 값 0 을 읽고 sleep
2. B 가 amount[0] 에서 값 0 을 읽고 sleep
3. A 가 amount[0] 에서 값 100 더해서 amount[0] 값 갱신 (amount[0] = 100)
4. B 가 amount[0] 에서 값 100 더해서 amount[0] 값 갱신 (amount[0] = 100)
```

순서 3과 4를 보면 A, B 가 sleep 전에 읽어드린 값을 기반을 값을 갱신한 것을 볼 수 있다.   
그렇기 때문에 값은 계속해서 초기화 되고 순서는 보장되지 않기 때문에 매번 결과가 상이할 수 밖에 없다.

---

# 📌 코드로 보는 교착 상태(Dead Lock)

```java
class DeadlockExample {
  private final ReentrantLock lock1 = new ReentrantLock();
  private final ReentrantLock lock2 = new ReentrantLock();

  public void method1() {
    lock1.lock();
    thread.sleep(1000);
    lock2.lock();
  }

  public void method2() {
    lock2.lock();
    thread.sleep(1000);
    lock1.lock();
  }
}

```

method1() 과 method2() 가 실행 되었을 때 lock1 은 method1 에서 접근 권한을 얻고, lock2 는 method2 에서 권한을 얻는데, 다음
작업을 위해서는 서로가 점유한 자원이 필요하기 때문에 교착된 상태를 발생 시킨다.

#### 🤔 교착 상태에서의 문제는?

처음에는 스레드 2개가 정지가 자원을 얻기 위해서 계속 경합 하지만, 이 후 다른 스레드들도 작업을 위해서 자원을 필요로 한다면 대기 상태에 들어가게
되면서 사용할 수 있는 가용 스레드가 줄어들게 된다.

---

# 📌 동시성 제어를 하는 방법은 무엇이 있을까?

단일 인스턴스 기준 입니다.

## 👉 Synchronized

메소드 혹은 블록에 사용 가능한 Java 의 예약어 이다.  
synchronized 가 사용된 메소드 혹은 코드 블록에 사용 가능하다.

1. 인스턴스 메소드

```java
class MyClass {
    private int cnt = 0;
    
    public synchronized void increase() {
        this.cnt++;
    }
    
    public synchronized int getCnt() {
        return this.cnt;
    }
}
```

인스턴스화 된 객체 this 가 동기화 대상으로 한 인스턴스의 increase() 가 호출 되었다면 같은 인스턴스 getCnt() 메소드는 호출 될 수 없다.

2. 정적 메소드

```java
class MyClass {
    private int cnt = 0;
    
    public static synchronized void increase() {
        this.cnt++;
    }
    
    public static synchronized int getCnt() {
        return this.cnt;
    }
}
```

MyClass.class 가 동기화 대상이다.  
마찬가지로 increase() 가 호출 되었다면 getCnt() 메소드는 호출 될 수 없다.

3. 정적 메소드

```java
class MyClass {
    private int cnt = 0;
    
    public void increase() {
        synchronized (this) {
          cnt++;
        }
    }
    
    public int getCnt() {
        synchronized (this) {
          return this.cnt;
        }
    }
}
```

코드의 블록 단위 동기화 범위를 지정 한다.  
인스턴스화 된 객체 this 가 동기화 대상 이다.

#### 👍 장점과 단점

|                      장점                      |                              단점                              |
|:--------------------------------------------:|:------------------------------------------------------------:|
| Java 에서 제공하기 때문에 별도의 구현 없이 사용 가능해 편리하고 간단하다. | 데드락에 대한 위험이 해소되지 않았고, 데이더 변경이 단순한 조회 성능에도 영향을 미치기 때문에 좋지 않다. |

## 👉 Lock

synchronized 는 내부에서 개발자 입장에서 알 수 있는 방법이 없는 정보들을 유틸성 메소드로 제공하며 조금 더 세밀한 동기화 범위 지정과 공정한 자원 획득을
지원 한다.

1. 공정성

```java
ReentrantLock fairLock = new ReentrantLock(true); // true: 공정(fair) / false: 비공정(non-fair)
```

기아 상태를 방지하기 위한 공정성 락을 지원한다.  
락을 획득한 순서가 아닌 락을 요청한 순서로 획득이 가능하다.

2. 유틸 메소드

- hasQueuedThreads(): 대기열 스레드 존재 여부
- isLocked(): 락이 스레드에 의해 점유되고 있는지 여부
- isHeldByCurrentThread(): 현재 락을 소유한 스레드 확인
- lockInterruptibly(): 락 점유 중단

lock 을 직접 제어 하거나 스레드 정보를 확인할 수 있는 유틸 메소드들이 존재한다.

#### 👍 장점과 단점

|                       장점                        |                          단점                          |
|:-----------------------------------------------:|:----------------------------------------------------:|
| Lock의 상태와 점유중인 스레드 추적이 가능하며 공정한 자원 점유 방법을 사용한다. | 개발자가 직접 로직을 작성해야 하며 자원 반환이 반드시 동반되야 리소스 낭비를 안할 수 있다. |

## 👉 ThreadPoolExecutor 를 이용한 스레드 제어

명시된 스레드의 개수에 따른 Pool 을 생성하고, 스레드를 반환 하는게 아닌 계속해서 재활용 하면 사용한다.  
뿐만 아니라 Max 값을 설정하여 작업에 추가 스레드가 필요한 경우 리소스 조절이 가능하다.

내부에서 사용하는 대기의 구현체, 작업 거부시 정책 등 설정 가능하기 때문에 Executors 에 비해서 더 많은 세밀한 조정이 가능하다.

※ 작업 거부: 작업이 실행되지 못하는 케이스에 동작하는 코드