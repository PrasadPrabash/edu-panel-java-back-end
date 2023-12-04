package lk.ijse.dep11.edupanal;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan
public class WebAppConfig {

    @Bean
    public StandardServletMultipartResolver multipartResolver(){  // we have to specify the StandardServletMultipartResolver to convert multipart to java object
        return new StandardServletMultipartResolver();
    }
}
