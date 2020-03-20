package queue;

import java.util.function.Predicate;

public abstract class AbstractQueue implements Queue {
    protected int size;

    public void enqueue(Object element) {
        assert element != null;

        size++;
        furtherEnqueue(element);
    }

    protected abstract void furtherEnqueue(Object element);

    public Object element() {
        assert size > 0;

        return furtherElement();
    }

    protected abstract Object furtherElement();

    public Object dequeue() {
        assert size > 0;

        Object result = element();
        size--;
        furtherDequeue();
        return result;
    }

    protected abstract void furtherDequeue();

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        size = 0;
        furtherClear();
    }

    protected abstract void furtherClear();

    public void removeIf(Predicate<Object> predicate) {
        int n = size;
        for(int i = 0; i < n; i++) {
            if (!predicate.test(this.element())) {
                this.enqueue(this.element());
            }
            this.dequeue();
        }
    }

    public void retainIf(Predicate<Object> predicate) {
        removeIf(predicate.negate());
    }

    private void equate(AbstractQueue queue) {
        this.clear();
        while (!queue.isEmpty()) {
            this.enqueue(queue.dequeue());
        }
    }

    public void takeWhile(Predicate<Object> predicate) {
        takeWhile(predicate, false);
    }

    private void takeWhile(Predicate<Object> predicate, boolean isDelete) {
        LinkedQueue temp = new LinkedQueue();
        while (!this.isEmpty() && predicate.test(this.element())) {
            if (isDelete) {
                this.dequeue();
            } else {
                temp.enqueue(this.dequeue());
            }
        }
        if (!isDelete) {
            this.equate(temp);
        }
    }

    public void dropWhile(Predicate<Object> predicate) {
        takeWhile(predicate, true);
    }
}
