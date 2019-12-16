package net.endimyon.sdk4j.matrix;

import net.endimyon.sdk4j.SDKBlock;
import net.endimyon.sdk4j.SDKLogger;
import net.endimyon.util.Tools;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class SDKMatrix {
    private int[][] binaryElem = new int[9][9];

    public SDKMatrix(List<SDKBlock> block) {
        for (int blockID = 0; blockID < 9; blockID++) {
            SDKBlock b = block.get(blockID);
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    if (Tools.check2PowerN(b.getSolutionSet(r, c))) {
                        binaryElem[blockID / 3 * 3 + r][blockID % 3 * 3 + c] = b.getSolutionSet(r, c);
                    } else {
                        binaryElem[blockID / 3 * 3 + r][blockID % 3 * 3 + c] = 0;
                    }
                }
            }
        }
    }

    public boolean checkMatrix() {
        int bitSum;
        int addSum;
        boolean[] b = new boolean[2];
        b[0] = true;
        b[1] = true;
        for (int r = 0; r < 9; r++) {
            bitSum = 0;
            addSum = 0;
            for (int c = 0; c < 9; c++) {
                bitSum |= binaryElem[r][c];
                addSum += binaryElem[r][c];
            }
            b[0] = (addSum == bitSum) && b[0];
        }
        for (int c = 0; c < 9; c++) {
            bitSum = 0;
            addSum = 0;
            for (int r = 0; r < 9; r++) {
                bitSum |= binaryElem[r][c];
                addSum += binaryElem[r][c];
            }
            b[1] = (addSum == bitSum) && b[1];
        }
        return b[0] && b[1];
    }

    public void logBlockSet() {
        SDKLogger.log.info("---Current set:---");
        for (int r = 0; r < 9; r++) {
            StringBuilder b = new StringBuilder();
            b.append("\t");
            for (int c = 0; c < 9; c++) {
                b.append(Tools.castToDenary(binaryElem[r][c]));
                if (c != 8) {
                    b.append(",");
                }
            }
            SDKLogger.log.info("Line " + r + ":" + b + ";");
        }
    }

    public void outputFinalResult() {
        File f = new File("output-final.log");
        f.delete();
        File finalFile = new File("output-final.log");
        try {
            PrintStream ps = new PrintStream(new FileOutputStream(finalFile, true));
            for (int r = 0; r < 9; r++) {
                StringBuilder b = new StringBuilder();
                b.append("\t");
                for (int c = 0; c < 9; c++) {
                    b.append(Tools.castToDenary(binaryElem[r][c]));
                    if (c != 8) {
                        b.append(",");
                    }
                }
                ps.println("Line " + r + ":" + b + ";");
            }
            ps.close();
            Desktop.getDesktop().open(finalFile);
        } catch (IOException e) {
            SDKLogger.log.error(e, "Exception writing final result");
        }
    }
}
