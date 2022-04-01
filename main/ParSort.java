package edu.neu.coe.info6205.sort.par;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * TODO tidy it up a bit.
 */
class ParSort {

    public static int cutoff = 1000;
    public static int threadCount = 2;
    public static ForkJoinPool customFJP = new ForkJoinPool(threadCount);


    public static void sort(int[] array, int from, int to) {
        if (to - from < cutoff) Arrays.sort(array, from, to);
        else {
            // FIXME next few lines should be removed from public repo.
            // Lines have been modified.
            CompletableFuture<int[]> parsort1 = CompletableFuture.supplyAsync(()-> parsort(array, from, (to - from) / 2), customFJP);
            CompletableFuture<int[]> parsort2 =  CompletableFuture.supplyAsync(()->parsort(array, from + (to - from) / 2, to), customFJP);
            CompletableFuture<int[]> parsort = parsort1.thenCombine(parsort2, (xs1, xs2) -> {
                int[] result = new int[xs1.length + xs2.length];
                // TO IMPLEMENT
                // Initial indexes of first and second subarrays
                int i = 0, j = 0;

                for (int k = 0; k < result.length; k++) {
                    if (i == xs1.length) {
                        result[k] = xs2[j++];
                    } else if (j == xs2.length) {
                        result[k] = xs1[i++];
                    } else if (xs1[i] <= xs2[j]) {
                        result[k] = xs1[i++];
                    } else {
                        result[k] = xs2[j++];
                    }
                }
                return result;
            });

            parsort.whenComplete((result, throwable) -> System.arraycopy(result, 0, array, from, result.length));
//            System.out.println("# threads: "+ ForkJoinPool.commonPool().getRunningThreadCount());
            parsort.join();
        }
    }

    private static int[] parsort(int[] array, int from, int to) {

                    int[] result = new int[to - from];
                    // TO IMPLEMENT
                    System.arraycopy(array, from, result, 0, result.length);
                    sort(result, 0, to - from);
                    return result;
                }


}