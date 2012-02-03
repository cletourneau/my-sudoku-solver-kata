package com.iweb.sudoku;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solver {
    private final Grid grid;
    private final int gridSize;
    private final int totalCellCount;
    
    private static int totalTries = 0;

    public Solver(final Grid grid) {
        gridSize = Grid.SIZE;
        totalCellCount = gridSize * gridSize;
        this.grid = grid;
    }
    
    public boolean hasDuplicates() {
        return hasDuplicatesInSameRow() || hasDuplicatesInSameColumn() || hasDuplicatesInSameSubGrid();
    }
    
    private boolean hasDuplicatesInSameSubGrid() {
        for (int subGridIndex = 0; subGridIndex < 9; ++subGridIndex) {
            final int startRow = (subGridIndex / 3) * 3;
            final int startCol = (subGridIndex % 3) * 3;
            final int endRow = startRow + 3;
            final int endCol = startCol + 3;

            final List<Integer> subGridRemainingNumbers = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
            for (int rowIndex = startRow; rowIndex < endRow; ++rowIndex) {
                for (int colIndex = startCol; colIndex < endCol; ++colIndex) {
                    final int cellIndex = rowIndex * 9 + colIndex;
                    if (grid.cellHasValue(cellIndex)) {
                        if (!subGridRemainingNumbers.remove(grid.getCell(cellIndex))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean hasDuplicatesInSameRow() {
        for (int rowIndex = 0; rowIndex < gridSize; ++rowIndex) {
            final List<Integer> rowRemainingNumbers = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
            for (int colIndex = 0; colIndex < gridSize; ++colIndex) {
                final int cellIndex = rowIndex * 9 + colIndex;
                if (grid.cellHasValue(cellIndex)) {
                    if (!rowRemainingNumbers.remove(grid.getCell(cellIndex))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasDuplicatesInSameColumn() {
        for (int colIndex = 0; colIndex < gridSize; ++colIndex) {
            final List<Integer> colRemainingNumbers = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
            for (int rowIndex = 0; rowIndex < gridSize; ++rowIndex) {
                final int cellIndex = rowIndex * 9 + colIndex;
                if (grid.cellHasValue(cellIndex)) {
                    if (!colRemainingNumbers.remove(grid.getCell(cellIndex))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean solve() {
        totalTries = 0;
        final boolean success = internalSolve(0);
        System.out.println("Total tries : " + totalTries);
        return success;
    }

    public boolean internalSolve(final int startIndex) {
        for (int cellIndex = startIndex; cellIndex < totalCellCount; ++cellIndex) {
            if (grid.cellHasValue(cellIndex)) {
                continue; // with next cell
            }
            
            final List<Integer> candidates = grid.getCandidatesFor(cellIndex);
            for (final Integer candidate : candidates) {
                totalTries++;
                grid.setCell(cellIndex, candidate);

                final boolean candidateStillAvailableForNextCell = internalSolve(cellIndex + 1);
                if (candidateStillAvailableForNextCell) {
                    return true;
                }
            }

            if (candidates.size() > 0) {
                grid.clearCell(cellIndex);
            }

            return false;
        }
        return true;
    }

    public static void main(final String[] args) throws IOException {
        final String filePath = args.length < 1 ? "./Sudoku-SolveMe.txt" : args[0];

        final Grid grid = new Grid();
        grid.loadGridFromString(readFileToString(filePath));
        System.out.println(filePath + " will be solved...");
        System.out.println(grid);

        final Solver solver = new Solver(grid);
        
        if (solver.hasDuplicates()) {
            System.out.println("Sudoku has duplicate numbers in one or more row, column or subgrid.");
            System.exit(-1);
        }
        if (!solver.solve()) {
            System.out.println(filePath + " could not be solved, maybe it has no solution?");
            System.exit(-1);
        }
        System.out.println(filePath + " solved :");
        System.out.println(grid);
    }

    private static String readFileToString(final String path) throws IOException {
        final FileInputStream stream = new FileInputStream(new File(path));
        try {
            final FileChannel fc = stream.getChannel();
            final MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

            /* Instead of using default, pass in a decoder. */
            return Charset.defaultCharset().decode(bb).toString();
        }
        finally {
            stream.close();
        }
    }
}
