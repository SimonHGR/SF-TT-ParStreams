package collections;

import java.util.OptionalDouble;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.DoubleStream;

class Average {
  private double sum;
  private long count;

  public Average(double sum, long count) {
    this.sum = sum;
    this.count = count;
  }
  public void merge(Average other) {
    this.sum += other.sum;
    this.count += other.count;
  }
  public void include(double d) {
    this.sum += d;
    this.count++;
  }
  public OptionalDouble get() {
    if (count > 0) {
      return OptionalDouble.of(sum / count);
    } else {
      return OptionalDouble.empty();
    }
  }
}
public class Example {
  public static void main(String[] args) {
    long start = System.nanoTime();
    DoubleStream.generate(() -> ThreadLocalRandom.current().nextDouble(-1, +1))
        .limit(9_000_000_000L)
        .parallel()
        .collect(() -> new Average(0, 0),
            (r, d) -> r.include(d),
            (rf, ri) -> rf.merge(ri))
        .get()
        .ifPresentOrElse(d -> System.out.println("Mean " + d),
            () -> System.out.println("No data"));
    long time = System.nanoTime() - start;
    System.out.printf("Time taken %7.3f\n", (time / 1_000_000_000.0));
  }
}
