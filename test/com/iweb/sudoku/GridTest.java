package com.iweb.sudoku;

import org.junit.internal.runners.JUnit4ClassRunner;
import org.junit.runner.RunWith;
import org.junit.Test;

import static org.hamcrest.Matchers.hasItems;

import static com.iweb.sudoku.Grid.*;
import static org.junit.Assert.*;

import java.util.List;

@RunWith(JUnit4ClassRunner.class)
public class GridTest {

    @Test
    public void testThatANewlyCreatedGridHasNoValueSet() {
        final Grid grid = new Grid();
        final int cellCount = SIZE * SIZE;
        for (int i = 0; i < cellCount; ++i) {
            assertFalse(grid.cellHasValue(i));
            assertNull(grid.getCell(i));
        }
    }
    
    @Test
    public void testThatCellHasAValueAfterBeingSetOne() {
        final Grid grid = new Grid();
        final Integer value = 5;
        final int cellIndex = 33;
        grid.setCell(cellIndex, value);
        
        assertTrue(grid.cellHasValue(cellIndex));
        assertEquals(value, grid.getCell(cellIndex));
    }

    @Test
    public void testThatCellHasNoValueAfterBeingSetAndCleared() {
        final Grid grid = new Grid();
        final int value = 1;
        final int cellIndex = 33;
        grid.setCell(cellIndex, value);
        
        assertTrue(grid.cellHasValue(cellIndex));
        
        grid.clearCell(cellIndex);
        assertFalse(grid.cellHasValue(cellIndex));
        assertNull(grid.getCell(cellIndex));
    }
    
    @Test
    public void testThatACellInAnEmptyGridHasAllPossibleCandidates() {
        final Grid grid = new Grid();
        final List<Integer> candidates = grid.getCandidatesFor(22);
        
        assertEquals(9, candidates.size());
        assertThat(candidates, hasItems(1, 2, 3, 4, 5, 6, 7, 8, 9));
    }
    
    @Test
    public void testThatACellShouldHaveCandidatesEliminatedForEachCellsOnTheSameRowWithValue() {
        final Grid grid = new Grid();
        grid.setCell(4, 9);
        grid.setCell(5, 3);
        grid.setCell(9, 4); // Not on same row

        final List<Integer> candidates = grid.getCandidatesFor(6);
        assertEquals(7, candidates.size());
        assertThat(candidates, hasItems(1, 2, 4, 5, 6, 7, 8));
    }

    @Test
    public void testThatACellShouldHaveCandidatesEliminatedForEachCellsOnTheSameColumnWithValue() {
        final Grid grid = new Grid();
        grid.setCell(1, 1);
        grid.setCell(19, 8);
        grid.setCell(8, 9); // Not on same column

        final List<Integer> candidates = grid.getCandidatesFor(10);
        assertEquals(7, candidates.size());
        assertThat(candidates, hasItems(2, 3, 4, 5, 6, 7, 9));
    }

    @Test
    public void testThatACellShouldHaveCandidatesEliminatedForEachCellsInTheSameSubGrid() {
        final Grid grid = new Grid();
        grid.setCell(80, 4);
        grid.setCell(70, 5);
        grid.setCell(40, 6); // Not in same sub grid

        final List<Integer> candidates = grid.getCandidatesFor(60);
        assertEquals(7, candidates.size());
        assertThat(candidates, hasItems(1, 2, 3, 6, 7, 8, 9));
    }
    
    @Test
    public void testThatLoadingAGridFromAnInvalidStringShouldNotSetAnyCell() {
        final String invalidGrid = "....643!;";
        final Grid grid = new Grid();
        grid.loadGridFromString(invalidGrid);

        final int cellCount = SIZE * SIZE;
        for (int i = 0; i < cellCount; ++i) {
            assertFalse(grid.cellHasValue(i));
        }
   }
    
    @Test
    public void testThatLoadGridFromStringShouldSetTheRightCellsAndIgnoreGarbage() {
        final StringBuilder validGrid = new StringBuilder(100);
        validGrid
                .append("+---+---+---+").append("\n")
                .append("|1..|...|...|").append("\n")
                .append("|...|...|...|").append("\n")
                .append("|...|...|...|").append("\n")
                .append("+---+---+---+").append("\n")
                .append("|...|...|...|").append("\n")
                .append("|...|...|...|").append("\n")
                .append("|2..|...|...|").append("\n")
                .append("+---+---+---+").append("\n")
                .append("|...|...|...|").append("\n")
                .append("|...|...|...|").append("\n")
                .append("|...|...|..3|").append("\n")
                .append("+---+---+---+");
        final Grid grid = new Grid();
        grid.loadGridFromString(validGrid.toString());
        
        assertTrue(grid.cellHasValue(0));
        assertTrue(grid.cellHasValue(45));
        assertTrue(grid.cellHasValue(80));
    }

    @Test
    public void testThatToStringGeneratesTheRightOutputForAnEmptyGrid() {
        final Grid emptyGrid = new Grid();
        final String output = emptyGrid.toString();
        final String[] lines = output.split("\n");

        assertEquals(13, lines.length);

        for (int lineIndex = 0; lineIndex < lines.length; ++lineIndex) {
            final String line = lines[lineIndex];
            assertEquals(13, line.length());
            if (lineIndex % 4 == 0) {
                assertEquals("+---+---+---+", line);
            } else {
                assertEquals("|...|...|...|", line);
            }
        }
    }

    @Test
    public void testThatToStringGeneratesTheRightOutputWithSomeValues() {
        final String sudoku = "693784512487512936125963874932651487568247391741398625319475268856129743274836159";
        final Grid filledGrid = new Grid();
        filledGrid.loadGridFromString(sudoku);
        final String output = filledGrid.toString();
        final String[] lines = output.split("\n");

        assertEquals("+---+---+---+", lines[0]);
        assertEquals("|693|784|512|", lines[1]);
        assertEquals("|487|512|936|", lines[2]);
        assertEquals("|125|963|874|", lines[3]);
        assertEquals("+---+---+---+", lines[4]);
        assertEquals("|932|651|487|", lines[5]);
        assertEquals("|568|247|391|", lines[6]);
        assertEquals("|741|398|625|", lines[7]);
        assertEquals("+---+---+---+", lines[8]);
        assertEquals("|319|475|268|", lines[9]);
        assertEquals("|856|129|743|", lines[10]);
        assertEquals("|274|836|159|", lines[11]);
        assertEquals("+---+---+---+", lines[12]);

        assertEquals(13, lines.length);
    }
}