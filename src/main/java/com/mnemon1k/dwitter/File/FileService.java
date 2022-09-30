package com.mnemon1k.dwitter.File;

import com.mnemon1k.dwitter.configuration.AppConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileService {
    AppConfiguration appConfig;
    Tika tika;

    @Autowired
    public FileService(AppConfiguration appConfig) {
        this.appConfig = appConfig;
        this.tika = new Tika();
    }

    public String saveProfileImage(String base64image) throws IOException {
        String imageName = UUID.randomUUID().toString().replace("-", "");
        byte[] decodedArray = Base64.getDecoder().decode(base64image);
        File target = new File(appConfig.getFullProfileImagesPath() + "/" + imageName);
        FileUtils.writeByteArrayToFile(target, decodedArray);

        return imageName;
    }

    public String detectType(byte[] byteArray) {
        return tika.detect(byteArray);
    }
}
