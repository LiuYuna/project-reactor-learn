import reactor.core.publisher.Flux;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/**
 * @author ZaiJian
 * @date 2024/7/13
 */
public class Main1 {

    public static void main(String[] args) throws InterruptedException {
        groupBy();
        System.out.println("==================");
    }

    static void groupBy() throws InterruptedException {
        var latch = new CountDownLatch(1);
        Flux.range(1, 10)
            .groupBy(e -> e % 2 == 0 ? "even" : "odd")
            .doFinally(e -> latch.countDown())
            .subscribe(
                e -> e
                    .scan(new LinkedList<Integer>(), (result, element) -> {
                        result.add(element);
                        if (result.size() > 2) result.removeFirst();
                        return result;
                    })
                    .filter(list -> !list.isEmpty())
                    .subscribe(item ->
                        System.out.printf("key: %s, value: %s%n", e.key(), item)
                    )
            );
        latch.await();
    }

}
