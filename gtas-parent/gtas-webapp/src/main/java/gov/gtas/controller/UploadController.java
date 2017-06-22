/*
 * All GTAS code is Copyright 2016, The Department of Homeland Security (DHS), U.S. Customs and Border Protection (CBP).
 * 
 * Please see LICENSE.txt for details.
 */
package gov.gtas.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import gov.gtas.aws.QueueService;
import gov.gtas.repository.AppConfigurationRepository;
import gov.gtas.repository.FlightRepository;
import gov.gtas.repository.LookUpRepository;

@Controller
public class UploadController {
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Autowired
    private FlightRepository flightRespository;

    @Autowired
    private LookUpRepository lookupRepo;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/upload")
    public void upload(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) throws IOException {
        if (file.isEmpty()) {
            logger.info("empty file!");
            return;
        }
        
        writeFile(file);

        // disable this for now
//        String queueName = lookupRepo.getAppConfigOption(AppConfigurationRepository.QUEUE);
//        QueueService sqs = new QueueService(queueName);
//        sqs.sendMessage(new String(file.getBytes()));
        
        logger.info(String.format("received %s from %s", file.getOriginalFilename(), username));
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/deleteall")
    public void wipeAllMessages() throws Exception {
        logger.info("DELETE ALL MESSAGES");
        flightRespository.deleteAllMessages();
    }
    
    /**
     * for writing uploaded files to disk.
     * @param file
     * @throws IOException
     */
    private void writeFile(MultipartFile file) throws IOException {
        FileOutputStream output = null;
        String uploadDir = lookupRepo.getAppConfigOption(AppConfigurationRepository.UPLOAD_DIR);

        try {
            if (!file.isEmpty()) {
                byte[] bytes = file.getBytes();
                String filename = uploadDir + File.separator + file.getOriginalFilename();
                output = new FileOutputStream(new File(filename));
                IOUtils.write(bytes, output);
           }
       } finally {
           if (output != null) {
               output.close();
           }
       }
    }
}
