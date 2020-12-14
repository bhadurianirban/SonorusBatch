/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.batch.emotion;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sonorus.core.client.SonorusCoreClient;
import org.hedwig.leviosa.constants.CMSConstants;

import org.patronus.termmeta.DataSeriesMeta;
import org.patronus.termmeta.MFDFAResultsMeta;
import org.sonorus.batch.dataseries.CMSClientAuthCredentialValue;
import org.sonorus.batch.dataseries.DataSeriesAdd;
import org.patronus.core.client.PatronusCoreClient;
import org.patronus.core.dto.FractalDTO;
import org.patronus.response.FractalResponseCode;
import org.patronus.response.FractalResponseMessage;
import org.sonorus.core.dto.SonorusDTO;
import org.sonorus.core.dto.SonorusResultsMeta;

/**
 *
 * @author dgrfi
 */
public class SpeechEmoCalc {

    private File wavFile;
    private Map<String, Object> speechEmoTermInstance;
    private Map<String, Object> dataSeriesTermInstance;
    private Map<String, Object> speechEmoMfdfaTermInstance;
    private String emoResult;

    public SpeechEmoCalc(File wavFile) {
        this.wavFile = wavFile;
    }

    public void calcEmo() {
        Logger.getLogger(SpeechEmoCalc.class.getName()).log(Level.INFO, "Uploading {0}to DGRF Speech Server", wavFile.getName());
        DataSeriesAdd dsa = new DataSeriesAdd(wavFile);
        dsa.uploadWav();
        Logger.getLogger(DataSeriesAdd.class.getName()).log(Level.INFO, "File {0} uploaded successfully.",wavFile.getName());
        dataSeriesTermInstance = dsa.getDataseriesTermInstance();
        Logger.getLogger(SpeechEmoCalc.class.getName()).log(Level.INFO, "Calculating Emotion on DGRF Speech server");
        decideEmo();
        Logger.getLogger(SpeechEmoCalc.class.getName()).log(Level.INFO, "Getting results from DGRF Speech server");
        captureEmoResult();
        Logger.getLogger(SpeechEmoCalc.class.getName()).log(Level.INFO, "Cleaning up DGRF Speech server");
        deleteEmoResult();
        Logger.getLogger(SpeechEmoCalc.class.getName()).log(Level.INFO, "Results obtained {0}",emoResult);
    }

    private void decideEmo() {
        

        //code for DGRFSpeech
        SonorusDTO dGRFSpeechDTO = new SonorusDTO();
        FractalResponseMessage responseMessage = new FractalResponseMessage();
        dGRFSpeechDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        String dataSeriesSlug = (String)dataSeriesTermInstance.get(CMSConstants.TERM_INSTANCE_SLUG);
        dGRFSpeechDTO.setDataSeriesSlug(dataSeriesSlug);
        SonorusCoreClient dgrfscc = new SonorusCoreClient();
        dGRFSpeechDTO = dgrfscc.calculateSpeechEmotion(dGRFSpeechDTO);

        if (dGRFSpeechDTO.getResponseCode()!= FractalResponseCode.SUCCESS) {
            Logger.getLogger(SpeechEmoCalc.class.getName()).log(Level.SEVERE, responseMessage.getResponseMessage(dGRFSpeechDTO.getResponseCode()));
        }
        speechEmoTermInstance = dGRFSpeechDTO.getSpeechEmoTermInstance();
        speechEmoMfdfaTermInstance = dGRFSpeechDTO.getMfdfaTermInstance();
        //Logger.getLogger(SpeechEmoCalc.class.getName()).log(Level.INFO,(String)speechEmoTermInstance.get(SonorusResultsMeta.EMOTION));
    }

    public Map<String, Object> getSpeechEmoTermInstance() {
        return speechEmoTermInstance;
    }
    
    private void captureEmoResult () {
        
        
        
        String dataSeriesOrigFileName = (String)dataSeriesTermInstance.get(DataSeriesMeta.DATA_SERIES_ORIGINAL_FILENAME);
        String dataSeriesSlug = (String)dataSeriesTermInstance.get(CMSConstants.TERM_INSTANCE_SLUG);
        String hurstExp = (String)speechEmoMfdfaTermInstance.get(MFDFAResultsMeta.HURST_EXPONENT);
        String identifiedEmo = speechEmoTermInstance.get(SonorusResultsMeta.EMOTION).toString();
        emoResult = dataSeriesOrigFileName+","+dataSeriesSlug+","+hurstExp+","+identifiedEmo;
        Logger.getLogger(SpeechEmoCalc.class.getName()).log(Level.INFO,emoResult);
        
        
    }
    private void deleteEmoResult() {
        //delete emo result instance
        
        //delete emo mfdfa instance
        SonorusDTO sonorusDTO = new SonorusDTO();
        sonorusDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        sonorusDTO.setSpeechEmoTermInstance(speechEmoTermInstance);
        
        SonorusCoreClient dgrfscc = new SonorusCoreClient();
        sonorusDTO = dgrfscc.deleteEmoInstance(sonorusDTO);
        FractalResponseMessage responseMessage = new FractalResponseMessage();
        if (sonorusDTO.getResponseCode() != FractalResponseCode.SUCCESS) {
            Logger.getLogger(SpeechEmoCalc.class.getName()).log(Level.SEVERE, responseMessage.getResponseMessage(sonorusDTO.getResponseCode()));
        }
        //delete dataseries 
        PatronusCoreClient mts = new PatronusCoreClient();
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setHedwigAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        String selectedTermInstanceSlug = (String) dataSeriesTermInstance.get(CMSConstants.TERM_INSTANCE_SLUG);
        fractalDTO.setDataSeriesSlug(selectedTermInstanceSlug);
        //delete dataseries metadata
        fractalDTO = mts.deleteDataseries(fractalDTO);
        if (fractalDTO.getResponseCode() != FractalResponseCode.SUCCESS) {
            Logger.getLogger(SpeechEmoCalc.class.getName()).log(Level.SEVERE, responseMessage.getResponseMessage(sonorusDTO.getResponseCode()));
        }
        Logger.getLogger(SpeechEmoCalc.class.getName()).log(Level.INFO, responseMessage.getResponseMessage(fractalDTO.getResponseCode()));
    }

    public String getEmoResult() {
        return emoResult;
    }

    
}
