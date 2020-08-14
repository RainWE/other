package com.other.collection.list;

import java.util.*;
import java.util.function.Consumer;

public class MyLinkedList<E> {
    transient int size = 0;

    /**
     * 指向第一个节点的指针。
     * 不变的: (first == null && last == null) ||
     *            (first.prev == null && first.item != null)
     */
    transient Node<E> first;

    /**
     * 指向最后一个节点的指针。
     * 不变的: (first == null && last == null) ||
     *            (last.next == null && last.item != null)
     */
    transient Node<E> last;

    /**
     * 构造一个空列表。
     */
    public MyLinkedList() {
    }
    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    /**
     * 构造一个包含指定集合的元素的列表，其顺序由集合的迭代器返回。
     * @param  c 将其`元素放入此列表的集合
     * @throws NullPointerException-如果指定的集合为null
     */
    public MyLinkedList(Collection<? extends E> c) {
        this();
        addAll(c);
    }

    /**
     * 按照指定集合的迭代器返回的顺序，将指定集合中的所有元素追加到此列表的末尾。
     * 如果在操作进行过程中修改了指定的集合，则此操作的行为是不确定的。
     * （请注意，如果指定的集合是此列表，并且是非空的，则将发生这种情况。）
     * @param c 包含要添加到此列表的元素的集合
     * @return {@code true} 该列表是否因通话而改变
     * @throws NullPointerException 如果指定的集合为null
     */
    public boolean addAll(Collection<? extends E> c) {
        return addAll(size, c);
    }

    /**
     * 从指定位置开始，将指定集合中的所有元素插入此列表。
     * 将当前在该位置的元素（如果有）和任何后续元素右移（增加其索引）。
     * 新元素将按照指定集合的​​迭代器返回的顺序显示在列表中。
     * @param index 从指定集合插入第一个元素的索引
     * @param c 包含要添加到此列表的元素的集合
     * @return 如果此列表因调用而更改，则为true
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException 如果指定的集合为null
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        checkPositionIndex(index);
        Object[] a = c.toArray();
        int numNew = a.length;
        if (numNew == 0)
            return false;
        Node<E> pred, succ;
        if (index == size) {
            succ = null;
            pred = last;
        } else {
            succ = node(index);
            pred = succ.prev;
        }
        //链表批量增加，是靠for循环遍历原数组，依次执行插入节点操作。
        // 对比ArrayList是通过System.arraycopy完成批量增加的
        for (Object o : a) {
            @SuppressWarnings("unchecked") E e = (E) o;
            Node<E> newNode = new Node<>(pred, e, null);
            if (pred == null)
                first = newNode;
            else
                pred.next = newNode;
            pred = newNode;
        }

        if (succ == null) {
            last = pred;
        } else {
            pred.next = succ;
            succ.prev = pred;
        }

        size += numNew;
        modCount++;
        return true;
    }
    private void checkPositionIndex(int index) {
        if (!isPositionIndex(index))
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * 判断参数是迭代器还是添加操作的有效位置的索引。
     */
    private boolean isPositionIndex(int index) {
        return index >= 0 && index <= size;
    }
    /**
     * 构造一个IndexOutOfBoundsException详细信息。
     * 在错误处理代码的许多可能重构中，此“概述”在服务器和客户端VM上均表现最佳。
     */
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    /**  modCount
     *已对该列表进行结构修改的次数。结构修改是指更改列表大小或以其他方式干扰列表的方式，
     * 即正在进行的迭代可能会产生错误的结果。
     *该字段由iterator和listIterator方法返回的迭代器和列表迭代器实现使用。
     * 如果此字段的值意外更改，则迭代器（或列表迭代器）将抛出ConcurrentModificationException以响应下一个，
     * 移除，上一个，设置或添加操作。
     * 面对迭代期间的并发修改，这提供了快速故障行为，而不是不确定的行为。
     *子类对此字段的使用是可选的。如果子类希望提供快速失败的迭代器（和列表迭代器），
     * 则只需在其add（int，E）和remove（int）方法
     * （以及任何其他覆盖该方法而导致结构化的方法）中递增此字段。修改列表）。
     * 一次调用add（int，E）或remove（int）不得在此字段中添加不超过一个，
     * 否则迭代器（和列表迭代器）将抛出虚假的ConcurrentModificationExceptions。
     * 如果实现不希望提供快速失败迭代器，则可以忽略此字段。
     */
    protected transient int modCount = 0;

