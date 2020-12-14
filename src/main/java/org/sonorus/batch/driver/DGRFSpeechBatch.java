/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.batch.driver;

import java.io.File;
import org.hedwig.cloud.dto.HedwigAuthCredentials;
import org.sonorus.batch.dataseries.CMSClientAuthCredentialValue;

/**
 *
 * @author dgrfi
 */
public class DGRFSpeechBatch {
    public static void main(String args[]) {
        HedwigAuthCredentials authCredentials = new HedwigAuthCredentials();
        authCredentials.setProductId(4);
        authCredentials.setTenantId(1);
        authCredentials.setHedwigServer("localhost");
        authCredentials.setHedwigServerPort("8080");
        CMSClientAuthCredentialValue.AUTH_CREDENTIALS= authCredentials;
        
        CliArgs cliArgs = new CliArgs(args);
        boolean wavFilePresent = cliArgs.switchPresent("-f");
        boolean writeEmoResult = cliArgs.switchPresent("-o");
        
        if (!wavFilePresent) {
            showWrongCommand();
            System.exit(1);
        } 
        String filePath = cliArgs.switchValue("-f");
        File wavFile = new File(filePath);
        if (wavFile.isFile()) {
            System.out.println("Arguement is a file. Processing will be done on "+wavFile.getName());
            DGRFSpeechDriver dgrfsd = new DGRFSpeechDriver();
            dgrfsd.calcEmoForSingleFile(wavFile);
            if (writeEmoResult) {
                String outFilePath = cliArgs.switchValue("-o");
                dgrfsd.logEmoResults(outFilePath);
            }
        } else if (wavFile.isDirectory()){
            System.out.println("Arguement is a directory. All files under directory will be processed.");
            
            DGRFSpeechDriver dgrfsd = new DGRFSpeechDriver();
            dgrfsd.calcEmoForFolder(wavFile);
            if (writeEmoResult) {
                String outFilePath = cliArgs.switchValue("-o");
                dgrfsd.logEmoResults(outFilePath);
            }
        } else {
            System.out.println("Arguement for -f should be a file or folder.");
            System.exit(1);
            
        }

    }
    public static void showWrongCommand() {
        System.out.println("Worng syntax.");
    }
    
    
}
