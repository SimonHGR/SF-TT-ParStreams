package sideeffects;

import java.util.stream.IntStream;

public class Example {
  public static long counter = 0;

  public static void main(String[] args) {
    final Object rendezvous = new Object();
    /*long answer = */IntStream.range(0, 1_000_000)
        .parallel()
        .map(x -> {
//          synchronized (rendezvous) { // or AtomicLong BUT REALLY, NAH, don't!
            counter++; // SIDE-EFFECT--DON'T DO THIS in a stream
//          }
          return x * 2;
        })
//            .count();
        .forEach(System.out::println);
    /*
    Side-effect...
    Effect is something that happens during computation that can affect
    subsequent computations
    Side-effect, any effect other than the RETURN VALUE of a function invocation

    Practical functional programming prohibits "visible" side effects
    Side-effects in concurrent programming are very dangerous
     */

//    System.out.println(answer);
    System.out.println(counter);
  }
}
