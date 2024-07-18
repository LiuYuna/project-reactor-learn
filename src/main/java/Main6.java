import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 冷热数据流
 * 冷数据流只会在被订阅时生成数据
 * 热数据流无论订阅多少次都只会生成一次数据
 *
 * @author ZaiJian
 * @date 2024/7/14
 */
public class Main6 {

    public static void main(String[] args) throws InterruptedException {
        coolByDefer();
        System.out.println("===================");
        hotByPublish();
        System.out.println("===================");
        hotByCache();
        System.out.println("===================");
        hotByShare();
    }

    static void coolByDefer() throws InterruptedException {
        var latch = new CountDownLatch(1);
        var flux = Flux.defer(() -> {
                System.out.println("【冷数据流】生成数据");
                return Flux.range(1, 10);
            })
            .doFinally(e -> latch.countDown())
            .doOnSubscribe(e -> System.out.println("subscribed!"));
        System.out.println("coolByDefer 开始订阅");
        flux.subscribe(System.out::println);
        latch.await();
    }

    static void hotByPublish() throws InterruptedException {
        var latch = new CountDownLatch(1);

        var publish = Flux.range(1, 10)
            .delayElements(Duration.ofMillis(100))
            .doOnSubscribe(e -> System.out.println("subscribed!"))
            .doFinally(e -> latch.countDown())
            .publish();

        System.out.println("hotByPublish 开始订阅");

        publish.subscribe(e -> System.out.println("subscribe1: " + e));
        publish.subscribe(e -> System.out.println("subscribe2: " + e));
        publish.connect();

        latch.await();
    }

    static void hotByCache() throws InterruptedException {
        var latch = new CountDownLatch(1);

        var publish = Flux.range(1, 10)
            .doOnSubscribe(e -> System.out.println("subscribed!"))
            .doFinally(e -> latch.countDown())
            .cache(Duration.ofSeconds(1));

        System.out.println("hotByCache 开始订阅");

        publish.subscribe(e -> System.out.println("subscribe1: " + e));
        Thread.sleep(2 * 1000); // 让 subscribe2 错过缓存，重新订阅消费
        publish.subscribe(e -> System.out.println("subscribe2: " + e));

        latch.await();
    }

    static void hotByShare() throws InterruptedException {
        var latch = new CountDownLatch(1);

        var publish = Flux.range(1, 10)
            .delayElements(Duration.ofMillis(100))
            .doFinally(e -> latch.countDown())
            .share();

        System.out.println("hotByShare 开始订阅");

        // 毫秒延时不太准确

        // 订阅者1：无延时
        publish.subscribe(e -> System.out.println("subscribe1: " + e));

        // 订阅者2：延时200ms
        TimeUnit.MILLISECONDS.sleep(200);
        publish.subscribe(e -> System.out.println("subscribe2: " + e));

        // 订阅者3：延时800ms
        TimeUnit.MILLISECONDS.sleep(600);
        publish.subscribe(e -> System.out.println("subscribe3: " + e));

        latch.await();
    }

}
