package net.endimyon.sdk4j.matrix;

import net.endimyon.sdk4j.SDKBlock;
import net.endimyon.sdk4j.SDKLogger;
import net.endimyon.util.Tools;

import java.util.ArrayList;
import java.util.List;

public class SDKMatrixContainer {
    private List<SDKBlock> bs = new ArrayList<>();
    private static int count = 1;

    public SDKMatrixContainer(List<SDKBlock> blockSet) {
        SDKLogger.log.info("Building new matrix.");
        int[][] binaryElem = new int[9][9];
        int[][] decElem = new int[9][9];
        for (int blockID = 0; blockID < 9; blockID++) {
            SDKBlock b = blockSet.get(blockID);
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    binaryElem[blockID / 3 * 3 + r][blockID % 3 * 3 + c] = b.getSolutionSet(r, c);
                }
            }
        }

        SDKLogger.log.debug("---Solved elements:---");
        for (int r = 0; r < 9; r++) {
            StringBuilder tempString = new StringBuilder();
            for (int c = 0; c < 9; c++) {
                if (Tools.check2PowerN(binaryElem[r][c])) {
                    decElem[r][c] = Tools.castToDenary(binaryElem[r][c]);
                } else {
                    decElem[r][c] = 0;
                }
                tempString.append(decElem[r][c]).append(" ");
            }
            SDKLogger.log.debug("Line" + (r + 1) + ": " + tempString.toString());
        }

        for (int index = 0; index < 9; index++) {
            bs.add(new SDKBlock(index / 3, index % 3, decElem));
        }

        for (int index = 0; index < 9; index++) {
            SDKLogger.log.debug("Block " + index + ": ");
            StringBuilder s = new StringBuilder();
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    bs.get(index).apply(r, c, binaryElem[index / 3 * 3 + r][index % 3 * 3 + c]);
                    s.append(bs.get(index).getSolutionSet(r, c)).append(" ");
                }
            }
            SDKLogger.log.debug(s.toString());
        }

        for (int index = 0; index < 9; index++) {
            bs.get(index).archive();
            bs.get(index).buildCandidates();
        }
    }

    public List<SDKBlock> copyBlockSet() {
        count++;
        SDKLogger.log.info("Now there are " + count + " block sets in the list.");
        return this.bs;
    }
}
