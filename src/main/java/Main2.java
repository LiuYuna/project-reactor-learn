import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

/**
 * @author ZaiJian
 * @date 2024/7/14
 */
public class Main2 {

    public static void main(String[] args) throws InterruptedException {
        concat();
        System.out.println("======================");
        merge();
        System.out.println("======================");
        generate();
    }

    static void concat() throws InterruptedException {
        var latch = new CountDownLatch(1);
        Flux.concat(
                Flux.range(1, 3)
                    .delayElements(Duration.ofSeconds(1))
                    .doOnSubscribe(e -> System.out.println("concat_1 订阅")),
                Flux.range(4, 3)
                    .delayElements(Duration.ofSeconds(1))
                    .doOnSubscribe(e -> System.out.println("concat_1 订阅"))
            )
            .doFinally(e -> latch.countDown())
            .subscribe(s -> System.out.println("Subscribed: " + s));
        latch.await();
    }

    static void merge() throws InterruptedException {
        var latch = new CountDownLatch(1);
        Flux.merge(
                Flux.range(1, 3)
                    .delayElements(Duration.ofSeconds(1))
                    .doOnSubscribe(e -> System.out.println("merge_1 订阅")),
                Flux.range(4, 3)
                    .delayElements(Duration.ofSeconds(1))
                    .doOnSubscribe(e -> System.out.println("merge_1 订阅"))
            )
            .doFinally(e -> latch.countDown())
            .subscribe(s -> System.out.println("Subscribed: " + s));
        latch.await();
    }

    static void generate() throws InterruptedException {
        var latch = new CountDownLatch(1);
        Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next(state * 3);
                    if (state == 10) {
                        sink.complete();
                    }
                    return ++state;
                }
            )
            .doFinally(e -> latch.countDown())
            .subscribe(state -> System.out.println("Subscribed: " + state));
        latch.await();
    }

}
