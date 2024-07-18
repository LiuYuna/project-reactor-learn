import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;

/**
 * 物化、非物化
 *
 * @author ZaiJian
 * @date 2024/7/14
 */
public class Main4 {

    public static void main(String[] args) throws InterruptedException {
        materialize();
        System.out.println("===================");
        dematerialize();
    }

    // 物化
    static void materialize() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Flux.range(1, 4)
            .concatWith(Flux.error(new RuntimeException("manual error")))
            .materialize()
            .doOnEach(System.out::println)
            .doFinally(e -> latch.countDown())
            .subscribe(
                System.out::println,
                System.err::println,
                latch::countDown
            );
        latch.await();
    }

    // 非物化
    static void dematerialize() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Flux.range(1, 4)
            .concatWith(Flux.error(new RuntimeException("manual error")))
            .materialize()
            .doOnEach(System.out::println)
            .doFinally(e -> latch.countDown())
            .dematerialize()
            .subscribe(
                System.out::println,
                System.err::println,
                latch::countDown
            );
        latch.await();
    }

}
