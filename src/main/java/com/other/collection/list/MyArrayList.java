package com.other.collection.list;

import java.util.*;
import java.util.function.Consumer;

public class MyArrayList<E> {
    /**
     * 默认初始容量为10
     */
    private static final int DEFAULT_CAPACITY = 10;

    /**
     * ArrayList的大小（它包含的元素数）。
     */
    private int size;

    /**
     * ArrayList元素存储到的数组缓冲区。
     * ArrayList的容量是此数组缓冲区的长度。
     * 添加第一个元素时，任何具有elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * 的空ArrayList都将扩展为DEFAULT_CAPACITY。
     */
    //将不需要序列化的属性前添加关键字transient，序列化对象的时候，这个属性就不会被序列化
    transient Object[] elementData; // 非私有以简化嵌套类访问

    /**
     * 用于空实例的共享空数组实例。
     */
    private static final Object[] EMPTY_ELEMENTDATA = {};

    /**
     * 共享的空数组实例，用于默认大小的空实例。
     * 我们将此与EMPTY_ELEMENTDATA区别开来，以了解添加第一个元素时需要充气多少。
     *
     */
    private static final Object[] DEFAULTCAPACITY_EMPTY_ELEMENTDATA = {};

    /**
     * 构造一个具有指定初始容量的空列表。
     *
     * @param  initialCapacity  列表的初始容量
     * @throws IllegalArgumentException 如果指定的初始容量为负
     *
     */
    public MyArrayList(int initialCapacity) {
        if (initialCapacity > 0) {
            this.elementData = new Object[initialCapacity];
        } else if (initialCapacity == 0) {
            this.elementData = EMPTY_ELEMENTDATA;
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                                                       initialCapacity);
        }
    }

    /**
     * 构造一个初始容量为10的空列表。
     */
    public MyArrayList() {
        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
    }

    /**
     * 构造一个包含指定集合的元素的列表，其顺序由集合的迭代器返回。
     * @param c 将其元素放入此列表的集合
     * @throws NullPointerException 如果指定的集合为null
     */
    public MyArrayList(Collection<? extends E> c) {
        elementData = c.toArray();
        if ((size = elementData.length) != 0) {
            // c.toArray可能（不正确）不返回Object [] (see 6260652)
            if (elementData.getClass() != Object[].class)
                elementData = Arrays.copyOf(elementData, size, Object[].class);
        } else {
            // 替换为空数组。
            this.elementData = EMPTY_ELEMENTDATA;
        }
    }

    /**
     * modCount来自继承AbstractList<E>
     */
    protected transient int modCount = 0;

    /**
     * 将此ArrayList实例的容量调整为列表的当前大小。
     *  应用程序可以使用此操作来最小化ArrayList实例的存储。
     */
    public void trimToSize() {
        modCount++;
        if (size < elementData.length) {
            elementData = (size == 0)? EMPTY_ELEMENTDATA:Arrays.copyOf(elementData, size);
        }
    }

    /**
     * 如有必要，增加此ArrayList实例的容量，
     * 以确保它至少可以容纳最小容量参数指定的元素数量。
     * @param   minCapacity   所需的最小容量
     */
    public void ensureCapacity(int minCapacity) {
        int minExpand = (elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA)
                                // 任何大小（如果不是默认元素表）
                                ? 0
                                // 大于默认的默认空表。它应该已经是默认大小。
                                : DEFAULT_CAPACITY;
        if (minCapacity > minExpand) {
            ensureExplicitCapacity(minCapacity);
        }
    }
    private void ensureCapacityInternal(int minCapacity) {
        if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
            minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
        }
        ensureExplicitCapacity(minCapacity);
    }
    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;
        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }

    /**
     * 要分配的最大数组大小.
     * 一些虚拟机在数组中保留一些标题字.
     * 尝试分配更大的阵列可能会导致OutOfMemoryError：请求的阵列大小超出VM限制
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * 增加容量以确保其至少可以容纳最小容量参数指定的元素数量。
     * @param minCapacity 所需的最小容量
     */
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        //右移一位，oldCapacity >> 1 为oldCapacity大小的一半
        //即扩大原来oldCapacity大小的一半
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity通常接近大小，因此这是一个胜利：
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    /**
     *返回此列表中的元素数。
     * @return 此列表中的元素数
     */
    public int size() {
        return size;
    }

    /**
     * 如果此列表不包含任何元素，则返回true。
     *
     * @return 如果此列表不包含任何元素，则为true
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 如果此列表包含指定的元素，则返回true。
     * 更正式地说，当且仅当此列表包含至少一个元素（e == null？e == null：o.equals（e））时，返回true。
     * @param o element whose presence in this list is to be tested
     * @return <tt>true</tt> if this list contains the specified element
     */
    public boolean contains(Object o) {
        return indexOf(o) >= 0;
    }

    /**
     * 返回指定元素在此列表中首次出现的索引；如果此列表不包含该元素，则返回-1。
     * 更正式地，返回最低索引i使得（o == null？get（i）== null：o.equals（get（i）））;
     * 如果没有这样的索引，则返回-1。
     */
    public int indexOf(Object o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }

    /**
     * 返回指定元素在此列表中最后一次出现的索引；如果此列表不包含该元素，则返回-1.
     * 更正式地，返回最高索引i使得（o == null？get（i）== null：o.equals（get（i）））;
     * 如果没有这样的索引，则返回-1。
     *
     *
     */
    public int lastIndexOf(Object o) {
        if (o == null) {
            for (int i = size-1; i >= 0; i--)
                if (elementData[i]==null)
                    return i;
        } else {
            for (int i = size-1; i >= 0; i--)
                if (o.equals(elementData[i]))
                    return i;
        }
        return -1;
    }
    /**clone调用Arrays.copyOf()进行复制
     * 返回此ArrayList实例的浅表副本。 （元素本身不会被复制。）
     * @return 此ArrayList实例的副本
     */
    public Object clone() {
        try {
            MyArrayList<E> v = (MyArrayList<E>) super.clone();
            v.elementData = Arrays.copyOf(elementData, size);
            v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // 这不应该发生，因为我们是可克隆的
            throw new InternalError(e);
        }
    }
    /**
     * 以正确的顺序（从第一个元素到最后一个元素）返回包含此列表中所有元素的数组。
     * 返回的数组将是“安全的”，因为此列表不保留对其的引用。（换句话说，此方法必须分配一个新数组）。
     * 因此，调用者可以自由修改返回的数组。
     * 此方法充当基于数组的API和基于集合的API之间的桥梁。
     * @return 包含此列表中所有元素的序列按适当顺序的数组
     */
    public Object[] toArray() {
        return Arrays.copyOf(elementData, size);
    }

    /**
     * 返回一个数组，该数组按正确的顺序包含此列表中的所有元素（从第一个元素到最后一个元素）；
     * 返回数组的运行时类型是指定数组的运行时类型。如果列表适合指定的数组，则将其返回。
     * 否则，将使用指定数组的运行时类型和此列表的大小分配一个新数组。
     * 如果列表适合指定的数组并有剩余空间（即，数组中的元素多于列表），则紧接集合结束后的数组中的元素设置为null。
     * (仅当调用者知道列表不包含任何null元素时，这才对确定列表的长度很有用。)
     * @param a 列表元素要存储到的数组（如果足够大）；否则，将为此分配一个具有相同运行时类型的新数组。
     * @return 包含列表元素的数组
     * @throws ArrayStoreException 如果指定数组的运行时类型不是此列表中每个元素的运行时类型的超类型
     * @throws NullPointerException 如果指定的数组为null
     */
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }
    // 位置访问操作
    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) elementData[index];
    }

    /**
     * 返回此列表中指定位置的元素。
     * @param  index 要返回的元素的索引
     * @return 此列表中指定位置的元素
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E get(int index) {
        rangeCheck(index);

        return elementData(index);
    }

    /**
     * 检查给定的索引是否在范围内。如果不是，则抛出适当的运行时异常。
     * 此方法不检查索引是否为负：始终在数组访问之前立即使用它，
     * 如果索引为负，则抛出ArrayIndexOutOfBoundsException。
     */
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * 构造一个IndexOutOfBoundsException详细消息。
     * 在错误处理代码的许多可能重构中，此“概述”在服务器和客户端VM上均表现最佳。
     */
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }
    /**
     * 用指定的元素替换此列表中指定位置的元素。
     * @param index 替换元素的索引
     * @param element 要存储在指定位置的元素
     * @return 先前位于指定位置的元素
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E set(int index, E element) {
        rangeCheck(index);//确定未越界
        E oldValue = elementData(index);//得到原来index位置得元素
        elementData[index] = element;//替换元素
        return oldValue;
    }
    /**
     * 将指定的元素追加到此列表的末尾。
     * @param e 要添加到此列表的元素
     * @return true（由{@link Collection＃add}指定）
     */
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        elementData[size++] = e;
        return true;
    }

    /**因为插入指定位置需要后移当前位置后面的所有元素，所以不适合插入操作
     * 将指定的元素插入此列表中的指定位置。
     * 将当前在该位置的元素（如果有）和任何后续元素右移（将其索引添加一个）。
     * @param index 指定元素要插入的索引
     * @param element 要插入的元素
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void add(int index, E element) {
        rangeCheckForAdd(index);//判断是否越界
        ensureCapacityInternal(size + 1);  // 增加modCount!!
        System.arraycopy(elementData, index, elementData, index + 1,
                size - index);
        elementData[index] = element;
        size++;
    }

    /**判断是否越界
     * add和addAll使用的rangeCheck版本。
     */
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**ArrayList不适合删除
     * 删除此列表中指定位置的元素。
     * 将所有后续元素向左移动（从其索引中减去一个）。
     * @param index 要删除的元素的索引
     * @return 从列表中删除的元素
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public E remove(int index) {
        rangeCheck(index);
        modCount++;
        E oldValue = elementData(index);
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                    numMoved);
        elementData[--size] = null; // 明确让GC开展工作
        return oldValue;
    }

    /**
     * 如果存在指定元素，则从该列表中删除该元素的第一次出现。
     * 如果列表不包含该元素，则该元素不变。
     * 更正式地讲，删除索引i最低的元素，使（o == null？get（i）== null：o.equals（get（i）））（如果存在这样的元素）。
     * 如果此列表包含指定的元素（或者等效地，如果此列表由于调用而更改），则返回true。
     * @param o 要从此列表中删除的元素（如果存在）
     * @return 如果此列表包含指定的元素，则为true
     */
    public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }
    /*
     * 专用的remove方法，跳过边界检查并且不返回已删除的值。
     */
    private void fastRemove(int index) {
        modCount++;
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                    numMoved);
        elementData[--size] = null; // 明确让GC开展工作
    }

    /**
     *从此列表中删除所有元素。此调用返回后，该列表将为空。
     */
    public void clear() {
        modCount++;

        // clear to let GC do its work
        for (int i = 0; i < size; i++)
            elementData[i] = null;

        size = 0;
    }
    /**
     * 按照指定集合的​​Iterator返回的顺序，将指定集合中的所有元素追加到此列表的末尾。
     * 如果在操作进行过程中修改了指定的集合，则此操作的行为是不确定的。
     * （这意味着如果指定的集合是此列表，并且此列表是非空的，则此调用的行为是不确定的。）
     * @param c 包含要添加到此列表的元素的集合
     * @return 如果此列表因通话而更改，则为true
     * @throws NullPointerException 如果指定的集合为null
     */
    public boolean addAll(Collection<? extends E> c) {
        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityInternal(size + numNew);  // 增加modCount
        System.arraycopy(a, 0, elementData, size, numNew);
        size += numNew;
        return numNew != 0;
    }
    /**
     * 从指定位置开始，将指定集合中的所有元素插入此列表。
     * 将当前位于该位置的元素（如果有）和任何后续元素右移（增加其索引）。
     * 新元素将按照指定集合的​​迭代器返回的顺序显示在列表中。
     * @param index 从指定的集合中插入第一个元素的索引
     * @param c 包含要添加到此列表的元素的集合
     * @return 如果此列表因通话而更改，则为true
     * @throws IndexOutOfBoundsException {@inheritDoc}
     * @throws NullPointerException 如果指定的集合为null
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        rangeCheckForAdd(index);

        Object[] a = c.toArray();
        int numNew = a.length;
        ensureCapacityInternal(size + numNew);  // Increments modCount

        int numMoved = size - index;
        if (numMoved > 0)
            System.arraycopy(elementData, index, elementData, index + numNew,
                    numMoved);

        System.arraycopy(a, 0, elementData, index, numNew);
        size += numNew;
        return numNew != 0;
    }
    /**
     * 从此列表中删除索引在{@code fromIndex}（含）和{@code toIndex}（互斥）之间的所有元素。
     * 将所有后续元素向左移动（减少其索引）。此调用通过{@code（toIndex-fromIndex）}元素来缩短列表。
     * （如果{@code toIndex == fromIndex}，则此操作无效。）
     * @throws IndexOutOfBoundsException 如果{@code fromIndex}或{@code toIndex}超出范围
     * （{@code fromIndex <0 ||
     * fromIndex> = size（）||
     * toIndex> size（）||
     * toIndex <fromIndex}）
     */
    protected void removeRange(int fromIndex, int toIndex) {
        modCount++;
        int numMoved = size - toIndex;
        System.arraycopy(elementData, toIndex, elementData, fromIndex,
                numMoved);

        // clear to let GC do its work
        int newSize = size - (toIndex-fromIndex);
        for (int i = newSize; i < size; i++) {
            elementData[i] = null;
        }
        size = newSize;
    }

    /**
     * 从此列表中删除指定集合中包含的所有其元素。
     * @param c 包含要从此列表中删除的元素的集合
     * @return  如果此列表因通话而更改，则为true
     * @throws ClassCastException 如果此列表的元素的类与指定的集合不兼容
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException 如果此列表包含null元素，并且指定的collection不允许null元素，
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     * 或者如果指定的collection为null
     * @see Collection#contains(Object)
     */
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return batchRemove(c, false);
    }
    private boolean batchRemove(Collection<?> c, boolean complement) {
        final Object[] elementData = this.elementData;
        int r = 0, w = 0;
        boolean modified = false;
        try {
            for (; r < size; r++)
                if (c.contains(elementData[r]) == complement)
                    elementData[w++] = elementData[r];
        } finally {
            // 即使c.contains（）抛出异常，仍保留与AbstractCollection的行为兼容性。
            if (r != size) {
                System.arraycopy(elementData, r,
                        elementData, w,
                        size - r);
                w += size - r;
            }
            if (w != size) {
                // 明确让GC开展工作
                for (int i = w; i < size; i++)
                    elementData[i] = null;
                modCount += size - w;
                size = w;
                modified = true;
            }
        }
        return modified;
    }

    /**
     * 仅保留此列表中包含在指定集合中的元素。换句话说，从该列表中删除所有未包含在指定集合中的元素。
     * @param c 包含要保留在此列表中的元素的集合
     * @return 如果此列表因通话而更改，则为true
     * @throws ClassCastException 如果此列表的元素的类与指定的集合不兼容
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException 如果此列表包含null元素并且指定的集合不允许null元素l，
     * (<a href="Collection.html#optional-restrictions">optional</a>),
     *  或者指定的collection为nul
     * @see Collection#contains(Object)
     */
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return batchRemove(c, true);
    }
    /**
     * 将ArrayList实例的状态保存到流（即，对其进行序列化）。
     * @serialData 发出支持ArrayList实例的数组的长度（int），
     * 然后以适当的顺序跟随其所有元素（每个Object）。
     */
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException{
        // 写出元素计数以及任何隐藏的内容
        int expectedModCount = modCount;
        s.defaultWriteObject();
        // 写出大小作为与clone（）行为兼容的容量
        s.writeInt(size);
        // 按照正确的顺序写出所有元素。
        for (int i=0; i<size; i++) {
            s.writeObject(elementData[i]);
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * 从流中重构ArrayList实例（即，反序列化它）。
     */
    private void readObject(java.io.ObjectInputStream s)
            throws java.io.IOException, ClassNotFoundException {
        elementData = EMPTY_ELEMENTDATA;
        // 阅读大小以及任何隐藏的内容
        s.defaultReadObject();
        // 读入容量
        s.readInt(); // ignored
        if (size > 0) {
            // 像clone（）一样，根据大小而不是容量分配数组
            ensureCapacityInternal(size);
            Object[] a = elementData;
            // Read in all elements in the proper order.
            for (int i=0; i<size; i++) {
                a[i] = s.readObject();
            }
        }
    }

    /**
     * 对Iterable的每个元素执行给定的操作，直到所有元素都被处理或动作引发异常。
     * 除非实现类另有规定，否则按照迭代的顺序执行操作（如果指定了迭代顺序）。
     * 动作抛出的异常被转发给呼叫者。
     * @param action
     */
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        final int expectedModCount = modCount;
        @SuppressWarnings("unchecked")
        final E[] elementData = (E[]) this.elementData;
        final int size = this.size;
        for (int i=0; modCount == expectedModCount && i < size; i++) {
            action.accept(elementData[i]);
        }
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
    }

    /**
     * 使用提供的 Comparator对此列表进行排序，以比较元素。
     * @param c
     */
    @SuppressWarnings("unchecked")
    public void sort(Comparator<? super E> c) {
        final int expectedModCount = modCount;
        Arrays.sort((E[]) elementData, 0, size, c);
        if (modCount != expectedModCount) {
            throw new ConcurrentModificationException();
        }
        modCount++;
    }

    /**ArrayList适合随机查找和遍历，不适合插入和删除。
     * System.arraycopy(),Arrays.copyOf()
     * MyArrayList等同于ArrayList
     * @param args
     */
    public static void main(String[] args) {
//        MyArrayList<Integer> list = new MyArrayList<>();
//        MyArrayList<Integer> list2 = new MyArrayList<>();
//        int a =100;
//        int b =a>>1;
//        System.out.println(b);
        int arr []={1,2,3,4};
        int insertarr[] = {5,6,7,8,9};
        //将指定源数组中的数组从指定位置复制到目标数组的指定位置。
        System.arraycopy(arr,0,insertarr,1,2);
        System.out.println(Arrays.toString(insertarr));//[5, 1, 2, 8, 9]
    }
}
