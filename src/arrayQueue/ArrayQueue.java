package arrayQueue;

import java.util.Arrays;

public class ArrayQueue {
    // a - queue
    private int size;
    private int head;
    private Object[] elements = new Object[5];

    // Inv: size >= 0 && для всех i от 0 до size - 1: a[i] != null

    // Pre: element != null
    // Post: size' = size + 1 && для всех i от 0 до size - 1: a'[i] = a[i] && a'[size] = element
    public void enqueue(Object element) {
        assert element != null;

        ensureCapacity(size + 1);
        head = (head + 1) % elements.length;
        elements[head] = element;
        size++;
    }

    // Pre: element != null
    // Post: size' = size + 1 && для всех i от 0 до size - 1: a'[i + 1] = a[i] && a[0] = element
    public void push(Object element) {
        assert element != null;

        ensureCapacity(size + 1);
        size++;
        elements[tail()] = element;
    }

    // Pre:
    // Post: (a' == a) && ((capacity <= elements.length && elements' == elements) ||
    // (capacity > elements.length && elements'.length = capacity * 2 && для i от 0 до head:
    // elements'[i] == elements[i] && для i от tail до elements.length - 1:
    // elements'[i] == elements[i - capacity]))
    private void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            Object[] temp = elements;
            elements = Arrays.copyOf(Arrays.copyOfRange(elements, 0, head + 1),
                    2 * capacity);
            System.arraycopy(temp, head, elements, head + capacity + 1, temp.length - head);
        }
    }

    // Pre: size > 0
    // Post: size' == size - 1 && для всех i от 0 до size - 2: a'[i] = a[i + 1] && result == a[0]
    public Object dequeue() {
        assert size > 0;

        Object result = elements[tail()];
        size--;
        return result;
    }

    // Pre: size > 0
    // Post: size' == size - 1 && для всех i от 0 до size - 2: a'[i] = a[i] && result = a[size - 1]
    public Object remove() {
        assert size > 0;

        Object result = elements[head];
        head = (head - 1 + elements.length) % elements.length;
        size--;
        return result;
    }

    // Pre: size > 0
    // Post: a' == a && result = a[0]
    public Object element() {
        assert size > 0;

        return elements[tail()];
    }

    // Pre: size > 0
    // Post: a' == a && result = a[size - 1]
    public Object peek() {
        assert size > 0;

        return elements[head];
    }

    // Pre: size > 0 && 0 <= index < size
    // Post: a' == a && result == a[index]
    public Object get(int index) {
        return elements[getIndex(index)];
    }

    // Pre: size > 0 && 0 <= index < size && element != null
    // Post: для всех i от 0 до size - 1, кроме index: a'[i] == a[i] &&  a[index] == element
    public void set(int index, Object element) {
        elements[getIndex(index)] = element;
    }

    // Pre: size > 0 && 0 <= index < size
    // Post: a' == a && result: a[index] == elements[result]
    private int getIndex(int index) {
        return (tail() + index) % elements.length;
    }

    // Pre:
    // Post: a' == a && result == size
    public int size() {
        return size;
    }

    // Pre:
    // Post: a' == a && result == (size == 0)
    public boolean isEmpty() {
        return size == 0;
    }

    // Pre:
    // Post: size == 0
    public void clear() {
        head = 0;
        size = 0;
    }

    // Pre: size > 0
    // Post: a' == a && result == i: elements[i - 1] == null && elements[i] != null &&
    // result == (head - size + elements.length + 1) % elements.length
    private int tail() {
        return (head - size + elements.length + 1) % elements.length;
    }
}
