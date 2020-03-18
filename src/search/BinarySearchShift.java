package search;

public class BinarySearchShift {
    // Pre: (для всех i: a[i] < a[i + 1], кроме одного k: a[k - 1] > a[k] &&
    // a[0] > a[a.length() - 1]) || (для всех i: a[i] < a[i + 1]) || (a[] is empty)
    // Post: (a[i]' == a[i]) && ((ans == 0 && (a[] is empty || a[0] <= a[a.length - 1]))
    // || (ans == k: a[k - 1] > a[k]))
    public static int binarySearch(int[] a) {
        // Pre
        int l = 0, r = a.length;
        // Pre && l == 0 && r = a.length && l == l' && r == r'

        // Inv: (l == 0 || l > 0 && a[l] > a[a.length - 1]) && (r == a.length || (r < a.length &&
        // a[r] < a[a.length - 1]) && (r - l >= 0) && (r - l <= (r' - l') // 2)
        while (r - l > 0) {
            // Pre && Inv && r - l > 0
            int m = l + (r - l) / 2;
            // Pre && Inv && r - l > 0 && l <= m < r && m = (l + r) // 2
            if (a[m] > a[a.length - 1]) {
                // Pre && Inv && r - l > 0 && l <= m < r && m = l + (r - l) // 2 && a[m] > a[a.length - 1]
                l = m + 1;
                // Pre && Inv && r - l > 0 && l' <= m < r' && m = l' + (r' - l') // 2 &&
                // a[m] > a[a.length - 1] && l == m + 1 && r == r' ->
                // (r - l == r' - l' - (r' - l') // 2 - 1 <= (r' - l') // 2 && m < r -> l <= r -> r - l >= 0
            } else {
                // Pre && Inv && r - l > 0 && m = l + (r - l) // 2 && a[m] <= a[a.length - 1]
                r = m;
                // Pre && Inv && r - l > 0 && l' <= m < r' && m = l' + (r' - l') // 2 &&
                // a[m] < a[a.length - 1] && r == m && l == l' ->
                // (r - l == l' + (r' - l') // 2 - l' == (r' - l') // 2 && l <= m -> l <= r -> r - l >= 0
            }
        }
        // Pre && Inv && l == r && (l == 0 && (a[] is empty || a[l] < a[length - 1]) || (l > 0 &&
        // a[l - 1] > a[a.length - 1] && a[l] <= a[a.length - 1]) -> a[l - 1] > a[l] -> l == k
        return l;
    }

    // Pre: 0 <= l <= r <= a.length && (для всех i: a[i] < a[i + 1] ||
    // ((для всех i: a[i] < a[i + 1], кроме одного k: a[k - 1] > a[k] && a[0] > a[a.length() - 1])
    // Inv: (l == 0 || l > 0 && a[l] > a[a.length - 1]) && (r == a.length || (r < a.length
    // && a[r] < a[a.length - 1]) && (r - l >= 0) && (r - l <= (r' - l') // 2)))
    // Post: (a[i]' == a[i]) && ((ans == 0 && (a[] is empty || a[0] <= a[a.length - 1])) ||
    // (ans == k: a[k - 1] > a[k]))
    public static int recursionBinarySearch(int[] a, int l, int r) {
        if (l == r) {
            // Pre && Inv && l == r && (l == 0 && (a[] is empty || a[l] < a[length - 1]) || (l > 0 &&
            // a[l - 1] > a[a.length - 1] && a[l] <= a[a.length - 1]) -> a[l - 1] > a[l] -> l == k
            return l;
        }
        // Pre && Inv &&r - l > 0 && l == l' && r == r'
        int m = l + (r - l) / 2;
        // Pre && Inv && r - l > 0 && l <= m < r && m = (l + r) // 2 && l == l' && r == r'
        if(a[m] > a[a.length - 1]) {
            // Pre && Inv && r - l > 0 && l <= m < r && m = l + (r - l) // 2 && a[m] > a[a.length - 1]
            l = m + 1;
            // Pre && Inv && r - l > 0 && l' <= m < r' && m = l' + (r' - l') // 2 &&
            // a[m] > a[a.length - 1] && l == m + 1 && r == r' ->
            // (r - l == r' - l' - (r' - l') // 2 - 1 <= (r' - l') // 2 && m < r -> l <= r -> r - l >= 0
        } else {
            // Pre && Inv && r - l > 0 && m = l + (r - l) // 2 && a[m] <= a[a.length - 1]
            r = m;
            // Pre && Inv && r - l > 0 && l' <= m < r' && m = l' + (r' - l') // 2 &&
            // a[m] < a[a.length - 1] && r == m && l == l' ->
            // (r - l == l' + (r' - l') // 2 - l' == (r' - l') // 2 && l <= m -> l <= r -> r - l >= 0
        }
        return recursionBinarySearch(a, l, r);
    }

    // (для всех i: args[i] - integer) && (для всех i: int(args[i]) < int(args[i + 1]), кроме одного k:
    // int(args[k - 1]) > int(args[k]) && int(args[0]) > int(args[a.length() - 1])) ||
    // (для всех i: int(args[i] < int(args[i + 1]))
    public static void main(String[] args) {
        int[] a = new int[args.length];
        for (int i = 0; i < args.length; i++) {
            a[i] = Integer.parseInt(args[i]);
        }
        System.out.println(recursionBinarySearch(a, 0, a.length));
        // System.out.println(binarySearch(a));
    }
}
