package com.other.collection.map;

import java.io.ObjectStreamField;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

public class MyConcurrentHashMap<K,V>{
    
    /* ---------------- Constants -------------- */

    /**
     * 可能的最大表容量。该值必须正好为1 << 30，
     * 才能保持在Java数组分配和两个表大小的索引范围内，
     * 并且由于32位哈希字段的高两位用于控制目的，因此进一步需要此值。
     */
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * 默认初始表容量。必须是2的幂（即至少1），并且最大为MAXIMUM_CAPACITY。
     */
    private static final int DEFAULT_CAPACITY = 16;

    /**
     * 最大可能的数组大小（非2的幂）。 toArray和相关方法需要。
     */
    static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * 该表的默认并发级别。未使用，但已定义为与此类的先前版本兼容。
     */
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    /**
     * 该表的负载系数。在构造函数中覆盖此值仅影响初始表容量。
     * 通常不使用实际的浮点值-使用{{code n-（n >>> 2）}
     * 这样的表达式来关联调整大小阈值会更简单。
     */
    private static final float LOAD_FACTOR = 0.75f;

    /**
     * 使用树而不是列出容器的容器计数阈值。
     * 将元素添加到至少具有这么多节点的bin中时，bin会转换为树。
     * 该值必须大于2，并且至少应为8才能与树删除的假设相吻合，
     * 该假设涉及在收缩时转换回原始垃圾箱。
     */
    static final int TREEIFY_THRESHOLD = 8;

    /**
     * 在调整大小操作期间用于取消树状化（拆分的）箱的箱计数阈值。
     * 应小于TREEIFY_THRESHOLD，并且最多6以与移除下的收缩检测相啮合。
     */
    static final int UNTREEIFY_THRESHOLD = 6;

    /**
     *可将其分类为树木的最小桌子容量。 
     * （否则，如果bin中有太多节点，则调整表的大小。）
     * 该值应至少为4 * TREEIFY_THRESHOLD，以避免调整大小和树化阈值之间发生冲突。
     */
    static final int MIN_TREEIFY_CAPACITY = 64;

    /**
     * 每个传输步骤的最小重新绑定数量。
     * 范围被细分为允许多个调整程序线程。
     * 此值用作下限，以避免调整器遇到过多的内存争用。
     * 该值应至少为DEFAULT_CAPACITY。
     */
    private static final int MIN_TRANSFER_STRIDE = 16;

    /**
     * sizeCtl中用于生成标记的位数。对于32位阵列，必须至少为6。
     */
    private static int RESIZE_STAMP_BITS = 16;

    /**
     * 可以帮助调整大小的最大线程数。必须为32-RESIZE_STAMP_BITS位。
     */
    private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;

    /**
     * 在sizeCtl中记录大小标记的位移位。
     */
    private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;

    /*
     * 节点哈希字段的编码。请参阅上面的说明。
     */
    static final int MOVED     = -1; // 用于转发节点的哈希
    static final int TREEBIN   = -2; // 散列为树的根
    static final int RESERVED  = -3; // 临时保留的哈希
    static final int HASH_BITS = 0x7fffffff; // 普通节点哈希的可用位

    /**CPUS的数量，用于限制某些尺寸 */
    static final int NCPU = Runtime.getRuntime().availableProcessors();

    /** 为了实现序列化兼容性. */
    private static final ObjectStreamField[] serialPersistentFields = {
            new ObjectStreamField("segments",Segment[].class),
            new ObjectStreamField("segmentMask", Integer.TYPE),
            new ObjectStreamField("segmentShift", Integer.TYPE)
    };


    /**
     * 先前版本中使用的精简版帮助程序类，为实现序列化兼容性而声明
     */
    static class Segment<K,V> extends ReentrantLock implements Serializable {
        private static final long serialVersionUID = 2249069246763182397L;
        final float loadFactor;
        Segment(float lf) { this.loadFactor = lf; }
    }

    /* ---------------- Nodes -------------- */

    /**
     * 键值输入。此类永远不会导出为用户可变的Map.Entry
     * （即一种支持setValue；请参见下面的MapEntry），
     * 但可以用于批量任务中使用的只读遍历。
     * 具有负哈希字段的Node的子类是特殊的，
     * 并且包含空键和值（但从不导出）。
     * 否则，键和值永远不会为空。
     */
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        volatile V val;
        volatile  Node<K,V> next;

        Node(int hash, K key, V val,  Node<K,V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }

        public final K getKey()       { return key; }
        public final V getValue()     { return val; }
        public final int hashCode()   { return key.hashCode() ^ val.hashCode(); }
        public final String toString(){ return key + "=" + val; }
        public final V setValue(V value) {
            throw new UnsupportedOperationException();
        }

        public final boolean equals(Object o) {
            Object k, v, u; Map.Entry<?,?> e;
            return ((o instanceof Map.Entry) &&
                            (k = (e = (Map.Entry<?,?>)o).getKey()) != null &&
                            (v = e.getValue()) != null &&
                            (k == key || k.equals(key)) &&
                            (v == (u = val) || v.equals(u)));
        }

        /**
         * 对map.get（）的虚拟支持；在子类中重写。
         */
        Node<K,V> find(int h, Object k) {
             Node<K,V> e = this;
            if (k != null) {
                do {
                    K ek;
                    if (e.hash == h &&
                                ((ek = e.key) == k || (ek != null && k.equals(ek))))
                        return e;
                } while ((e = e.next) != null);
            }
            return null;
        }
    }

    /* ---------------- Static utilities -------------- */

    /**
     * 将散列的较高位散布（XOR）降低，并且也将最高位强制为0。
     * 由于该表使用2的幂次掩码，因此仅在当前掩码上方的位中变化的哈希集将始终发生冲突。
     * （众所周知的示例是在小表中保存连续整数的Float键集。）
     * 因此，我们应用了一种变换，将向下传播较高位的影响。
     * 在速度，实用性和位扩展质量之间需要权衡。
     * 由于许多常见的哈希集已经合理分布（因此无法从扩展中受益），
     * 并且由于我们使用树来处理容器中的大量冲突集，
     * 因此我们仅以最便宜的方式对一些移位后的位进行XOR，
     * 以减少系统损失，以及合并最高位的影响，
     * 否则由于表范围的限制，这些位将永远不会在索引计算中使用。
     */
    static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }

    /**
     * 对于给定的所需容量，返回两个表大小的幂。
      See Hackers Delight, sec 3.2
     */
    private static final int tableSizeFor(int c) {
        int n = c - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    /**
     *如果x的形式为“类C实现Comparable <C>”，则返回x的类，否则返回null。
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

    /* ---------------- Table element access -------------- */



















    public static void main(String[] args) {
        Map<Integer,Integer> map = new ConcurrentHashMap<>();

    }
}
