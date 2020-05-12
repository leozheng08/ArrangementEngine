package cn.tongdun.kunpeng.api.engine.model.policy.challenger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by lvyadong on 2020/02/17.
 */
public class WeightRoundRobin {

    //当前轮询位置
    private AtomicInteger currentIndex = new AtomicInteger(-1);
    //当前使用权重
    private AtomicInteger currentWeight = new AtomicInteger(0);
    //最大权重
    private int maxWeight;
    //权重最大公约数
    private int gcdWeight;
    //元素总数
    private int itemCount;
    //权重列表
    private List<Integer> weights = new ArrayList<>();

    /*
     * 得到两值的最大公约数
     */
    public int greaterCommonDivisor(int a, int b) {
        if (a % b == 0) {
            return b;
        } else {
            return greaterCommonDivisor(b, a % b);
        }
    }

    /*
     * 得到list中所有权重的最大公约数，实际上是两两取最大公约数d，然后得到的d
     * 与下一个权重取最大公约数，直至遍历完
     */
    public int greatestCommonDivisor(List<Integer> weights) {
        int divisor = 0;
        for (int index = 0, len = weights.size(); index < len - 1; index++) {
            if (index == 0) {
                divisor = greaterCommonDivisor(
                        weights.get(index), weights.get(index + 1));
            } else {
                divisor = greaterCommonDivisor(divisor, weights.get(index));
            }
        }
        return divisor;
    }

    /*
     * 得到list中的最大的权重
     */
    public int greatestWeight(List<Integer> weights) {
        int weight = 0;
        for (Integer item : weights) {
            if (weight < item) {
                weight = item;
            }
        }
        return weight;
    }

    public WeightRoundRobin init(List<Integer> weights) {
        this.weights = weights;
        maxWeight = greatestWeight(weights);
        gcdWeight = greatestCommonDivisor(weights);
        itemCount = weights.size();
        return this;
    }

    public Integer get() {
        while (true) {
            int current = currentIndex.get();
            int next = (current + 1) % itemCount;// 每次从上一次命中节点,的下个节点开始权值比较
            if (next == 0) {// 第0台,第size台机器,取余才会得0 (让外部所有节点权值都进行权值比较,至找到合适的权值节点)
                // 当前权值减最大公约数(gcd)相比每次减(1,2,最大公差,n)的优点:`维持整数比例关系`的`最小值表现`.公约数可保持权重平衡,最大公约数可最快的轮换节点.
                currentWeight.addAndGet(-gcdWeight);// max - gcd (权值衰减直到近一个合适的权重,才会换节点)

                if (currentWeight.get() <= 0) {// 首次:0 - gcd = 负值 / 权重衰减至最大公约数时-gcd = 0值,此时需要重置最大权值
                    currentWeight.set(maxWeight);// 为负时,取重置为最大权重(从最大权重节点开始递减选择)
                    if (currentWeight.get() == 0) {
                        return null;
                    }
                }
            }
            if (currentIndex.compareAndSet(current, next)
                    && weights.get(next) >= currentWeight.get()) {// 第一次:节点1~size权重>currentWeight(maxWeight),返回节点:最大权值
                return next;
            }
        }
    }
}
