package arrayQueue;

public class TestArrayQueueADT {
    public static void fill(ArrayQueueADT queue) {
        for (int i = 0; i < 10; i++) {
            ArrayQueueADT.enqueue(queue, i);
        }
    }

    public static void dump(ArrayQueueADT queue) {
        while (!ArrayQueueADT.isEmpty(queue)) {
            System.out.println(ArrayQueueADT.size(queue) + " " +
                    ArrayQueueADT.element(queue) + " " + ArrayQueueADT.dequeue(queue));
            if (!ArrayQueueADT.isEmpty(queue)) {
                System.out.println(ArrayQueueADT.size(queue) + " " +
                        ArrayQueueADT.peek(queue) + " " + ArrayQueueADT.remove(queue));
            }
        }
    }

    public static void main(String[] args) {
        ArrayQueueADT queue = new ArrayQueueADT();
        fill(queue);
        dump(queue);
    }
}
