/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.batch.driver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sonorus.batch.emotion.SpeechEmoCalc;

/**
 *
 * @author dgrfi
 */
public class DGRFSpeechDriver {

    private List<String> emoResultList;

    public DGRFSpeechDriver() {
        emoResultList = new ArrayList<>();
    }

    public void calcEmoForSingleFile(File wavFile) {
        SpeechEmoCalc emoCalc = new SpeechEmoCalc(wavFile);
        emoCalc.calcEmo();
        String emoResult = emoCalc.getEmoResult();
        emoResultList.add(emoResult);
    }

    public void calcEmoForFolder(File wavFileFolder) {
        File[] wavFileList = wavFileFolder.listFiles();
        for (File wavFile : wavFileList) {
            calcEmoForSingleFile(wavFile);
        }
    }

    public void logEmoResults(String outFileName) {
        try {
            FileWriter writer = new FileWriter(outFileName);
            for (String emoResult : emoResultList) {
                writer.write(emoResult);
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(DGRFSpeechDriver.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
