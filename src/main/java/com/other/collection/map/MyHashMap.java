package com.other.collection.map;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
/**
 *  基于哈希表的Map接口的实现。此实现提供所有可选的映射操作，并允许空值和空键。
 *  （HashMap类与Hashtable大致等效，不同之处在于它是不同步的，并且允许为null。）
 *  此类不保证映射的顺序。特别是，它不能保证顺序会随着时间的推移保持恒定。
 *
 *   该实现为基本操作（get和put）提供了恒定时间的性能，假设哈希函数将元素正确分散在存储桶中。
 *   集合视图上的迭代所需的时间与HashMap实例的“容量”（存储桶数）及其大小（键-值映射数）成正比。
 *   因此，如果迭代性能很重要，则不要将初始容量设置得过高（或负载因子过低），这一点非常重要。
 *
 *   HashMap的实例具有两个影响其性能的参数：初始容量和负载因子。
 *   容量是哈希表中存储桶的数量，初始容量只是创建哈希表时的容量。
 *   负载因子是散列表的容量自动增加之前允许其填充的完整程度的度量。
 *   当哈希表中的条目数超过负载因子和当前容量的乘积时，
 *   哈希表将被重新哈希（即，内部数据结构将被重建），因此哈希表的存储桶数约为两倍。
 *
 * 通常，默认负载因子（.75）在时间和空间成本之间提供了很好的折衷。
 * 较高的值会减少空间开销，但会增加查找成本（在HashMap类的大多数操作中都得到体现，包括get和put）。
 * 设置其初始容量时，应考虑映射中的预期条目数及其负载因子，以最大程度地减少重新哈希操作的次数。
 * 如果初始容量大于最大条目数除以负载因子，则将不会进行任何哈希操作。
 *
 *  如果将许多映射存储在HashMap实例中，
 *  则创建具有足够大容量的映射将比让其根据需要增长表的自动重新哈希处理更有效地存储映射。
 *  请注意，使用许多具有相同{@code hashCode（）}的键是降低任何哈希表性能的肯定方法。
 *  为了改善影响，当键为{@link Comparable}时，此类可以使用键之间的比较顺序来帮助打破平局。
 *
 *   请注意，此实现未同步。如果多个线程同时访问哈希映射，
 *   并且至少有一个线程在结构上修改该映射，则必须在外部进行同步。
 *   （结构修改是添加或删除一个或多个映射的任何操作；
 *   仅更改与实例已经包含的键相关联的值不是结构修改。）
 *   这通常是通过对自然封装了地图的某个对象进行同步来实现的。
 *
 *  如果不存在这样的对象，则应使用{@link Collections＃synchronizedMap Collections.synchronizedMap}方法“包装”地图。
 *  最好在创建时完成此操作，以防止意外的非同步访问地图：Map m = Collections.synchronizedMap（new HashMap（...））;
 *
 *  此类的所有“集合视图方法”返回的迭代器都是快速失败的：
 *  如果在创建迭代器之后的任何时间以任何方式对地图进行结构修改，
 *  除非通过迭代器自己的remove方法，否则迭代器将抛出{ @link ConcurrentModificationException}。
 *  因此，面对并发修改，迭代器将快速而干净地失败，而不是冒着在未来不确定的时间冒任意，不确定行为的风险。
 *
 *   注意，不能保证迭代器的快速失败行为，因为通常来说，在存在不同步的并发修改的情况下，
 *   不可能做出任何严格的保证。快速失败的迭代器会尽最大努力抛出ConcurrentModificationException。
 *   因此，编写依赖于此异常的程序的正确性是错误的：迭代器的快速失败行为应仅用于检测错误。
 *
 *  此类是Java Collections Framework的成员。
  
 * @param <K> 此地图维护的键的类型
 * @param <V> 映射值的类型
 *
 */
