package reductions;

import java.util.OptionalDouble;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

class Average {
  private final double sum;
  private final long count;
  private Average(double sum, long count) {
    this.sum = sum;
    this.count = count;
  }
  public static Average of(double sum, long count) {
    return new Average(sum, count);
  }
  public Average merge(Average other) {
    return new Average(this.sum + other.sum, this.count + other.count);
  }
  public OptionalDouble get() {
    if (count > 0) {
      return OptionalDouble.of(this.sum / this.count);
    } else {
      return OptionalDouble.empty();
    }
  }
}

public class Example1 {
  // see the JIT compilations using VM option: -XX:+PrintCompilation
  public static void main(String[] args) {
//    IntStream.iterate(0, x -> x + 1)
//        .forEach(System.out::println);

//    ThreadLocalRandom.current().doubles(10_000, -1, +1)
    long start = System.nanoTime();
    DoubleStream.iterate(0.0, x -> ThreadLocalRandom.current().nextDouble(-1, +1))
    // generate is "free of order", so the stream runs that way
//    DoubleStream.generate(() -> ThreadLocalRandom.current().nextDouble(-1, +1))
        // doubles (currently) has horrible concurrency bug
        // Heinz Kabutz is working a fix :)
//    ThreadLocalRandom.current().doubles(1_000_000_000, -1, +1)
        .parallel()
//        .limit(3_000_000_000L)
        .limit(3_000_000_000L)
        .mapToObj(d -> Average.of(d, 1L))
//        .reduce(Average.of(0, 0), (a1, a2) -> a1.merge(a2))
        .reduce(Average.of(0, 0), Average::merge)
        .get()
        .ifPresentOrElse(d -> System.out.println("Mean is " + d),
            () -> System.out.println("no data in stream"));
    long time = System.nanoTime() - start;
    System.out.printf("took %7.3f seconds\n", (time / 1_000_000_000.0));
  }
}