    /**
     * 返回指定元素索引处的（非空）节点。
     */
    Node<E> node(int index) {
        // 断言isElementIndex（index）;
        if (index < (size >> 1)) {
            Node<E> x = first;
            for (int i = 0; i < index; i++)
                x = x.next;
            return x;
        } else {
            Node<E> x = last;
            for (int i = size - 1; i > index; i--)
                x = x.prev;
            return x;
        }
    }

    /**
     * 链接e作为第一个元素。
     */
    private void linkFirst(E e) {
        final Node<E> f = first;
        final Node<E> newNode = new Node<>(null, e, f);
        first = newNode;
        if (f == null)
            last = newNode;
        else
            f.prev = newNode;
        size++;
        modCount++;
    }

    /**
     *将e链接为最后一个元素。
     */
    void linkLast(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        size++;
        modCount++;
    }

    /**
     * 在非null节点succ之前插入元素e。
     */
    void linkBefore(E e, Node<E> succ) {
        // assert succ != null;
        final Node<E> pred = succ.prev;
        final Node<E> newNode = new Node<>(pred, e, succ);
        succ.prev = newNode;
        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;
        size++;
        modCount++;
    }

    /**
     * 取消链接非空的第一个节点f。
     */
    private E unlinkFirst(Node<E> f) {
        // assert f == first && f != null;
        final E element = f.item;
        final Node<E> next = f.next;
        f.item = null;
        f.next = null; // help GC
        first = next;
        if (next == null)
            last = null;
        else
            next.prev = null;
        size--;
        modCount++;
        return element;
    }

    /**
     *取消链接非空的最后一个节点l。
     */
    private E unlinkLast(Node<E> l) {
        // assert l == last && l != null;
        final E element = l.item;
        final Node<E> prev = l.prev;
        l.item = null;
        l.prev = null; // help GC
        last = prev;
        if (prev == null)
            first = null;
        else
            prev.next = null;
        size--;
        modCount++;
        return element;
    }

    /**
     * 取消链接非空节点x。
     */
    E unlink(Node<E> x) {
        // assert x != null;
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        x.item = null;
        size--;
        modCount++;
        return element;
    }


    /**
     * 返回此列表中的第一个元素。
     * @return 此列表中的第一个元素
     * @throws NoSuchElementException 如果此列表为空
     */
    public E getFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return f.item;
    }

    /**
     * 返回此列表中的最后一个元素。
     * @return 此列表中的最后一个元素
     * @throws NoSuchElementException 如果此列表为空
     */
    public E getLast() {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return l.item;
    }

    /**
     * 从此列表中删除并返回第一个元素。
     * @return 此列表中的第一个元素
     * @throws NoSuchElementException 如果此列表为空
     */
    public E removeFirst() {
        final Node<E> f = first;
        if (f == null)
            throw new NoSuchElementException();
        return unlinkFirst(f);
    }

    /**
     * 从此列表中删除并返回最后一个元素。
     * @return 此列表中的最后一个元素
     * @throws NoSuchElementException 如果此列表为空
     */
    public E removeLast() {
        final Node<E> l = last;
        if (l == null)
            throw new NoSuchElementException();
        return unlinkLast(l);
    }
    /**
     * 将指定的元素插入此列表的开头。
     * @param e 要添加的元素
     */
    public void addFirst(E e) {
        linkFirst(e);
    }

    /**
     * 将指定的元素追加到此列表的末尾。
     * 此方法等效于添加。
     * @param e 要添加的元素
     */
    public void addLast(E e) {
        linkLast(e);
    }

    /**
     * 如果此列表包含指定的元素，则返回true。
     * 更正式地说，当且仅当此列表包含至少一个元素
     * （e == null？e == null：o.equals（e））时，返回true。
     * @param o 要检查其在此列表中是否存在的元素
     * @return 如果此列表包含指定的元素，则为true
     */
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    /**
     * 返回此列表中的元素数。
     * @return 此列表中的元素数
     */
    public int size() {
        return size;
    }

