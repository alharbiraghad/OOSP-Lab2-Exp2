import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

/*
 * 题目 11：三数之和（threeSum）的单元测试类
 * 学号：2024130117
 *
 * 测试用例设计的总体原则：
 * 1. 等价类划分：
 *    - 有效等价类：存在和为 0 的三元组（含正数、负数、零的混合数组）
 *    - 无效等价类：不存在和为 0 的三元组（全正数、全负数、和不为 0 的数组）
 *    - 特殊等价类：数组中有大量重复元素（检验去重逻辑是否正确）
 * 2. 边界值分析：
 *    - 数组长度为 0、1、2（不足三个元素，结果必须为空）
 *    - 数组长度恰好为 3（只可能有一个三元组）
 *    - 全为 0 的数组（结果只有一个 [0,0,0]）
 * 3. 结果比较方式：
 *    - 题目说明输出顺序不重要，因此比较前先把每个三元组排序、再把整个结果排序，
 *      这样只比较“内容”，不依赖输出顺序。
 *    - 另外检查结果中没有重复三元组、每个三元组的和都为 0。
 */
public class L2024130117_11_Test {

    private final Solution solution = new Solution();

    /** 把结果规范化：每个三元组内部排序，再对整个列表排序，便于与期望结果比较 */
    private List<List<Integer>> normalize(List<List<Integer>> result) {
        List<List<Integer>> copy = new ArrayList<>();
        for (List<Integer> triple : result) {
            List<Integer> t = new ArrayList<>(triple);
            Collections.sort(t);
            copy.add(t);
        }
        copy.sort((a, b) -> {
            for (int i = 0; i < 3; i++) {
                int c = Integer.compare(a.get(i), b.get(i));
                if (c != 0) {
                    return c;
                }
            }
            return 0;
        });
        return copy;
    }

    /** 根据二维数组构造期望结果 */
    private List<List<Integer>> expected(int[][] triples) {
        List<List<Integer>> list = new ArrayList<>();
        for (int[] t : triples) {
            list.add(Arrays.asList(t[0], t[1], t[2]));
        }
        return normalize(list);
    }

    /** 检查结果的通用性质：每个三元组和为 0，且没有重复三元组 */
    private void checkProperties(List<List<Integer>> result) {
        Set<List<Integer>> seen = new HashSet<>();
        for (List<Integer> t : normalize(result)) {
            assertEquals(3, t.size());
            assertEquals(0, t.get(0) + t.get(1) + t.get(2));
            assertTrue("结果中出现重复三元组: " + t, seen.add(t));
        }
    }

    /*
     * 测试目的：验证题目示例 1（有效等价类，有多个答案且原数组含重复元素）
     * 测试用例：nums = [-1,0,1,2,-1,-4]，期望 [[-1,-1,2],[-1,0,1]]
     */
    @Test
    public void testExample1() {
        List<List<Integer>> result = solution.threeSum(new int[] {-1, 0, 1, 2, -1, -4});
        assertEquals(expected(new int[][] {{-1, -1, 2}, {-1, 0, 1}}), normalize(result));
        checkProperties(result);
    }

    /*
     * 测试目的：验证题目示例 2（无效等价类，不存在和为 0 的三元组）
     * 测试用例：nums = [0,1,1]，期望 []
     */
    @Test
    public void testExample2NoAnswer() {
        List<List<Integer>> result = solution.threeSum(new int[] {0, 1, 1});
        assertTrue(result.isEmpty());
    }

    /*
     * 测试目的：验证题目示例 3（边界值：长度恰好为 3，且全为 0）
     * 测试用例：nums = [0,0,0]，期望 [[0,0,0]]
     */
    @Test
    public void testExample3AllZero() {
        List<List<Integer>> result = solution.threeSum(new int[] {0, 0, 0});
        assertEquals(expected(new int[][] {{0, 0, 0}}), normalize(result));
    }

    /*
     * 测试目的：验证边界值——数组元素少于 3 个时，结果必须为空，且程序不能出错
     * 测试用例：[]、[0]、[1,-1]
     */
    @Test
    public void testLessThanThreeElements() {
        assertTrue(solution.threeSum(new int[] {}).isEmpty());
        assertTrue(solution.threeSum(new int[] {0}).isEmpty());
        assertTrue(solution.threeSum(new int[] {1, -1}).isEmpty());
    }

    /*
     * 测试目的：验证无效等价类——全是正数或全是负数时不可能和为 0
     * 测试用例：[1,2,3,4]、[-5,-3,-1]
     */
    @Test
    public void testAllPositiveOrAllNegative() {
        assertTrue(solution.threeSum(new int[] {1, 2, 3, 4}).isEmpty());
        assertTrue(solution.threeSum(new int[] {-5, -3, -1}).isEmpty());
    }

    /*
     * 测试目的：验证去重逻辑——大量重复元素时，相同的三元组只能出现一次
     * 测试用例：[0,0,0,0,0] 期望 [[0,0,0]]；[-2,0,0,2,2] 期望 [[-2,0,2]]
     */
    @Test
    public void testDuplicatesRemoved() {
        List<List<Integer>> r1 = solution.threeSum(new int[] {0, 0, 0, 0, 0});
        assertEquals(expected(new int[][] {{0, 0, 0}}), normalize(r1));

        List<List<Integer>> r2 = solution.threeSum(new int[] {-2, 0, 0, 2, 2});
        assertEquals(expected(new int[][] {{-2, 0, 2}}), normalize(r2));
        checkProperties(r2);
    }

    /*
     * 测试目的：验证有效等价类中答案较多的情况，检查是否漏解
     * 测试用例：[-4,-2,-2,-2,0,1,2,2,2,3,3,4,4,6,6]
     * 期望：[[-4,-2,6],[-4,0,4],[-4,1,3],[-4,2,2],[-2,-2,4],[-2,0,2]]
     */
    @Test
    public void testManyAnswers() {
        int[] nums = {-4, -2, -2, -2, 0, 1, 2, 2, 2, 3, 3, 4, 4, 6, 6};
        List<List<Integer>> result = solution.threeSum(nums);
        int[][] exp = {
            {-4, -2, 6}, {-4, 0, 4}, {-4, 1, 3}, {-4, 2, 2}, {-2, -2, 4}, {-2, 0, 2}
        };
        assertEquals(expected(exp), normalize(result));
        checkProperties(result);
    }

    /*
     * 测试目的：验证输入数组无序时也能得到正确结果（程序内部会先排序）
     * 测试用例：[3,-1,-2,0,1,-3,2]
     * 期望：[[-3,0,3],[-3,1,2],[-2,-1,3],[-2,0,2],[-1,0,1]]
     */
    @Test
    public void testUnsortedInput() {
        List<List<Integer>> result = solution.threeSum(new int[] {3, -1, -2, 0, 1, -3, 2});
        int[][] exp = {{-3, 0, 3}, {-3, 1, 2}, {-2, -1, 3}, {-2, 0, 2}, {-1, 0, 1}};
        assertEquals(expected(exp), normalize(result));
        checkProperties(result);
    }
}
