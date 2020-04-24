package arrayQueue;

import java.util.Arrays;

public class ArrayQueueADT {
    // a - queue
    private int size;
    private int head;
    private Object[] elements = new Object[5];

    // Inv: size >= 0 && для всех i от 0 до size - 1: a[i] != null

    // Pre: element != null && queue != null
    // Post: size' = size + 1 && для всех i от 0 до size - 1: a'[i] = a[i] && a'[size] = element
    public static void enqueue(ArrayQueueADT queue, Object element) {
        assert element != null;

        ensureCapacity(queue, queue.size + 1);
        queue.head = (queue.head + 1) % queue.elements.length;
        queue.elements[queue.head] = element;
        queue.size++;
    }

    // Pre: element != null && queue != null
    // Post: size' = size + 1 && для всех i от 0 до size - 1: a'[i + 1] = a[i] && a[0] = element
    public static void push(ArrayQueueADT queue, Object element) {
        assert element != null;

        ensureCapacity(queue, queue.size + 1);
        queue.size++;
        queue.elements[tail(queue)] = element;
    }

    // Pre: queue != null
    // Post: (a' == a) && ((capacity <= elements.length && elements'.length == elements.length) ||
    // (capacity > elements.length && elements'.length = capacity * 2))
    private static void ensureCapacity(ArrayQueueADT queue, int capacity) {
        if (capacity > queue.elements.length) {
            Object[] temp = queue.elements;
            queue.elements = Arrays.copyOf(Arrays.copyOfRange(queue.elements, 0, queue.head + 1),
                    2 * capacity);
            System.arraycopy(temp, queue.head, queue.elements, queue.head + capacity + 1,
                    temp.length - queue.head);
        }
    }

    // Pre: size > 0 && queue != null
    // Post: size' == size - 1 && для всех i от 0 до size - 2: a'[i] = a[i + 1] && result == a[0]
    public static Object dequeue(ArrayQueueADT queue) {
        assert queue.size > 0;

        Object result = queue.elements[tail(queue)];
        queue.size--;
        return result;
    }

    // Pre: size > 0 && queue != null
    // Post: size' == size - 1 && для всех i от 0 до size - 2: a'[i] = a[i] && result = a[size - 1]
    public static Object remove(ArrayQueueADT queue) {
        assert queue.size > 0;

        Object result = queue.elements[queue.head];
        queue.head = (queue.head - 1 + queue.elements.length) % queue.elements.length;
        queue.size--;
        return result;
    }

    // Pre: size > 0 && queue != null
    // Post: a' == a && result = a[0]
    public static Object element(ArrayQueueADT queue) {
        assert queue.size > 0;

        return queue.elements[tail(queue)];
    }

    // Pre: size > 0 && queue != null
    // Post: a' == a && result = a[size - 1]
    public static Object peek(ArrayQueueADT queue) {
        assert queue.size > 0;

        return queue.elements[queue.head];
    }

    // Pre: size > 0 && 0 <= index < size && queue != null
    // Post: a' == a && result == a[index]
    public static Object get(ArrayQueueADT queue, int index) {
        return queue.elements[getIndex(queue, index)];
    }

    // Pre: size > 0 && 0 <= index < size && element != null && queue != null
    // Post: для всех i от 0 до size - 1, кроме index: a'[i] == a[i] &&  a[index] == element
    public static void set(ArrayQueueADT queue, int index, Object element) {
        queue.elements[getIndex(queue, index)] = element;
    }

    // Pre: size > 0 && 0 <= index < size && queue != null
    // Post: a' == a && result: a[index] == elements[result]
    private static int getIndex(ArrayQueueADT queue, int index) {
        return (tail(queue) + index) % queue.elements.length;
    }

    // Pre: queue != null
    // Post: a' == a && result == size
    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }

    // Pre: queue != null
    // Post: a' == a && result == (size == 0)
    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.size == 0;
    }

    // Pre: queue != null
    // Post: size == 0
    public static void clear(ArrayQueueADT queue) {
        queue.head = 0;
        queue.size = 0;
    }

    // Pre: size > 0 && queue != null
    // Post: a' == a && result == i: elements[i - 1] == null && elements[i] != null &&
    // result == (head - size + elements.length + 1) % elements.length
    private static int tail(ArrayQueueADT queue) {
        return (queue.head - queue.size + queue.elements.length + 1) % queue.elements.length;
    }
}
