package ru.hogwarts.school.utility;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;

@Component
public class ImageioInitializer {

    @PostConstruct
    public void init() {
        ImageIO.scanForPlugins();
    }
}