public class MyHashMap <K,V> extends AbstractMap<K,V>
        implements Map<K,V>, Cloneable, Serializable {

    /*
     * 实施说明。
     *
     * 该映射通常用作装箱（存储桶）的哈希表，但是当bin太大时，
     * 它们将转换为TreeNodes的bin，每个bin的结构与java.util.TreeMap中的相似。
     * 大多数方法尝试使用普通的bin，但是在适用时中继到TreeNode方法（只需通过检查节点的instance）。
     *  TreeNodes的Bin可以像其他任何遍历一样使用，但在人口过多时还支持更快的查找。
     * 但是，由于正常使用中的绝大多数垃圾箱没有人口过多，
     * 因此在使用表方法的过程中可能会延迟检查是否存在树木垃圾箱。
     *
     * 树箱（即，其元素均为TreeNode的箱）主要由hashCode排序，但在有联系的情况下，
     * 如果两个元素属于同一“类C实现Comparable <C>”，请键入，然后使用他们的compareTo方法订购。
     * （我们通过反射保守地检查泛型类型以验证这一点-参见方法compareclassClass）。
     * 当键具有不同的哈希值或可排序时，增加树状容器的复杂性值得提供最坏情况的O（log n）操作，
     * 因此，在意外或恶意使用情况下（哈希代码（）方法返回的值很差），性能会优雅地下降。
     * 分布式，以及其中许多键共享一个hashCode的键，只要它们也是可比较的。
     * （如果这两种方法都不适用，那么与不采取预防措施相比，我们可能在时间和空间上浪费大约两倍。
     * 但是，唯一已知的情况是由于不良的用户编程实践已经如此之慢，以至于几乎没有什么区别。）
     *
     * 因为TreeNode的大小大约是常规节点的两倍，
     * 所以我们仅在垃圾箱包含足以保证使用的节点时才使用它们（请参阅TREEIFY_THRESHOLD）。
     * 当它们变得太小（由于移除或调整大小）时，它们会转换回普通纸槽。
     * 在具有良好分布的用户hashCode的用法中，很少使用树箱。
     * 理想情况下，在随机hashCodes下，bin中节点的频率遵循Poisson分布（http://en.wikipedia.org/wiki/Poisson_distribution），
     * 其默认调整大小阈值为0.75，平均参数约为0.5，尽管由于调整粒度的差异很大。
     * 忽略方差，列表大小k的预期出现次数为（exp（-0.5）* pow（0.5，k）/ factorial（k））。第一个值是：
     *
     * 0:    0.60653066
     * 1:    0.30326533
     * 2:    0.07581633
     * 3:    0.01263606
     * 4:    0.00157952
     * 5:    0.00015795
     * 6:    0.00001316
     * 7:    0.00000094
     * 8:    0.00000006
     * 更多：少于一千万分之一
     *
     * 树枝的根通常是其第一个节点。
     * 但是，有时（当前仅在Iterator.remove上）根可能在其他位置，
     * 但可以在父链接之后恢复（方法TreeNode.root（））。
     *
     * 所有适用的内部方法均接受哈希码作为参数（通常由公共方法提供），
     * 从而允许它们在不重新计算用户hashCode的情况下彼此调用。
     * 大多数内部方法还接受“ tab”参数，
     * 该参数通常是当前表，但在调整大小或转换时可以是新的或旧的。
     *
     * 当bin列表被树化，拆分或未树化时，
     * 我们将它们保持在相同的相对访问/遍历顺序（即字段Node.next）中，
     * 以更好地保留局部性，并略微简化对调用iterator.remove的拆分和遍历的处理。
     * 当在插入时使用比较器时，为了在重新平衡之间保持总体排序（或此处要求的接近度），
     * 我们将类和identityHashCodes作为决胜局进行比较。
     *
     * 子类LinkedHashMap的存在使普通模式与树模式之间的使用和转换变得复杂。
     * 请参见下面的钩子方法，这些钩子方法定义为在插入，删除和访问时被调用，
     * 这些方法允许LinkedHashMap内部保持独立于这些机制。
     * （这还要求将地图实例传递给一些可能创建新节点的实用程序方法。）
     *
     * 类似于并发编程的基于SSA的编码样式有助于避免所有扭曲指针操作中的混叠错误。
     */
    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

    /**
     * 最大容量，如果两个构造函数都使用参数隐式指定了更高的值，
     * 则使用该容量。必须是两个<= 1 << 30的幂。
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * 在构造函数中未指定时使用的负载系数。
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 使用树而不是列出容器的容器计数阈值。
     * 将元素添加到至少具有这么多节点的bin中时，bin会转换为树。
     * 该值必须大于2，并且至少应为8才能与树删除中的假设（即收缩时转换回原始垃圾箱）相啮合。
     */
    static final int TREEIFY_THRESHOLD = 8;

    /**
     * 在调整大小操作期间用于取消树状化（拆分的）箱的箱计数阈值。
     * 应小于TREEIFY_THRESHOLD，并且最多6以与移除下的收缩检测相啮合。
     */
    static final int UNTREEIFY_THRESHOLD = 6;

    /**
     * 可将其分类为树的最小桌子容量。
     * （否则，如果bin中的节点过多，则将调整表的大小。）
     * 应至少为4 * TREEIFY_THRESHOLD，以避免调整大小和树化阈值之间的冲突。
     */
    static final int MIN_TREEIFY_CAPACITY = 64;

    /**
     * 基本哈希箱节点，用于大多数条目。
     * （有关TreeNode子类的信息，请参见下文；有关其Entry子类的信息，请参见LinkedHashMap。）
     */
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;

        Node(int hash, K key, V value, Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey()        { return key; }
        public final V getValue()      { return value; }
        public final String toString() { return key + "=" + value; }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o) {
            if (o == this)
                return true;
            if (o instanceof Map.Entry) {
                Map.Entry<?,?> e = (Map.Entry<?,?>)o;
                if (Objects.equals(key, e.getKey()) &&
                            Objects.equals(value, e.getValue()))
                    return true;
            }
            return false;
        }
    }

    /* ---------------- 静态实用程序 -------------- */

    /**
     * 计算key.hashCode（）并将哈希的较高位扩展（XOR）到较低位。
     * 由于该表使用2的幂次掩码，因此仅在当前掩码上方的位中变化的哈希集将始终发生冲突。
     * （众所周知的示例是在小表中保存连续整数的Float键集。）
     * 因此，我们应用了一种变换，将向下传播较高位的影响。
     * 在速度，实用性和位扩展质量之间需要权衡。
     * 由于许多常见的哈希集已经合理分布（因此无法从扩展中受益），
     * 并且由于我们使用树来处理容器中的大量冲突集，
     * 因此我们仅以最便宜的方式对一些移位后的位进行XOR，
     * 以减少系统损失，以及合并最高位的影响，否则由于表范围的限制，
     * 这些位将永远不会在索引计算中使用。
     */
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * 如果x的形式为“类C实现Comparable <C>”，则返回x的类，否则返回null。
     */
    static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Class<?> c; Type[] ts, as; Type t; ParameterizedType p;
            if ((c = x.getClass()) == String.class) // bypass checks
                return c;
            if ((ts = c.getGenericInterfaces()) != null) {
                for (int i = 0; i < ts.length; ++i) {
                    if (((t = ts[i]) instanceof ParameterizedType) &&
                                ((p = (ParameterizedType)t).getRawType() ==
                                         Comparable.class) &&
                                (as = p.getActualTypeArguments()) != null &&
                                as.length == 1 && as[0] == c) // type arg is c
                        return c;
                }
            }
        }
        return null;
    }

    /**
     * 如果x匹配kc（k的筛选的可比类），则返回k.compareTo（x），否则返回0。
     */
    @SuppressWarnings({"rawtypes","unchecked"}) // for cast to Comparable
    static int compareComparables(Class<?> kc, Object k, Object x) {
        return (x == null || x.getClass() != kc ? 0 :
                        ((Comparable)k).compareTo(x));
    }

    /**
     * 对于给定的目标容量，返回两倍大小的幂。
     */
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
    /* ---------------- Fields -------------- */

    /**
     * 该表在首次使用时初始化，并根据需要调整大小。
     * 分配时，长度始终是2的幂。
     * （在某些操作中，我们还允许长度为零，以允许使用当前不需要的引导机制。）
     */
    transient Node<K,V>[] table;

    /**
     * 保存缓存的entrySet（）。
     * 注意，AbstractMap字段用于keySet（）和values（）。
     */
    transient Set<Map.Entry<K,V>> entrySet;

    /**
     * 此映射中包含的键-值映射数。
     */
    transient int size;

    /**
     * 对该HashMap进行结构修改的次数结构修改是指更改HashMap
     * 中映射次数或以其他方式修改其内部结构（例如，重新哈希）的修改。
     * 此字段用于使HashMap的Collection-view上的迭代器快速失败。
     * （请参见ConcurrentModificationException）。
     */
    transient int modCount;

    /**
     * 要调整大小的下一个大小值（容量*负载系数）。
     *
     * @serial
     */
    // （javadoc描述在序列化时为true。此外，如果尚未分配表数组，
    // 则此字段将保留初始数组容量，或者为零，表示DEFAULT_INITIAL_CAPACITY。）
    int threshold;

    /**
     * 哈希表的负载因子。
     *
     * @serial
     */
    final float loadFactor;

    /* ---------------- Public operations -------------- */

    /**
     * 构造一个具有指定初始容量和负载因子的空HashMap。
     *
     * @param  initialCapacity 初始容量
     * @param  loadFactor      负载系数
     * @throws IllegalArgumentException 如果初始容量为负或负载系数为正
     */
    public MyHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                                       initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                                       loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    /**
     * 构造一个具有指定初始容量和默认负载因子（0.75）的空HashMap。
     *
     * @param  initialCapacity 初始容量。
     * @throws IllegalArgumentException 如果初始容量为负。
     */
    public MyHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    /**
     * 使用默认的初始容量（16）和默认的加载因子（0.75）构造一个空的HashMap。
     */
    public MyHashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // 所有其他字段默认
    }

    /**
     * 构造一个具有与指定Map相同的映射关系的新HashMap。
     * 使用默认负载因子（0.75）和足以将映射保存在指定Map中的初始容量创建HashMap。
     *
     * @param   m 要在其地图中放置其映射的地图
     * @throws  NullPointerException if the specified map is null
     */
    public MyHashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }
    /**
     * 实现Map.putAll和Map构造函数
     *
     * @param m the map
     * @param evict 最初构造此映射时为false，否则为true（中继到afterNodeInsertion方法）。
     */
    final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
        int s = m.size();
        if (s > 0) {
            if (table == null) { // pre-size
                float ft = ((float)s / loadFactor) + 1.0F;
                int t = ((ft < (float)MAXIMUM_CAPACITY) ?
                                 (int)ft : MAXIMUM_CAPACITY);
                if (t > threshold)
                    threshold = tableSizeFor(t);
            }
            else if (s > threshold)
//                resize();
            for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                putVal(hash(key), key, value, false, evict);
            }
        }
    }

    /**
     * 返回此映射中的键值映射数。
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
        return size;
    }

    /**
     * 如果此映射不包含键值映射，则返回true。
     *
     * @return  true  if this map contains no key-value mappings
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 返回指定键所映射到的值；如果此映射不包含键的映射关系，则返回{@code null}。
     * 更正式地讲，如果此映射包含从键{@code k}到值{@code v}的映射，
     * 使得{@code（key == null？k == null：key.equals（k））}，
     * 然后此方法返回{@code v};否则返回{@code null}。 
     * （最多可以有一个这样的映射。）返回值{@code null}不一定表示该映射不包含该键的映射；
     * 它的返回值不能为0。映射也可能将键显式映射到{@code null}。
     * {@link #containsKey containsKey}操作可用于区分这两种情况。
     *
     * @see #put(Object, Object)
     */
    public V get(Object key) {
         Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

    /**
     * 实现Map.get和相关方法
     *
     * @param hash hash for key
     * @param key the key
     * @return the node, or null if none
     */
    final  Node<K,V> getNode(int hash, Object key) {

        return null;
    }

    /**
     * Returns  true  if this map contains a mapping for the
     * specified key.
     *
     * @param   key   The key whose presence in this map is to be tested
     * @return  true  if this map contains a mapping for the specified
     * key.
     */
    public boolean containsKey(Object key) {
        return getNode(hash(key), key) != null;
    }

    /**
     * 将指定值与该映射中的指定键相关联。如果该映射先前包含该键的映射，则将替换旧值。
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with  key , or
     *          null  if there was no mapping for  key .
     *         (A  null  return can also indicate that the map
     *         previously associated  null  with  key .)
     */
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    private V putVal(int hash, K key, V value, boolean b, boolean b1) {

        return value;
    }


    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }








    /* ------------------------------------------------------------ */
    // Tree bins

    /**
     * 进入树箱。扩展LinkedHashMap.Entry（进而扩展Node），因此可以用作常规节点或链接节点的扩展。
     */
    //看HashMap源码
    static final class TreeNode<K,V>  {

    }

    
    
    
    
    public static void main(String[] args) {
        Map<String,String> map = new HashMap<>();
    }
}
