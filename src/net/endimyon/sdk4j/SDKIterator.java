package net.endimyon.sdk4j;

import net.endimyon.sdk4j.matrix.SDKMatrix;
import net.endimyon.sdk4j.matrix.SDKMatrixContainer;
import net.endimyon.sdk4j.matrix.UnsatisfiedMatrixException;
import net.endimyon.util.Tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class SDKIterator {
    static int[][] Elem = new int[9][9];
    private boolean solved = false;
    private int loopCount = 0;
    private int lastSet;
    private int currentBlockSet = 0;
    static List<SDKBlock> currentBlocks;
    private List<List<SDKBlock>> blockSets;
    private Set<String> blockPhase = new HashSet<>();
    private Set<String> globalPhase = new HashSet<>();

    SDKIterator() {
        this.blockSets = new ArrayList<>();
        List<SDKBlock> block = new ArrayList<>();
        BufferedReader br;
        int numCount = 0;
        try {
            SDKLogger.log.info("Loading Sudoku matrix from file \"sudoku.txt\".");
            br = new BufferedReader(new FileReader("sudoku.txt"));
            int file_num;
            char file_ch;
            int loopCount = 0;
            while ((file_num = br.read()) != -1) {
                file_ch = (char) file_num;
                int row = loopCount / 9;
                int col = loopCount % 9;
                if (isNumber(file_ch)) {
                    if (isPositive(file_ch)) {
                        numCount++;
                    }
                    Elem[row][col] = file_ch - '0';
                    loopCount = (loopCount + 1) % 81;
                }
            }
        } catch (FileNotFoundException fe) {
            throw new RuntimeException("Please put the sudoku.txt which contains an unsolved standard Sudoku in the same folder." , fe);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (numCount < 18) {
            //directly terminate this program...
            throw new UnsatisfiedMatrixException("Cannot load this Sudoku matrix. It might need further mathematical proof.");
        }
        for (int i = 0; i < 9; i++) {
            block.add(new SDKBlock(i / 3, i % 3));
        }
        blockSets.add(block);
        currentBlocks = blockSets.get(currentBlockSet);
    }

    private void checkMatrix() {
        int bitSum;
        int addSum;
        lastSet = 0;
        for (int r = 0; r < 9; r++) {
            bitSum = 0;
            addSum = 0;
            for (int c = 0; c < 9; c++) {
                bitSum |= Tools.castToBinary(2, Elem[r][c]);
                addSum += Tools.castToBinary(2, Elem[r][c]);
            }
            if (addSum != bitSum) {
                throw new UnsatisfiedMatrixException("There are duplicate elements in line " + r + ".");
            }
            lastSet += bitSum;
        }
        for (int c = 0; c < 9; c++) {
            bitSum = 0;
            addSum = 0;
            for (int r = 0; r < 9; r++) {
                bitSum |= Tools.castToBinary(2, Elem[r][c]);
                addSum += Tools.castToBinary(2, Elem[r][c]);
            }
            if (addSum != bitSum) {
                throw new UnsatisfiedMatrixException("There are duplicate elements in column " + c + ".");
            }
        }
        for (int blk = 0; blk < 9; blk++) {
            bitSum = 0;
            addSum = 0;
            for (int loc = 0; loc < 9; loc++) {
                bitSum |= currentBlocks.get(blk).getSolutionSet(loc / 3, loc % 3);
                addSum += currentBlocks.get(blk).getSolutionSet(loc / 3, loc % 3);
            }
            if (addSum != bitSum) {
                throw new UnsatisfiedMatrixException("There are duplicate elements in block " + blk + ".");
            }
        }
    }

    void run() {
        SDKLogger.log.info("Verifying matrix...");
        checkMatrix();
        SDKLogger.log.info("Current matrix:");
        for (int i = 0; i < 9; i++) {
            StringBuilder logContent = new StringBuilder();
            for (int j = 0; j < 9; j++) {
                logContent.append(getElement(i, j)).append(",");
            }
            SDKLogger.log.info("Line " + (i + 1) + ":\t" + logContent);
        }
        SDKLogger.log.info("Matrix verified. Beginning to solve it.");
        int blockIndex = 0;
        while (!solved) {
            for (int elem_num = 0; elem_num < 9; elem_num++) {
                int el_r = elem_num / 3;
                int el_c = elem_num % 3;
                if (!currentBlocks.get(blockIndex).getUniqueness(el_r, el_c)) {
                    currentBlocks.get(blockIndex).mergeSolutionSet(el_r, el_c);
                }
            }
            blockIndex = (blockIndex + 1) % 9;
            if (blockIndex == 0) {
                solved = true;
                loopCount++;
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        boolean solved1 = currentBlocks.get(j).getUniqueness(i / 3, i % 3);
                        solved = solved1 && solved;
                    }
                }
                if (!needToContinue()) {
                    break;
                }
            }
        }
        if (!solved) {
            new SDKMatrix(currentBlocks).logBlockSet();
            SDKLogger.log.info("Adding new matrix to the to-do list.");
            handleCandidateIterator();
        }
        for (int i = 0; i < 9; i++) {
            if (!blockPhase.contains(currentBlocks.get(i).phase())) {
                blockPhase.add(currentBlocks.get(i).phase());
            } else {
                throw new UnsatisfiedMatrixException("Detected duplicate blocks without using candidate mode. This math problem is unable to solve.");
            }
        }
        blockPhase.clear();
        if (solved) {
            SDKLogger.log.info("After " + loopCount + " time(s) of attempt, finally...");
        } else {
            SDKLogger.log.error("After " + loopCount + " time(s) of attempt, finally... I don't know how to solve it.");
        }
        SDKLogger.log.info(" -- Final result: --");
        for (int i = 0; i < 9; i++) {
            StringBuilder logContent = new StringBuilder();
            for (int j = 0; j < 9; j++) {
                logContent.append(getElement(i, j)).append(",");
            }
            SDKLogger.log.info("Line " + (i + 1) + ":\t" + logContent);
        }
        if (solved) {
            (new SDKMatrix(currentBlocks)).outputFinalResult();
        }
    }

    private void handleCandidateIterator() {
        int[] defaultIndex = {4,5,3,1,7,0,2,6,8};
        for (int blk = 0; blk < 9; blk++) {
            (currentBlocks.get(blk)).archive();
            (currentBlocks.get(blk)).rebuildCandidates();
        }
        int emptyLoop = 0;
        for (int i = 0;;) {
            if (!(currentBlocks.get(defaultIndex[i])).isBlockFull()) {
                emptyLoop = 0;
                runCandidateIterator(defaultIndex[i]);
                blockSets.remove(0);
                if (!solved) {
                    currentBlockSet++;
                    currentBlocks = blockSets.get(0);
                    for (int blk = 0; blk < 9; blk++) {
                        (currentBlocks.get(blk)).archive();
                        (currentBlocks.get(blk)).rebuildCandidates();
                    }
                } else {
                    for (int j = 0; j < 9; j++){
                        if (!currentBlocks.get(i).isBlockFull()) {
                            throw new IllegalStateException("Unexpected empty-block pass in set " + currentBlockSet + ", block " + i);
                        }
                    }
                    break;
                }
            } else {
                emptyLoop++;
            }
            i = (i + 1) % 9;
            watchDog(emptyLoop);
        }
    }

    private void watchDog(int i) {
        if (i > 81) {
            throw new IllegalStateException("Infinite loop without executing task");
        }
    }

    private void runCandidateIterator(int defaultBlockName) {
        List<SDKBlock.SDKCandidate> cans = new ArrayList<>();
        SDKLogger.log.info("Loading unsolved matrix " + currentBlockSet + " from the to-do list.");
        SDKLogger.log.info("Using block " + defaultBlockName + " as the target block to directly apply new candidates.");
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            if (!currentBlocks.get(defaultBlockName).can(i).getCanUniqueness()) {
                cans.add(currentBlocks.get(defaultBlockName).can(i));
                string.append(currentBlocks.get(defaultBlockName).can(i).get());
            }
        }
        SDKLogger.log.info("Attempt to solve loaded matrix " + currentBlockSet + ":");
        new SDKMatrix(currentBlocks).logBlockSet();
        SDKLogger.log.info("Candidates: " + string);
        boolean microBlockError = false;
        //Make sure we don't step into an infinite loop
        Set<String> microBlocks = new HashSet<>();
        while (!solved) {
            int[] currentCandidate = new int[cans.size()];
            StringBuilder s = new StringBuilder();
            try {
                boolean microBlockSolved = false;
                while (!microBlockSolved) {
                    int level = 1;
                    for (int i = 0; i < 9; i++) {
                        currentBlocks.get(i).reArchive();
                    }
                    for (int index = 0; index < level; index++) {
                        currentCandidate[index] = cans.get(index).next();
                        if (cans.get(index).primaryState()) {
                            if (level < cans.size()) {
                                level++;
                            } else {
                                break;
                            }
                        }
                    }
                    StringBuilder sh = new StringBuilder();
                    for (Integer i : currentCandidate) {
                        sh.append(i).append(" ");
                    }
                    s = new StringBuilder();
                    //Apply current candidates to the global matrix.
                    for (int i = 0; i < cans.size(); i++) {
                        cans.get(i).apply(currentCandidate[i]);
                        s.append(Tools.castToDenary(currentCandidate[i])).append(" ");
                    }
                    microBlockSolved = currentBlocks.get(defaultBlockName).isBlockFull() && (new SDKMatrix(currentBlocks)).checkMatrix();
                    if (!microBlocks.contains(sh.toString())) {
                        microBlocks.add(sh.toString());
                    } else {
                        microBlockError = true;
                        break;
                    }
                }
                if (microBlockError) {
                    break;
                }
                SDKLogger.log.info("Preparing for merging");
                new SDKMatrix(currentBlocks).logBlockSet();

                int blockName = 0;
                boolean globalSolved = true;
                int emptyLoop = 0;
                while (!this.solved) {
                    boolean valid = true;
                    for (int index = 0; index < 9; index++) {
                        if (!currentBlocks.get(index).getUniqueness(index / 3, index % 3)) {
                            currentBlocks.get(blockName).mergeSolutionSet(index / 3, index % 3);
                            emptyLoop = 0;
                        } else {
                            emptyLoop++;
                        }
                        valid &= currentBlocks.get(blockName).isBlockFull();
                    }
                    globalSolved &= valid;
                    blockName = (blockName + 1) % 9;
                    if (blockName == 0) {
                        new SDKMatrix(currentBlocks).logBlockSet();
                        solved = globalSolved && (new SDKMatrix(currentBlocks)).checkMatrix();
                        if (!needToContinue()) {
                            break;
                        }
                        watchDog(emptyLoop);
                    }
                }

                StringBuilder globalContent = new StringBuilder();
                SDKLogger.log.debug(" -- Debug info: -- ");
                for (int i = 0; i < 9; i++) {
                    StringBuilder logContent = new StringBuilder();
                    for (int j = 0; j < 9; j++) {
                        logContent.append(getElement(i, j)).append(",");
                    }
                    SDKLogger.log.debug("Line " + (i + 1) + ":\t" + logContent);
                    logContent.append("\r\n");
                    globalContent.append(logContent);
                }
                SDKLogger.log.info("Adding new matrix to the to-do list.");
                if (!globalPhase.contains(globalContent.toString()) && (new SDKMatrix(currentBlocks)).checkMatrix()) {
                    globalPhase.add(globalContent.toString());
                    if ((new SDKMatrix(currentBlocks)).checkMatrix()) {
                        blockSets.add(new SDKMatrixContainer(currentBlocks).copyBlockSet());
                    }
                } else {
                    SDKLogger.log.info("Used up all possibilities. Turn to newly added matrices.");
                    globalPhase.clear();
                    break;
                }
                loopCount++;
            } catch (UnsatisfiedMatrixException ume) {
                //This UnsatisfiedMatrixException represents a candidate set with errors, and we can turn to another one.
                SDKLogger.log.info("Fail to solve Sudoku matrix by applying candidate set '" + s + "'. BUT THIS IS NOT AN ERROR.");
                SDKLogger.log.info(ume.toString());
                SDKLogger.log.info("Trying new candidate set.");
            } catch (Throwable e) {
                //But other exceptions such as CMEs or OutOfMemoryErrors doesn't mean we can turn to other candidate sets.
                SDKLogger.log.error(e, "Encountered an unexpected exception");
                throw new java.lang.RuntimeException("Unexpected error", e);
            }
        }
    }

    private boolean isNumber(char i) {
        int j = i - '0';
        return (j >= 0 && j < 10);
    }

    private boolean isPositive(char i) {
        int j = i - '0';
        return (j > 0 && j < 10);
    }

    private boolean needToContinue() {
        int currentSet = 0;
        for (int block = 0; block < 9; block++) {
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    currentSet += currentBlocks.get(block).getSolutionSet(r,c);
                }
            }
        }
        boolean isTheSame = (currentSet == lastSet);
        lastSet = currentSet;
        return !isTheSame;
    }

    private int getElement(int r, int c) {
        int blockName = (r / 3) * 3 + c / 3;
        int er = r % 3;
        int ec = c % 3;
        if (currentBlocks.get(blockName).getUniqueness(er, ec)) {
            return Tools.castToDenary(currentBlocks.get(blockName).getSolutionSet(er, ec));
        } else {
            return 0;
        }
    }
}
