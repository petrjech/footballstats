package com.example.jp.footballstats;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ShowStatsTest {
    @Test
    public void findLowestHigherEloTest() {
        ArrayList<Integer> testList = new ArrayList<>();
        testList.add(1);
        testList.add(1);
        testList.add(3);
        testList.add(5);
        testList.add(5);
        testList.add(7);
        testList.add(9);
        testList.add(9);
        int result = ShowStatsActivity.findLowestHigherElo(testList, 0);
        System.out.println("" + result);
        assertTrue(result == 0);
        result = ShowStatsActivity.findLowestHigherElo(testList, 1);
        System.out.println("" + result);
        assertTrue(result == 2);
        result = ShowStatsActivity.findLowestHigherElo(testList, 2);
        System.out.println("" + result);
        assertTrue(result == 2);
        result = ShowStatsActivity.findLowestHigherElo(testList, 3);
        System.out.println("" + result);
        assertTrue(result == 3);
        result = ShowStatsActivity.findLowestHigherElo(testList, 4);
        System.out.println("" + result);
        assertTrue(result == 3);
        result = ShowStatsActivity.findLowestHigherElo(testList, 5);
        System.out.println("" + result);
        assertTrue(result == 5);
        result = ShowStatsActivity.findLowestHigherElo(testList, 6);
        System.out.println("" + result);
        assertTrue(result == 5);
        result = ShowStatsActivity.findLowestHigherElo(testList, 7);
        System.out.println("" + result);
        assertTrue(result == 6);
        result = ShowStatsActivity.findLowestHigherElo(testList, 8);
        System.out.println("" + result);
        assertTrue(result == 6);
        result = ShowStatsActivity.findLowestHigherElo(testList, 9);
        System.out.println("" + result);
        assertTrue(result == 7);
    }

    @Test
    public void findHighestLowerEloTest() {
        ArrayList<Integer> testList = new ArrayList<>();
        testList.add(1);
        testList.add(1);
        testList.add(3);
        testList.add(5);
        testList.add(5);
        testList.add(7);
        testList.add(9);
        testList.add(9);
        int result = ShowStatsActivity.findHighestLowerElo(testList, 0);
        System.out.println("" + result);
        assertTrue(result == 0);
        result = ShowStatsActivity.findHighestLowerElo(testList, 1);
        System.out.println("" + result);
        assertTrue(result == 0);
        result = ShowStatsActivity.findHighestLowerElo(testList, 2);
        System.out.println("" + result);
        assertTrue(result == 1);
        result = ShowStatsActivity.findHighestLowerElo(testList, 3);
        System.out.println("" + result);
        assertTrue(result == 1);
        result = ShowStatsActivity.findHighestLowerElo(testList, 4);
        System.out.println("" + result);
        assertTrue(result == 2);
        result = ShowStatsActivity.findHighestLowerElo(testList, 5);
        System.out.println("" + result);
        assertTrue(result == 2);
        result = ShowStatsActivity.findHighestLowerElo(testList, 6);
        System.out.println("" + result);
        assertTrue(result == 4);
        result = ShowStatsActivity.findHighestLowerElo(testList, 7);
        System.out.println("" + result);
        assertTrue(result == 4);
        result = ShowStatsActivity.findHighestLowerElo(testList, 8);
        System.out.println("" + result);
        assertTrue(result == 5);
        result = ShowStatsActivity.findHighestLowerElo(testList, 9);
        System.out.println("" + result);
        assertTrue(result == 5);
        result = ShowStatsActivity.findHighestLowerElo(testList, 10);
        System.out.println("" + result);
        assertTrue(result == 7);
    }
}
