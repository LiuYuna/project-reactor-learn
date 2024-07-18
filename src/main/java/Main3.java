import reactor.core.publisher.Flux;

/**
 * 响应流转阻塞结构
 *
 * @author ZaiJian
 * @date 2024/7/14
 */
public class Main3 {

    public static void main(String[] args) {
        Flux.range(1, 10)
            .toIterable()
            .forEach(System.out::println);

        System.out.println("===================");

        Flux.range(1, 10)
            .toStream()
            .forEach(System.out::println);

        System.out.println("===================");

        var first = Flux.range(1, 10)
            .blockFirst();
        System.out.println(first);

        System.out.println("===================");

        var last = Flux.range(1, 10)
            .blockLast();
        System.out.println(last);

    }

}
