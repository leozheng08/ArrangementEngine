package cn.tongdun.kunpeng.api.engine.model.rule.util;

/**
 * Created by lvyadong on 2020/02/21.
 */
public class InterruptibleCharSequence implements CharSequence {

    private CharSequence inner;

    public InterruptibleCharSequence(CharSequence inner) {
        super();
        this.inner = inner;
    }

    @Override
    public int length() {
        return inner.length();
    }

    @Override
    public char charAt(int index) {
        if (Thread.currentThread().isInterrupted()) {
            throw new RuntimeException("Interrupted!");
        }
        return inner.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return new InterruptibleCharSequence(inner.subSequence(start, end));
    }

    @Override
    public String toString() {
        return inner.toString();
    }
}

