package com.other.collection.list;

import java.util.List;
import java.util.Vector;

/**
 在面试中还有可能会问到和Vector的区别，
 我大致看了一下Vector的源码，内部也是数组做的，
 区别在于Vector在API上都加了synchronized所以它是线程安全的，
 以及Vector扩容时，是翻倍size，而ArrayList是扩容50%。
 */
public class MyVector {

    public static void main(String[] args) {
        List<Integer> vetor = new Vector<>();
    }
}
