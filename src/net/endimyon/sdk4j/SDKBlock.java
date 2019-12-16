package net.endimyon.sdk4j;

import net.endimyon.sdk4j.matrix.UnsatisfiedMatrixException;
import net.endimyon.util.Tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SDKBlock
{
    private boolean[][] uniqueness;
    private int[][] solutionSet;
    private int row;
    private int column;
    private boolean[][] archiveUniqueness;
    private int[][] archiveSolutions;

    private List<SDKCandidate> can = new ArrayList<>();

    SDKBlock(int r, int c) {
        this.uniqueness = new boolean[3][3];
        this.solutionSet = new int[3][3];
        this.archiveUniqueness = new boolean[3][3];
        this.archiveSolutions = new int[3][3];
        this.row = r;
        this.column = c;
        for (int i = 0; i < 9; i++) {
            int elem_r = i / 3 + 3 * r;
            int elem_c = i % 3 + 3 * c;
            if (SDKIterator.Elem[elem_r][elem_c] != 0) {
                this.solutionSet[i / 3][i % 3] = Tools.castToBinary(2, SDKIterator.Elem[elem_r][elem_c]);
                this.uniqueness[i / 3][i % 3] = true;
            } else {
                this.solutionSet[i / 3][i % 3] = 0;
                this.uniqueness[i / 3][i % 3] = false;
            }
        }
    }

    public SDKBlock(int r, int c, int[][] array) {
        this.uniqueness = new boolean[3][3];
        this.solutionSet = new int[3][3];
        this.archiveUniqueness = new boolean[3][3];
        this.archiveSolutions = new int[3][3];
        this.row = r;
        this.column = c;
        for (int i = 0; i < 9; i++) {
            int elem_r = i / 3 + 3 * r;
            int elem_c = i % 3 + 3 * c;
            if (array[elem_r][elem_c] != 0) {
                this.solutionSet[i / 3][i % 3] = Tools.castToBinary(2, array[elem_r][elem_c]);
                this.uniqueness[i / 3][i % 3] = true;
            } else {
                this.solutionSet[i / 3][i % 3] = 0;
                this.uniqueness[i / 3][i % 3] = false;
            }
        }
    }

    public void archive() {
        System.arraycopy(solutionSet, 0, archiveSolutions, 0 ,3);
        System.arraycopy(uniqueness, 0, archiveUniqueness, 0, 3);
    }


    void reArchive() {
        System.arraycopy(archiveSolutions, 0, solutionSet, 0 ,3);
        System.arraycopy(archiveUniqueness, 0, uniqueness, 0, 3);
    }

    boolean getUniqueness(int r, int c) {
        return this.uniqueness[r][c];
    }

    public int getSolutionSet(int r, int c) {
        return this.solutionSet[r][c];
    }

    String phase() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            s.append(this.solutionSet[i / 3][i % 3]).append(" ");
        }
        return s.toString();
    }

    void mergeSolutionSet(int r, int c) {
        if (!this.uniqueness[r][c]) {
            SDKLogger.log.debug("Block " + (row * 3 + column) + " slot " + (r * 3 + c));
            int blockSln = 0;
            int columnSln = 0;
            int rowSln = 0;
            int C = c + column * 3;
            int R = r + row * 3;
            for (int bi = 0; bi < 9; bi++) {
                int br = bi / 3;
                int bc = bi % 3;
                if (!(br == r && bc == c)) {
                    SDKLogger.log.trace("Block element:\t" + Tools.toElementString(this.solutionSet[br][bc]));
                    if (this.uniqueness[br][bc]) {
                        blockSln = blockSln + this.solutionSet[br][bc];
                    }
                }
            }
            for (int ci = 0; ci < 9; ci++) {
                if (ci != C) {
                    int index = (R / 3) * 3 + ci / 3;
                    SDKLogger.log.trace("Column element:\t" + Tools.toElementString(SDKIterator.currentBlocks.get(index).getSolutionSet(R % 3,ci % 3)));
                    if (SDKIterator.currentBlocks.get(index).getUniqueness(R % 3,ci % 3)) {
                        columnSln = columnSln + SDKIterator.currentBlocks.get(index).getSolutionSet(R % 3,ci % 3);
                    }
                }
            }
            for (int ri = 0; ri < 9; ri++) {
                if (ri != R) {
                    int index = (ri / 3) * 3 + C / 3;
                    SDKLogger.log.trace("Line element:\t" + Tools.toElementString(SDKIterator.currentBlocks.get(index).getSolutionSet(ri % 3, C % 3)));
                    if (SDKIterator.currentBlocks.get(index).getUniqueness(ri % 3,C % 3)) {
                        rowSln = rowSln + SDKIterator.currentBlocks.get(index).getSolutionSet(ri % 3, C % 3);
                    }
                }
            }
            int alreadyHad = blockSln | columnSln | rowSln;
            SDKLogger.log.trace("Result: " + blockSln + " " + columnSln + " " + rowSln + "; " +Tools.toElementString(511 - alreadyHad));
            if (alreadyHad == 511) {
                throw new UnsatisfiedMatrixException("Unable to apply new solutions to element at line " + r + ", column " + c + " in block " + (row * 3 + column) + ".");
            }
            this.solutionSet[r][c] = 511 - alreadyHad;
            if (check2PowerN(this.solutionSet[r][c])) {
                mergeUniqueness(r, c,true);
            }
        }
    }

    public void apply(int r, int c, int i) {
        this.solutionSet[r][c] = i;
    }

    public void apply(int r, int c, boolean i) {
        this.uniqueness[r][c] = i;
    }

    public void apply(SDKCandidate can) {
        apply(can.ri, can.ci, can.currentCan);
    }

    private void mergeUniqueness(int r, int c, boolean b) {
        this.uniqueness[r][c] = b;
    }

    public void buildCandidates() {
        for (int index = 0; index < 9; index++) {
            this.can.add(new SDKCandidate(index / 3, index % 3));
        }
    }

    void rebuildCandidates() {
        this.can.clear();
        buildCandidates();
    }

    public SDKCandidate can(int r, int c) {
        if (r > 2 || c > 2 || r < 0 || c < 0) {
            throw new IllegalArgumentException("Cannot get candidate by location " + r + ", " + c + ". Doesn't exist.");
        }
        return can(r * 3 + c);
    }

    public SDKCandidate can(int index) {
        if (index > 8 || index < 0) {
            throw new IllegalArgumentException("Block " + index + " does not exist.");
        }
        return can.get(index);
    }

    private boolean check2PowerN(int a) {
        boolean verified;
        int pw = 1;
        int PWCount = 0;
        for(int c2p = 1; c2p <= 10; c2p ++) {
           if (a == pw) {
               PWCount++;
           }
           pw = 2 * pw;
        }
        verified = (PWCount == 1);
        return verified;
    }

    boolean isBlockFull() {
        int allSolutions = 0;
        for (int i = 0; i < 9; i++) {
            if (this.uniqueness[i / 3][i % 3])
            allSolutions |= solutionSet[i / 3][i % 3];
        }
        return allSolutions == 511;
    }


    class SDKCandidate {
        private int ri;
        private int ci;
        private int primaryCan;
        private int currentCan;
        private boolean isCanUnique;

        private List<Integer> candidate = new ArrayList<>();
        private Iterator can;

        SDKCandidate(int r, int c) {
            int[] candidates = new int[9];
            this.primaryCan = 0;
            int sln = solutionSet[r][c];
            int bit = 1;
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                candidates[i] = sln & bit;
                if (primaryCan == 0) {
                    primaryCan = candidates[i];
                    currentCan = candidates[i];
                }
                if (candidates[i] != 0) {
                    sum++;
                    candidate.add(candidates[i]);
                }
                bit *= 2;
            }
            int v = 0;
            for (Integer i : candidates) {
                v += i;
            }
            if (v == 0){
                throw new UnsatisfiedMatrixException("Unexpected empty-set pass");
            }
            this.ri = r;
            this.ci = c;
            this.isCanUnique = sum == 1;
            this.can = candidate.iterator();
        }

        String get() {
            StringBuilder s = new StringBuilder("Can " + (ri * 3 + ci) + ":{");
            for (int i = 0; i < candidate.size(); i++) {
                s.append(Tools.castToDenary(candidate.get(i)));
                if (i < candidate.size() - 1) {
                    s.append(",");
                }
            }
            s.append("} ");
            return s.toString();
        }

        public void apply(int i) {
            solutionSet[ri][ci] = i;
            uniqueness[ri][ci] = true;
        }

        boolean getCanUniqueness() {
            return this.isCanUnique;
        }

        boolean primaryState() {
            return currentCan == primaryCan;
        }

        //Have to "rebuild" object to some reason.
        int next() {
            if (!can.hasNext()) {
                this.can = candidate.iterator();
            }
            currentCan = (int)can.next();
            return currentCan;
        }
    }
}