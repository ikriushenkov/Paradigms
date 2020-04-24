package arrayQueue;

import java.util.Arrays;

public class ArrayQueueModule {
    // a - queue
    private static int size;
    private static int head;
    private static Object[] elements = new Object[5];

    // Inv: size >= 0 && для всех i от 0 до size - 1: a[i] != null

    // Pre: element != null
    // Post: size' = size + 1 && для всех i от 0 до size - 1: a'[i] = a[i] && a'[size] = element
    public static void enqueue(Object element) {
        assert element != null;

        ensureCapacity(size + 1);
        head = (head + 1) % elements.length;
        elements[head] = element;
        size++;
    }

    // Pre: element != null
    // Post: size' = size + 1 && для всех i от 0 до size - 1: a'[i + 1] = a[i] && a[0] = element
    public static void push(Object element) {
        assert element != null;

        ensureCapacity(size + 1);
        size++;
        elements[tail()] = element;
    }

    // Pre:
    // Post: (a' == a) && ((capacity <= elements.length && elements'.length == elements.length) ||
    // (capacity > elements.length && elements'.length = capacity * 2))
    private static void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            Object[] temp = elements;
            elements = Arrays.copyOf(Arrays.copyOfRange(elements, 0, head + 1),
                    2 * capacity);
            System.arraycopy(temp, head, elements, head + capacity + 1, temp.length - head);
        }
    }

    // Pre: size > 0
    // Post: size' == size - 1 && для всех i от 0 до size - 2: a'[i] = a[i + 1] && result == a[0]
    public static Object dequeue() {
        assert size > 0;

        Object result = elements[tail()];
        size--;
        return result;
    }

    // Pre: size > 0
    // Post: size' == size - 1 && для всех i от 0 до size - 2: a'[i] = a[i] && result = a[size - 1]
    public static Object remove() {
        assert size > 0;

        Object result = elements[head];
        head = (head - 1 + elements.length) % elements.length;
        size--;
        return result;
    }

    // Pre: size > 0
    // Post: result = a[0]
    public static Object element() {
        assert size > 0;

        return elements[tail()];
    }

    // Pre: size > 0
    // Post: result = a[size - 1]
    public static Object peek() {
        assert size > 0;

        return elements[head];
    }

    // Pre: size > 0 && 0 <= index < size
    // Post: a' == a && result == a[index]
    public static Object get(int index) {
        return elements[getIndex(index)];
    }

    // Pre: size > 0 && 0 <= index < size && element != null
    // Post: для всех i от 0 до size - 1, кроме index: a'[i] == a[i] &&  a[index] == element
    public static void set(int index, Object element) {
        elements[getIndex(index)] = element;
    }

    // Pre: size > 0 && 0 <= index < size
    // Post: a' == a && result: a[index] == elements[result]
    private static int getIndex(int index) {
        return (tail() + index) % elements.length;
    }

    // Pre:
    // Post: a' == a && result == size
    public static int size() {
        return size;
    }

    // Pre:
    // Post: a' == a && result == (size == 0)
    public static boolean isEmpty() {
        return size == 0;
    }

    // Pre:
    // Post: a' == a && size == 0
    public static void clear() {
        head = 0;
        size = 0;
    }

    // Pre: size > 0
    // Post: a' == a && result == i: elements[i - 1] == null && elements[i] != null &&
    // result == (head - size + elements.length + 1) % elements.length
    private static int tail() {
        return (head - size + elements.length + 1) % elements.length;
    }
}
