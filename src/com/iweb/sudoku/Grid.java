package com.iweb.sudoku;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Grid {
    public static final int SIZE = 9;

    private final Integer[][] cells = new Integer[SIZE][SIZE];
    
    public boolean cellHasValue(final int cellIndex) {
        final int row = cellIndex / SIZE;
        final int col = cellIndex % SIZE;
        return cells[row][col] != null;
    }
    
    private static List<Integer> createFullCandidateList() {
        // Arrays.asList is fixed size, need to copy it
        final List<Integer> candidates = new ArrayList<Integer>(15);
        candidates.addAll(Arrays.asList(1, 2, 3, 5, 4, 6, 7, 8, 9));
        return candidates;
    }

    public List<Integer> getCandidatesFor(final int cellIndex) {
        final int row = cellIndex / SIZE;
        final int col = cellIndex % SIZE;

        final List<Integer> candidates = createFullCandidateList();

        removeSameRowValuesFromCandidates(candidates, row);
        removeSameColumnValuesFromCandidates(candidates, col);
        removeSameSubGridCandidatesFromCandidates(row, col, candidates);

        return candidates;
    }

    private void removeSameSubGridCandidatesFromCandidates(final int row, final int col, final List<Integer> candidates) {
        final int startRow = row - (row % 3);
        final int startColumn = col - (col % 3);
        final int endRow = startRow + 3;
        final int endColumn = startColumn + 3;

        for (int i = startRow; i < endRow; ++i) {
            for (int j = startColumn; j < endColumn; ++j) {
                final Integer value = cells[i][j];
                if (value != null) {
                    candidates.remove(value);
                }
            }
        }
    }

    private void removeSameColumnValuesFromCandidates(final List<Integer> candidates, final int col) {
        for (int row = 0; row < SIZE; ++row) {
            final Integer value = cells[row][col];
            if (value != null) {
                candidates.remove(value);
            }
        }
    }

    private void removeSameRowValuesFromCandidates(final List<Integer> candidates, final int row) {
        for (final Integer sameRowCell : cells[row]) {
            if (sameRowCell != null) {
                candidates.remove(sameRowCell);
            }
        }
    }

    public void setCell(final int cellIndex, final Integer value) {
        final int row = cellIndex / SIZE;
        final int col = cellIndex % SIZE;
        cells[row][col] = value;
    }

    public void clearCell(final int cellIndex) {
        setCell(cellIndex, null);
    }

    public void loadGridFromString(final String dirtyGrid) {
        final String cleanGrid = dirtyGrid.replaceAll("[^\\.1-9]","");
        if (cleanGrid.length() != 81) return; // Ignoring invalid grids

        for (int i = 0; i < cleanGrid.length(); ++i) {
            final String value = String.valueOf(cleanGrid.charAt(i));
            setCell(i, value.equals(".") ? null : Integer.parseInt(value));
        }
    }
    
    public String toString() {
        final StringBuilder buf = new StringBuilder(100);

        int rowIndex = 0;
        for (final Integer[] row : cells) {
            if (rowIndex % 3 == 0) {
                buf.append("+---+---+---+\n");
            }
            buf.append(MessageFormat.format("|{0}{1}{2}|{3}{4}{5}|{6}{7}{8}|\n", transformNullToPoints(row)));
            rowIndex++;
        }
        buf.append("+---+---+---+");
        return buf.toString();
    }

    private static String[] transformNullToPoints(final Integer[] row) {
        final String[] values = new String[row.length];
        for (int i = 0; i < row.length; ++i) {
            values[i] = row[i] == null ? "." : Integer.toString(row[i]);
        }
        return values;
    }

    public Integer getCell(final int cellIndex) {
        final int row = cellIndex / SIZE;
        final int col = cellIndex % SIZE;
        return cells[row][col];
    }
}
