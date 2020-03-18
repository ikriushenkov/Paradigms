package queue;

import java.util.function.Predicate;

public interface Queue {
    // a - queue

    // Inv: size >= 0 && для всех i от 0 до size - 1: a[i] != null

    // Pre: element != null
    // Post: size' = size + 1 && для всех i от 0 до size - 1: a'[i] = a[i] && a'[size] = element
    void enqueue(Object element);

    // Pre: size > 0
    // Post: a' == a && result = a[0]
    Object element();

    // Pre: size > 0
    // Post: size' == size - 1 && для всех i от 0 до size - 2: a'[i] = a[i + 1] && result == a[0]
    Object dequeue();

    // Pre:
    // Post: a' == a && result == size
    int size();

    // Pre:
    // Post: a' == a && result == (size == 0)
    boolean isEmpty();

    // Pre:
    // Post: size == 0
    void clear();

    // Pre: predicate != null
    // Post: для всех i: predicate.test(a[i]) is false, a' contains a[i] && (для всех i' и j' от 0 до
    // size' - 1 существуют i и j от 0 до size - 1 такие, что a'[i'] == a[i] && a'[j'] == a[j]:
    // i < j -> i' < 'j)
    void removeIf(Predicate<Object> predicate);

    // Pre: predicate != null
    // Post: для всех i: predicate.test(a[i]) is true, a' contains a[i] && (для всех i' и j' от 0 до
    // size' - 1 существуют i и j от 0 до size - 1 такие, что a'[i'] == a[i] && a'[j'] == a[j]:
    // i < j -> i' < 'j)
    void retainIf(Predicate<Object> predicate);

    // Pre: predicate != null
    // Post: для всех i от 0 до k: k - минимальное такое, что predicate(k) is false:
    // a'[i] == a[i]
    void takeWhile(Predicate<Object> predicate);

    // Pre: predicate != null
    // Post: для всех i от 0 до k: k - минимальное такое, что predicate(k) is false:
    // не находятся в a' && (для всех i' и j' от 0 до size' - 1 существуют i и j от k до size - 1 такие,
    // что a'[i'] == a[i] && a'[j'] == a[j]: i < j -> i' < 'j)
    void dropWhile(Predicate<Object> predicate);
}