// 搜索操作
    /**
     * 返回指定元素在此列表中首次出现的索引；
     * 如果此列表不包含该元素，则返回-1。更正式地，
     * 返回最低索引i，使其（o == null？get（i）== null：o.equals（get（i）））;
     * 如果没有这样的索引，则返回-1。
     * @param o 搜索元素
     * @return 指定元素在此列表中首次出现的索引；如果此列表不包含该元素，则为-1
     */
    public int indexOf(Object o) {
        int index = 0;
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null)
                    return index;
                index++;
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item))
                    return index;
                index++;
            }
        }
        return -1;
    }

    /**
     * 返回指定元素在此列表中最后一次出现的索引；
     * 如果此列表不包含该元素，则返回-1。
     * 更正式地，返回最高索引i使得（o == null？get（i）== null：o.equals（get（i）））;
     * 如果没有这样的索引，则返回-1。
     * @param o 搜索元素
     * @return 指定元素在此列表中最后一次出现的索引；如果此列表不包含该元素，则为-1
     */
    public int lastIndexOf(Object o) {
        int index = size;
        if (o == null) {
            for (Node<E> x = last; x != null; x = x.prev) {
                index--;
                if (x.item == null)
                    return index;
            }
        } else {
            for (Node<E> x = last; x != null; x = x.prev) {
                index--;
                if (o.equals(x.item))
                    return index;
            }
        }
        return -1;
    }

