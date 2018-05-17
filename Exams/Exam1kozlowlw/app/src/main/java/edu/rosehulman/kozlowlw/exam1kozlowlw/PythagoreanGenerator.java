package edu.rosehulman.kozlowlw.exam1kozlowlw;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Created by Matt Boutell on 3/12/18.
 */

public class PythagoreanGenerator {

    private Queue<TripleWithMultiplicity> triples;
    private boolean hasGenerated;
    private Triple trueTriple;
    private Random randomizer;
    private boolean isPotentialATrueTriple;
    private static final double PROBABILITY_OF_GENERATING_A_TRUE_TRIPLE = 0.501;

    public PythagoreanGenerator() {
        triples = new LinkedList<>();
        triples.add(new TripleWithMultiplicity(3, 4, 5, 1));
        randomizer = new Random();
        hasGenerated = false;
        isPotentialATrueTriple = false;
    }

    public PythagoreanGenerator(int seed) {
        this();
        randomizer = new Random(seed);
    }

    public Triple generatePotentialTriple() {
        dequeueTrueTripleAndEnqueueItsNeighbors();
        hasGenerated = true;
        return aPossiblyChangedTriple();
    }

    private void dequeueTrueTripleAndEnqueueItsNeighbors() {
        TripleWithMultiplicity current = triples.poll();
        putNewTriplesInQueue(current);
        trueTriple = PythagoreanGenerator.tripleFromOneWithMultiplicity(current);
    }

    private Triple aPossiblyChangedTriple() {
        isPotentialATrueTriple = randomizer.nextDouble() < PROBABILITY_OF_GENERATING_A_TRUE_TRIPLE;
        if (isPotentialATrueTriple) {
            return trueTriple;
        } else {
            return makeTripleWithOneSideChangedByOne();
        }
    }

    private void putNewTriplesInQueue(TripleWithMultiplicity t) {
        // From UAD Tree, http://www.maths.surrey.ac.uk/hosted-sites/R.Knott/Pythag/pythag.html#section5
        // Alternative to m>m, http://www.maths.surrey.ac.uk/hosted-sites/R.Knott/Pythag/pythag.html#mnformula
        List<TripleWithMultiplicity> newTriples = new ArrayList<>();
        newTriples.add(t.generateNextMultiple());
        newTriples.add(t.generateUp());
        newTriples.add(t.generateAlong());
        newTriples.add(t.generateDown());
        // CONSIDER: Shuffle first
        triples.addAll(newTriples);
    }

    private Triple makeTripleWithOneSideChangedByOne() {
        int whichToChange = randomizer.nextInt(3);
        int amountToChange = randomizer.nextBoolean() ? 1 : -1;

        int amountToChangeA = whichToChange == 0 ? amountToChange : 0;
        int amountToChangeB = whichToChange == 1 ? amountToChange : 0;
        int amountToChangeH = whichToChange == 2 ? amountToChange : 0;
        return new Triple(trueTriple.a + amountToChangeA, trueTriple.b + amountToChangeB, trueTriple.h + amountToChangeH );
    }


    public boolean isPotentialATrueTriple() {
        return isPotentialATrueTriple;
    }

    public Triple getTrueTriple() throws IllegalStateException {
        if (!hasGenerated) {
            throw new IllegalStateException("Asked for a true triple before one was generated");
        }
        return trueTriple;
    }

    public boolean hasGeneratedATriple() {
        return hasGenerated;
    }

    private class TripleWithMultiplicity extends Triple {
        int multiple;

        public TripleWithMultiplicity(int a, int b, int h, int multiple) {
            super(a, b, h);
            this.multiple = multiple;
        }

        @Override
        public String toString() {
            return super.toString() + ","+multiple;
        }

        TripleWithMultiplicity generateUp() {
            return new TripleWithMultiplicity(a - 2*b + 2*h, 2*a - b + 2*h, 2*a - 2*b + 3*h, 1);
        }

        TripleWithMultiplicity generateAlong() {
            return new TripleWithMultiplicity(a + 2*b + 2*h, 2*a + b + 2*h, 2*a + 2*b + 3*h, 1);
        }

        TripleWithMultiplicity generateDown() {
            return new TripleWithMultiplicity(-a + 2*b + 2*h, -2*a + b + 2*h, -2*a + 2*b + 3*h, 1);
        }

        public TripleWithMultiplicity generateNextMultiple() {
            return new TripleWithMultiplicity(a, b, h, multiple + 1);
        }
    }

    private static Triple tripleFromOneWithMultiplicity(TripleWithMultiplicity source) {
        return new Triple(source.a*source.multiple, source.b*source.multiple, source.h*source.multiple);
    }
}

