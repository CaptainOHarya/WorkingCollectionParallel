package working_collection_parallel.home_work04;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Leonid Zulin
 * @date 01.02.2023 20:57
 */
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author Leonid Zulin
 * @date 01.02.2023 20:57
 */
public class MainParallel {

    private static final int NUMBER_OF_TEXTS = 10_000;
    private static final int TEXT_LENGTH = 100_000;
    private static final int QUEUE_SIZE = 100;
    private static BlockingQueue<String> blockingQueueA = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private static BlockingQueue<String> blockingQueueB = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private static BlockingQueue<String> blockingQueueC = new ArrayBlockingQueue<>(QUEUE_SIZE);

    public static void main(String[] args) throws InterruptedException {
        Thread threadTextGenerator;
        Thread threadA;
        Thread threadB;
        Thread threadC;

        threadTextGenerator = new Thread(() -> {
            String text;
            for (int i = 0; i < NUMBER_OF_TEXTS; i++) {
                text = generateText("abc", TEXT_LENGTH);
                try {
                    blockingQueueA.put(text);
                    blockingQueueB.put(text);
                    blockingQueueC.put(text);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
        threadTextGenerator.start();

        // using lambda expressions
        threadA = new Thread(() -> {
            char letter = 'a';
            int maxNumberA = findMaxNumberOfChar(blockingQueueA, letter);
            System.out.println("Максимальное количество буквы " + letter + " в тексте = " + maxNumberA);
        });
        threadA.start();

        // using the Runnable interface
        threadB = new Thread(new ThreadB(blockingQueueB));
        threadB.start();

        // using the Thread class
        threadC = new ThreadC(blockingQueueC);
        threadC.start();

        threadA.join();
        threadB.join();
        threadC.join();

        threadTextGenerator.join();
        System.out.println("Поток Main ends!!!");

    }

    public static int findMaxNumberOfChar(BlockingQueue<String> queue, char symbol) {
        int count = 0;
        int maxCount = 0;
        char[] charArray;

        try {
            for (int i = 0; i < NUMBER_OF_TEXTS; i++) {
                charArray = queue.take().toCharArray();
                for (char s : charArray) {
                    if (s == symbol) {
                        count++;
                    }
                }
                if (count > maxCount) {
                    maxCount = count;
                }
                count = 0;

            }

        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + "был прерван");
            return -1;
        }
        return maxCount;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}


class ThreadB implements Runnable {
    BlockingQueue<String> blockingQueueB;

    public ThreadB(BlockingQueue<String> blockingQueueB) {
        this.blockingQueueB = blockingQueueB;
    }

    @Override
    public void run() {
        char letter = 'b';
        int maxNumberB = MainParallel.findMaxNumberOfChar(blockingQueueB, letter);
        System.out.println("Максимальное количество буквы " + letter + " в тексте = " + maxNumberB);
    }
}

class ThreadC extends Thread {
    BlockingQueue<String> blockingQueueC;
    public ThreadC(BlockingQueue<String> blockingQueueC) {
        this.blockingQueueC = blockingQueueC;
    }
    @Override
    public void run() {
        char letter = 'c';
        int maxNumberC = MainParallel.findMaxNumberOfChar(blockingQueueC, letter);
        System.out.println("Максимальное количество буквы " + letter + " в тексте = " + maxNumberC);
    }
}