// 队列操作。

    /**
     * 检索但不删除此列表的头（第一个元素）。
     *
     * @return 此列表的标题；如果此列表为空，则为null
     * @since 1.5
     */
    public E peek() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }

    /**
     * 检索但不删除此列表的头（第一个元素）。
     * @return 此列表的头
     * @throws NoSuchElementException 如果此列表为空
     * @since 1.5
     */
    public E element() {
        return getFirst();
    }

    /**
     * 检索并删除此列表的头（第一个元素）。
     * @return 此列表的标题；如果此列表为空，则为null
     * @since 1.5
     */
    public E poll() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    /**
     * 检索并删除此列表的头（第一个元素）。
     * @return 此列表的头
     * @throws NoSuchElementException 如果此列表为空
     * @since 1.5
     */
    public E remove() {
        return removeFirst();
    }

    /**Queue
     * 将指定的元素添加为此列表的尾部（最后一个元素）。
     * @param e the element to add
     * @return {@code true} (as specified by {@link Queue#offer})
     * @since 1.5
     */
    public boolean offer(E e) {
        return add(e);
    }
    /**
     * 将指定的元素追加到此列表的末尾。
     * 此方法等效于addLast。
     * @param e 要添加到此列表的元素
     * @return {@code true} (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        linkLast(e);
        return true;
    }

    /**
     * 如果存在指定元素，则从此列表中删除该元素的第一次出现。
     * 如果此列表不包含该元素，则它保持不变。
     * 更正式地说，删除索引i最低的元素，
     * 使得（o == null？get（i）== null：o.equals（get（i）））（如果存在这样的元素）。
     * 如果此列表包含指定的元素，
     * 则返回true（或者等效地，如果此列表由于调用而更改），则返回true。
     * @param o 要从此列表中删除的元素（如果存在）
     * @return 如果此列表包含指定的元素，则为true
     */
    public boolean remove(Object o) {
        if (o == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }
    // Deque 行动
    /**Deque
     * 将指定的元素插入此列表的前面。
     * @param e 要插入的元素
     * @return 正确（由offerFirst指定）
     * @since 1.6
     */
    public boolean offerFirst(E e) {
        addFirst(e);
        return true;
    }

    /**Deque
     * 将指定的元素插入此列表的末尾。
     * @param e 要插入的元素
     * @return 正确（由offerLast指定）
     * @since 1.6
     */
    public boolean offerLast(E e) {
        addLast(e);
        return true;
    }

    /**
     * 检索但不删除此列表的第一个元素，如果此列表为空，则返回null。
     * @return 此列表的第一个元素；如果此列表为空，则为null
     * @since 1.6
     */
    public E peekFirst() {
        final Node<E> f = first;
        return (f == null) ? null : f.item;
    }

    /**
     * 检索但不删除此列表的最后一个元素，如果此列表为空，则返回null。
     * @return 此列表的最后一个元素；如果此列表为空，则为null
     * @since 1.6
     */
    public E peekLast() {
        final Node<E> l = last;
        return (l == null) ? null : l.item;
    }

    /**
     * 检索并删除此列表的第一个元素，如果此列表为空，则返回null。
     * @return 此列表的第一个元素；如果此列表为空，则为null
     * @since 1.6
     */
    public E pollFirst() {
        final Node<E> f = first;
        return (f == null) ? null : unlinkFirst(f);
    }

    /**
     * 检索并删除此列表的最后一个元素，如果此列表为空，则返回null。
     * @return 此列表的最后一个元素；如果此列表为空，则为null
     * @since 1.6
     */
    public E pollLast() {
        final Node<E> l = last;
        return (l == null) ? null : unlinkLast(l);
    }

    /**
     * 将元素压入此列表表示的堆栈。换句话说，将元素插入此列表的前面。
     * 此方法等效于addFirst。
     * @param e the element to push
     * @since 1.6
     */
    public void push(E e) {
        addFirst(e);
    }

    /**
     * 从此列表表示的堆栈中弹出一个元素。
     * 换句话说，删除并返回此列表的第一个元素。
     * 此方法等效于removeFirst（）。
     * @return 此列表前面的元素（此列表代表的堆栈的顶部）
     * @throws NoSuchElementException 如果此列表为空
     * @since 1.6
     */
    public E pop() {
        return removeFirst();
    }

    /**
     *删除此列表中第一次出现的指定元素（当从头到尾遍历列表时）。
     * 如果列表不包含该元素，则该元素不变。
     * @param o 要从此列表中删除的元素（如果存在）
     * @return {@code true} 如果列表包含指定的元素
     * @since 1.6
     */
    public boolean removeFirstOccurrence(Object o) {
        return remove(o);
    }

    /**
     * 删除此列表中最后一次出现的指定元素（当从头到尾遍历列表时）。
     * 如果列表不包含该元素，则该元素不变。
     * @param o 要从此列表中删除的元素（如果存在）
     * @return 如果列表包含指定的元素，则为true
     * @since 1.6
     */
    public boolean removeLastOccurrence(Object o) {
        if (o == null) {
            for (Node<E> x = last; x != null; x = x.prev) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = last; x != null; x = x.prev) {
                if (o.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        return false;
    }
    /**
     *从列表中的指定位置开始（按适当顺序）返回此列表中元素的列表迭代器。
     * 遵守List.listIterator（int）的常规协定。列表迭代器是快速失败的：
     * 如果在创建迭代器之后的任何时间对列表进行结构修改，
     * 则除了通过列表迭代器自己的remove或add方法进行之外，
     * 列表迭代器都会抛出ConcurrentModificationException。
     * 因此，面对并发修改，迭代器将快速而干净地失败，
     * 而不是冒着在未来不确定的时间冒任意，不确定行为的风险。
     * @param index 列表迭代器要返回的第一个元素的索引（通过调用{@code next}）
     * @return 此列表中元素的ListIterator（按正确顺序），从列表中的指定位置开始
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @see List#listIterator(int)
     */
    public ListIterator<E> listIterator(int index) {
        checkPositionIndex(index);
        return new ListItr(index);
    }

    private class ListItr implements ListIterator<E> {
        private Node<E> lastReturned;
        private Node<E> next;
        private int nextIndex;
        private int expectedModCount = modCount;

        ListItr(int index) {
            // assert isPositionIndex(index);
            next = (index == size) ? null : node(index);
            nextIndex = index;
        }

        public boolean hasNext() {
            return nextIndex < size;
        }

        public E next() {
            checkForComodification();
            if (!hasNext())
                throw new NoSuchElementException();

            lastReturned = next;
            next = next.next;
            nextIndex++;
            return lastReturned.item;
        }

        public boolean hasPrevious() {
            return nextIndex > 0;
        }

        public E previous() {
            checkForComodification();
            if (!hasPrevious())
                throw new NoSuchElementException();

            lastReturned = next = (next == null) ? last : next.prev;
            nextIndex--;
            return lastReturned.item;
        }

        public int nextIndex() {
            return nextIndex;
        }

        public int previousIndex() {
            return nextIndex - 1;
        }

        public void remove() {
            checkForComodification();
            if (lastReturned == null)
                throw new IllegalStateException();

            Node<E> lastNext = lastReturned.next;
            unlink(lastReturned);
            if (next == lastReturned)
                next = lastNext;
            else
                nextIndex--;
            lastReturned = null;
            expectedModCount++;
        }

        public void set(E e) {
            if (lastReturned == null)
                throw new IllegalStateException();
            checkForComodification();
            lastReturned.item = e;
        }

        public void add(E e) {
            checkForComodification();
            lastReturned = null;
            if (next == null)
                linkLast(e);
            else
                linkBefore(e, next);
            nextIndex++;
            expectedModCount++;
        }

        public void forEachRemaining(Consumer<? super E> action) {
            Objects.requireNonNull(action);
            while (modCount == expectedModCount && nextIndex < size) {
                action.accept(next.item);
                lastReturned = next;
                next = next.next;
                nextIndex++;
            }
            checkForComodification();
        }

        final void checkForComodification() {
            if (modCount != expectedModCount)
                throw new ConcurrentModificationException();
        }
    }

    /**
     * @since 1.6
     */
    public Iterator<E> descendingIterator() {
        return new DescendingIterator();
    }

    /**
     * 通过ListItr.previous提供降序迭代器的适配器
     */
    private class DescendingIterator implements Iterator<E> {
        private final ListItr itr = new ListItr(size());
        public boolean hasNext() {
            return itr.hasPrevious();
        }
        public E next() {
            return itr.previous();
        }
        public void remove() {
            itr.remove();
        }
    }

    @SuppressWarnings("unchecked")
    private MyLinkedList<E> superClone() {
        try {
            return (MyLinkedList<E>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    /**
     * 返回此{@code LinkedList}的浅表副本。 （元素本身不会被克隆。）
     * @return a shallow copy of this {@code LinkedList} instance
     */
    public Object clone() {
        MyLinkedList<E> clone = superClone();

        // 将克隆置于“原始”状态
        clone.first = clone.last = null;
        clone.size = 0;
        clone.modCount = 0;
        // 用我们的元素初始化克隆
        for (Node<E> x = first; x != null; x = x.next)
            clone.add(x.item);

        return clone;
    }

    /**
     * 以正确的顺序（从第一个元素到最后一个元素）返回一个包含此列表中所有元素的数组。
     * 返回的数组将是“安全的”，因为此列表不保留对其的引用。
     * （换句话说，此方法必须分配一个新数组）。
     * 因此，调用者可以自由修改返回的数组。
     * 此方法充当基于数组的API和基于集合的API之间的桥梁。
     * @return 包含此列表中所有元素的序列按适当顺序的数组
     */
    public Object[] toArray() {
        Object[] result = new Object[size];
        int i = 0;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;
        return result;
    }
    /**
     * 返回一个数组，该数组按适当顺序（从第一个元素到最后一个元素）包含此列表中的所有元素；
     * 返回数组的运行时类型是指定数组的运行时类型。
     * 如果列表适合指定的数组，则将其返回。
     * 否则，将使用指定数组的运行时类型和此列表的大小分配一个新数组。
     * 如果列表适合指定的数组并有剩余空间（即，数组中的元素多于列表），
     * 则紧接列表末尾的数组中的元素将设置为{@code null}。
     * （仅当调用者知道列表不包含任何null元素时，这才对确定列表的长度很有用。）
     * 与{@link #toArray（）}方法一样，此方法充当基于数组的集合与集合之间的桥梁。
     * 基于的API。此外，此方法允许对输出数组的运行时类型进行精确控制，
     * 并且在某些情况下可以用来节省分配成本。假设{@code x}是已知仅包含字符串的列表。
     * 以下代码可用于将列表转储到新分配的{@code String}数组中：String [] y = x.toArray（new String [0]）;
     * 请注意，{@code toArray（new Object [0]）}在功能上与{@code toArray（）}相同。
     * @param a 列表元素要存储到的数组（如果足够大）；否则，将为此分配一个具有相同运行时类型的新数组。
     * @return 包含列表元素的数组
     * @throws ArrayStoreException 如果指定数组的运行时类型不是此列表中每个元素的运行时类型的超类型
     * @throws NullPointerException 如果指定的数组为null
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            a = (T[])java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size);
        int i = 0;
        Object[] result = a;
        for (Node<E> x = first; x != null; x = x.next)
            result[i++] = x.item;

        if (a.length > size)
            a[size] = null;

        return a;
    }

    private static final long serialVersionUID = 876323262645176354L;

    /**
     * 将此{@code LinkedList}实例的状态保存到流中（即，对其进行序列化）。
     *
     * @serialData 发出（int）列表的大小（包含的元素数），然后以适当的顺序发出其所有元素（每个Object）。
     */
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException {
        // Write out any hidden serialization magic
        s.defaultWriteObject();

        // Write out size
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (Node<E> x = first; x != null; x = x.next)
            s.writeObject(x.item);
    }

    /**
     * 从流中重构此{@code LinkedList}实例
     * (that is, deserializes it).
     */
    @SuppressWarnings("unchecked")
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        // Read in any hidden serialization magic
        s.defaultReadObject();

        // Read in size
        int size = s.readInt();

        // Read in all elements in the proper order.
        for (int i = 0; i < size; i++)
            linkLast((E)s.readObject());
    }


    public static void main(String[] args) {
        List<Integer> list = new LinkedList<>();
    }
}
