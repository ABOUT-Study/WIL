package designPattern.adapter.EnumerationIterator;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

public class EnumerationIterator implements Iterator {
    Enumeration enumeration; // Iterator의 하위버전(remove가 없음)
    public EnumerationIterator(Enumeration enmt) {
        this.enumeration = enmt;
    }
    public boolean hasNext() {
        return enumeration.hasMoreElements();
    }
    public Object next() {
        return enumeration.nextElement();
    }
    public void remove() {
        throw new UnsupportedOperationException(); // 예외 처리
    }
}
