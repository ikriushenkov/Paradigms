package queue;

import java.util.function.Predicate;

public abstract class AbstractQueue implements Queue {
    protected int size;

    public void enqueue(Object element) {
        assert element != null;

        size++;
        myEnqueue(element);
    }

    protected abstract void myEnqueue(Object element);

    public Object element() {
        assert size > 0;

        return myElement();
    }

    protected abstract Object myElement();

    public Object dequeue() {
        assert size > 0;

        Object result = element();
        size--;
        myDequeue();
        return result;
    }

    protected abstract void myDequeue();

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        size = 0;
        myClear();
    }

    protected abstract void myClear();

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
        LinkedQueue temp = new LinkedQueue();
        while (!this.isEmpty() && predicate.test(this.element())) {
            temp.enqueue(this.dequeue());
        }
        this.equate(temp);
    }

    public void dropWhile(Predicate<Object> predicate) {
        while (!this.isEmpty() && predicate.test(this.element())) {
            this.dequeue();
        }
    }
}
