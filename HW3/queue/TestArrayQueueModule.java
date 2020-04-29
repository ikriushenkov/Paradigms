package queue;

public class TestArrayQueueModule {
    public static void fill() {
        for (int i = 0; i < 10; i++) {
            ArrayQueueModule.enqueue(i);
        }
    }

    public static void dump() {
        while (!ArrayQueueModule.isEmpty()) {
            System.out.println(ArrayQueueModule.size() + " " +
                    ArrayQueueModule.element() + " " + ArrayQueueModule.dequeue());
            if (!ArrayQueueModule.isEmpty()) {
                System.out.println(ArrayQueueModule.size() + " " +
                        ArrayQueueModule.peek() + " " + ArrayQueueModule.remove());
            }
        }
    }

    public static void main(String[] args) {
        fill();
        dump();
    }
}
