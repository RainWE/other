package com.other.collection.list;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

public class MyStack<E> extends Vector<E> {
    /**
     * 创建一个空的堆栈.
     */
    public MyStack() {
    }

    /**
     * 将项目推入此堆栈的顶部。其效果与:
     * addElement(item
     * @param   item   要推入堆栈的项目。
     * @return  the <code>item</code> argument.
     * @see     java.util.Vector#addElement
     */
    public E push(E item) {
        addElement(item);

        return item;
    }

    /**
     * 删除此堆栈顶部的对象，并将该对象作为此函数的值返回。
     * @return  此堆栈顶部的对象（Vector对象的最后一项）。
     * @throws EmptyStackException  如果此堆栈为空。
     */
    public synchronized E pop() {
        E       obj;
        int     len = size();

        obj = peek();
        removeElementAt(len - 1);

        return obj;
    }

    /**
     * 在不将其从堆栈中移除的情况下，查看该堆栈顶部的对象。

     * @return 此堆栈顶部的对象（Vector对象的最后一项*）。
     * @throws  EmptyStackException  if this stack is empty.
     */
    public synchronized E peek() {
        int     len = size();

        if (len == 0)
            throw new EmptyStackException();
        return elementAt(len - 1);
    }

    /**
     * 测试此堆栈是否为空。
     *
     * @return  当且仅当此堆栈不包含任何项目时，才返回true；否则，则返回true。否则为假。
     */
    public boolean empty() {
        return size() == 0;
    }

    /**
     * 返回对象在此堆栈上的从1开始的位置。
     * 如果对象o作为此堆栈中的一个项目出现，
     * 则此方法返回到出现的最上层堆栈顶部的距离。
     * 堆栈中最顶层的项目被视为距离为1。
     * equals方法用于将o与该堆栈中的项目进行比较.
     * @param   o   the desired object.
     * @return  从堆栈顶部（对象所在的位置）开始的从1开始的位置；
     * 返回值-1表示对象不在堆栈上。
     */
    public synchronized int search(Object o) {
        int i = lastIndexOf(o);

        if (i >= 0) {
            return size() - i;
        }
        return -1;
    }




    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>();
        List<Integer> list=new Stack<>();
    }
}
