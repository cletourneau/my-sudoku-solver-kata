package com.iweb.sudoku;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(JMock.class)
public class SolverTest {
    private final Mockery context = new JUnit4Mockery() {
        {
            super.setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    private final Grid grid = context.mock(Grid.class);
    private final Solver solver = new Solver(grid);

    @Test
    public void testThatGridShouldHaveDuplicatesWhenSameNumbersInSameRow() {
        context.checking(new Expectations() {
            {
                allowing(grid).cellHasValue(37);
                will(returnValue(true));
                
                allowing(grid).getCell(37);
                will(returnValue(4));

                allowing(grid).cellHasValue(43);
                will(returnValue(true));

                allowing(grid).getCell(43);
                will(returnValue(4));

                // Expect all remaining grid cells to not have a value
                allowing(grid).cellHasValue(with(any(Integer.class)));
                will(returnValue(false));
            }
        });
        assertTrue(solver.hasDuplicates());
    }

    @Test
    public void testThatGridShouldHaveDuplicatesWhenSameNumbersInSameColumn() {
        context.checking(new Expectations() {
            {
                allowing(grid).cellHasValue(15);
                will(returnValue(true));

                allowing(grid).getCell(15);
                will(returnValue(3));

                allowing(grid).cellHasValue(69);
                will(returnValue(true));

                allowing(grid).getCell(69);
                will(returnValue(3));

                // Expect all remaining grid cells to not have a value
                allowing(grid).cellHasValue(with(any(Integer.class)));
                will(returnValue(false));
            }
        });
        assertTrue(solver.hasDuplicates());
    }
    
    @Test
    public void testThatGridShouldHaveDuplicatesWhenSameNumbersInSameSubGrid() {
        context.checking(new Expectations() {
            {
                allowing(grid).cellHasValue(30);
                will(returnValue(true));

                allowing(grid).getCell(30);
                will(returnValue(7));

                allowing(grid).cellHasValue(50);
                will(returnValue(true));

                allowing(grid).getCell(50);
                will(returnValue(7));

                // Expect all remaining grid cells to not have a value
                allowing(grid).cellHasValue(with(any(Integer.class)));
                will(returnValue(false));
            }
        });

        assertTrue(solver.hasDuplicates());
    }

    @Test
    public void testThatSolveShouldWorkWhenAllTheCellsHaveAValue() {
        context.checking(new Expectations() {
            {
                allowing(grid).cellHasValue(with(any(Integer.class)));
                will(returnValue(true));
            }
        });
        assertTrue(solver.solve());
    }

    @Test
    public void testThatSolveShouldFailWhenOneCellHasNoValueAndHasNoCandidate() {
        final List<Integer> noCandidate = createEmptyCandidatesList();

        context.checking(new Expectations() {
            {
                allowing(grid).cellHasValue(with(equal(36)));
                will(returnValue(false));

                allowing(grid).getCandidatesFor(with(equal(36)));
                will(returnValue(noCandidate));

                // Expect all remaining grid cells to have a value
                allowing(grid).cellHasValue(with(any(Integer.class)));
                will(returnValue(true));
            }
        });

        assertFalse(solver.solve());
    }
    
    @Test
    public void testThatSolveShouldWorkWhenOneCellHasNoValueButHasAtLeastOneCandidate() {
        final List<Integer> oneCandidate = createCandidatesList(1);

        context.checking(new Expectations() {
            {
                allowing(grid).cellHasValue(with(equal(22)));
                will(returnValue(false));

                allowing(grid).getCandidatesFor(with(equal(22)));
                will(returnValue(oneCandidate));

                one(grid).setCell(with(equal(22)), with(equal(new Integer(1))));

                // Expect all remaining grid cells to have a value
                allowing(grid).cellHasValue(with(any(Integer.class)));
                will(returnValue(true));
            }
        });
        assertTrue(solver.solve());
    }

    @Test
    public void testThatSolverShouldWorkAsItTriesNextCandidateWhenNextCellHasNoCandidateAvailable() {
        context.checking(new Expectations() {
            {
                allowing(grid).cellHasValue(with(equal(16)));
                will(returnValue(false));

                allowing(grid).getCandidatesFor(with(equal(16)));
                will(returnValue(createCandidatesList(1, 2)));

                one(grid).cellHasValue(with(equal(17)));
                will(returnValue(false));

                one(grid).getCandidatesFor(with(equal(17)));
                will(returnValue(createEmptyCandidatesList()));

                one(grid).setCell(with(equal(16)), with(equal(1)));
                one(grid).setCell(with(equal(16)), with(equal(2)));

                // Expect all remaining grid cells to have a value
                allowing(grid).cellHasValue(with(any(Integer.class)));
                will(returnValue(true));
            }
        });

        assertTrue(solver.solve());
    }
    
    @Test
    public void testThatSolverShouldWorkAndBacktrackValueWhenNextCellHasNoCandidateAvailable() {
        context.checking(new Expectations() {
            {
                allowing(grid).cellHasValue(with(equal(16)));
                will(returnValue(false));

                allowing(grid).getCandidatesFor(with(equal(16)));
                will(returnValue(createCandidatesList(1, 2)));

                allowing(grid).cellHasValue(with(equal(17)));
                will(returnValue(false));

                allowing(grid).cellHasValue(with(equal(17)));
                will(returnValue(true));

                allowing(grid).getCandidatesFor(with(equal(17)));
                will(returnValue(createEmptyCandidatesList()));

                allowing(grid).getCandidatesFor(with(equal(17)));
                will(returnValue(createCandidatesList(1)));

                one(grid).setCell(with(equal(16)), with(equal(1)));
                one(grid).setCell(with(equal(16)), with(equal(2)));
                one(grid).clearCell(with(equal(16)));

                // Expect all remaining grid cells to have a value
                allowing(grid).cellHasValue(with(any(Integer.class)));
                will(returnValue(true));
            }
        });
        assertFalse(solver.solve());
    }
    
    @Test
    public void testThatSolverSolvesAdvancedProblem() {
        final Grid grid = new Grid();
        grid.loadGridFromString("+---+---+---+\n|...|.1.|..5|\n|.3.|8..|..4|\n|81.|..2|6..|\n+---+---+---+\n|...|5.4|.6.|\n|94.|7.6|.53|\n|.8.|1.3|...|\n+---+---+---+\n|..9|6..|.48|\n|4..|..8|.9.|\n|7..|.4.|...|\n+---+---+---+\n");
        final Solver solver = new Solver(grid);
        final boolean solved = solver.solve();
        assertTrue(solved);
    }

    @Test
    public void testThatSolverSolvesAVeryHardProblem() {
        final Grid grid = new Grid();
        grid.loadGridFromString(".......1.4.........2...........5.4.7..8...3....1.9....3..4..2...5.1........8.6...");
        final Solver solver = new Solver(grid);
        final boolean solved = solver.solve();
        System.out.println(grid);
        assertTrue(solved);
    }

    @Test
    @Ignore("This test is way to long to execute")
    public void testThatSolverSolvesAnotherVeryHardProblem() {
        final Grid grid = new Grid();
        grid.loadGridFromString(". . . |. . 5 |. 8 . . . . |6 . 1 |. 4 3 . . . |. . . |. . . ------+------+------. 1 . |5 . . |. . . . . . |1 . 6 |. . . 3 . . |. . . |. . 5 ------+------+------5 3 . |. . . |. 6 1 . . . |. . . |. . 4 . . . |. . . |. . .");
        final Solver solver = new Solver(grid);
        final boolean solved = solver.solve();
        System.out.println(grid);
        assertTrue(solved);
    }

    @Test
    public void testThatSolverSolvesAnotherOneVeryHardProblem() {
        final Grid grid = new Grid();
        grid.loadGridFromString(".....6....59.....82....8....45........3........6..3.54...325..6..................");
        final Solver solver = new Solver(grid);
        final boolean solved = solver.solve();
        System.out.println(grid);
        assertTrue(solved);
    }

    private List<Integer> createEmptyCandidatesList() {
        return new ArrayList<Integer>(0);
    }
    
    private List<Integer> createCandidatesList(final Integer... candidates) {
        return Arrays.asList(candidates);
    }

}