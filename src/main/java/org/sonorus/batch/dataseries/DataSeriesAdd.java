/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sonorus.batch.dataseries;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sonorus.core.dto.SonorusDTO;
import org.sonorus.core.client.SonorusCoreClient;
import org.patronus.fractal.core.client.FractalCoreClient;
import org.patronus.fractal.core.dto.FractalDTO;
import org.patronus.fractal.response.FractalResponseCode;
import org.patronus.fractal.response.FractalResponseMessage;
import org.patronus.fractal.termmeta.DataSeriesMeta;

/**
 *
 * @author dgrfi
 */
public class DataSeriesAdd {

    private Map<String, Object> dataseriesTermInstance;
    private String wavFilePath;
    private File fileToUpload;

    public DataSeriesAdd(File fileToUpload) {
        this.fileToUpload = fileToUpload;
    }
    
    
    private void createDataSeriesTermInstance() {
        FractalCoreClient fractalCoreClient = new FractalCoreClient();
        FractalDTO fractalDTO = new FractalDTO();
        fractalDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        fractalDTO = fractalCoreClient.createDataSeriesTermInstance(fractalDTO);
        dataseriesTermInstance = fractalDTO.getFractalTermInstance();
    }

    private void handleFileUpload() {
        //copy file to temp file path
        String fileName = fileToUpload.getName();
        dataseriesTermInstance.put(DataSeriesMeta.DATA_SERIES_ORIGINAL_FILENAME, fileName);
        wavFilePath = fileToUpload.getAbsolutePath();
        
    }

    private void getFileAndUpload() {

        //convert wav to csv and upload to dataseries
        SonorusCoreClient dgrfscc = new SonorusCoreClient();
        SonorusDTO dGRFSpeechDTO = new SonorusDTO();
        dGRFSpeechDTO.setAuthCredentials(CMSClientAuthCredentialValue.AUTH_CREDENTIALS);
        dGRFSpeechDTO.setWavFilePath(wavFilePath);
        dGRFSpeechDTO.setSpeechDataSeriesTermInstance(dataseriesTermInstance);
        dGRFSpeechDTO = dgrfscc.convertWavToCsv(dGRFSpeechDTO);

        if (dGRFSpeechDTO.getResponseCode() != FractalResponseCode.SUCCESS) {
            FractalResponseMessage message = new FractalResponseMessage();
            Logger.getLogger(DataSeriesAdd.class.getName()).log(Level.INFO, "Problem in upload",wavFilePath);
        }
    }
    public void  uploadWav() {
        createDataSeriesTermInstance();
        handleFileUpload();
        getFileAndUpload();
    }

    public File getFileToUpload() {
        return fileToUpload;
    }

    public void setFileToUpload(File fileToUpload) {
        this.fileToUpload = fileToUpload;
    }

    public Map<String, Object> getDataseriesTermInstance() {
        return dataseriesTermInstance;
    }

    public void setDataseriesTermInstance(Map<String, Object> dataseriesTermInstance) {
        this.dataseriesTermInstance = dataseriesTermInstance;
    }
    

}
