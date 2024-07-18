import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author ZaiJian
 * @date 2024/7/14
 */
public class Main5 {

    public static void main(String[] args) throws InterruptedException {
        flatMap();
        System.out.println("================");
    }

    static void flatMap() throws InterruptedException {
        var latch = new CountDownLatch(1);
        Flux.just(
                List.of(1, 2, 3),
                List.of(4, 5, 6)
            )
            .flatMap(e ->
                Flux.fromIterable(e).delayElements(Duration.ofSeconds(1))
            )
            .doOnSubscribe(e -> System.out.println("subscribed!"))
            .doFinally(e -> latch.countDown())
            .subscribe(System.out::println);
        latch.await();
    }

}
