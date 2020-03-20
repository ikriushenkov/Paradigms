package queue;

import java.util.Arrays;

public class ArrayQueue extends AbstractQueue {
    private int head;
    private Object[] elements = new Object[5];

    @Override
    protected void furtherEnqueue(Object element) {
        ensureCapacity(size);
        head = (head + 1) % elements.length;
        elements[head] = element;
    }

    private void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            Object[] temp = elements;
            elements = Arrays.copyOf(Arrays.copyOfRange(elements, 0, head + 1), 2 * capacity);
            System.arraycopy(temp, head, elements, head + capacity + 1, temp.length - head);
        }
    }

    @Override
    protected Object furtherElement() {
        return elements[tail()];
    }

    @Override
    protected void furtherDequeue() {
        // Do nothing
    }

    @Override
    protected void furtherClear() {
        head = 0;
    }

    private int tail() {
        return (head - size + elements.length + 1) % elements.length;
    }
}
