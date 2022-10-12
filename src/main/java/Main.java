import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static AtomicInteger counterA = new AtomicInteger(0);
    public static AtomicInteger counterB = new AtomicInteger(0);
    public static AtomicInteger counterC = new AtomicInteger(0);

    public static BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);

    public static String maxA;
    public static String maxB;
    public static String maxC;

    public static boolean op = true;
    public static boolean op1 = true;
    public static boolean op2 = true;
    public static boolean op3 = true;

    static int arraySize = 10_000;
    static int textLength = 100_000;

    public static void main(String[] args) {

        Thread arrayFilling;
        Thread checkA;
        Thread checkB;
        Thread checkC;
        long currentTime = System.currentTimeMillis();

        arrayFilling = new Thread(()->{
            for (int i = 0; i < arraySize; i++) {
                String text = generateText("abc",textLength);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            op = false;
        });

        checkA = new Thread(()->{
            while (op|!queueA.isEmpty()) {
                try {
                    String a = null;
                    a = queueA.take();
                    AtomicInteger cA = countSymbols(a,'a');
                    if (counterA.compareAndSet(counterA.get(),cA.get())) {
                        maxA = a;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            op1 = false;
        });

        checkB = new Thread(()->{
            while (op|!queueB.isEmpty()) {
                try {
                    String b = null;
                    b = queueB.take();
                    AtomicInteger bB = countSymbols(b,'b');
                    if (counterB.compareAndSet(counterB.get(),bB.get())) {
                        maxB = b;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            op2 = false;
        });

        checkC = new Thread(()->{
            while (op|!queueC.isEmpty()) {
                try {
                    String c = null;
                    c = queueC.take();
                    AtomicInteger cC = countSymbols(c,'c');
                    if (counterC.compareAndSet(counterC.get(),cC.get())) {
                        maxC = c;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            op3 = false;
        });

        arrayFilling.start();
        checkA.start();
        checkB.start();
        checkC.start();

        try {
            arrayFilling.join();
            checkA.join();
            checkB.join();
            checkC.join();
            System.out.println("Most A word contains " + counterA + " a symbols");
            System.out.println("Most B word contains " + counterB + " b symbols");
            System.out.println("Most C word contains " + counterC + " c symbols");
            System.out.println("Operations have took: " + (System.currentTimeMillis()-currentTime) + " ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static AtomicInteger countSymbols(String text, char ch) {
        AtomicInteger counter = new AtomicInteger(0);
        text.chars()
                .forEach(x->{
                    if (x == ch) {
                        counter.getAndIncrement();
                    }
                });
        return counter;
    }

}
