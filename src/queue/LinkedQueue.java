package queue;

public class LinkedQueue extends AbstractQueue {
    private Node begin;
    private Node end;

    @Override
    protected void myEnqueue(Object element) {
        end = new Node(element, end, null);
        if (size == 1) {
            begin = end;
        }
    }

    @Override
    protected Object myElement() {
        return begin.value;
    }

    @Override
    protected void myDequeue() {
        begin = begin.next;
    }

    @Override
    protected void myClear() {
        end = begin = null;
    }

    private static class Node {
        private Object value;
        private Node prev;
        private Node next;

        public Node(Object value, Node prev, Node next) {
            assert value != null;

            this.value = value;
            this.prev = prev;
            this.next = next;
            if (prev != null)  {
                prev.next = this;
            }
            if (next != null) {
                next.prev = this;
            }
        }
    }
}
