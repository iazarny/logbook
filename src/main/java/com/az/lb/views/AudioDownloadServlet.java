package com.az.lb.views;

import com.az.lb.servise.PersonActivityService;
import com.az.lb.views.masterdetail.PersonView;
import com.vaadin.flow.server.VaadinServletConfiguration;
import com.vaadin.flow.spring.SpringServlet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Optional;

@MultipartConfig
@WebServlet(urlPatterns = "/download/audio", name = "downloadaudio", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false)
public class AudioDownloadServlet extends SpringServlet {

    private static final Logger logger = LoggerFactory.getLogger(AudioDownloadServlet.class);

    private final ApplicationContext context;


    public AudioDownloadServlet(final ApplicationContext context) {
        super(context, true);
        this.context = context;
    }


    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String paid = request.getParameterValues("pid")[0];
        PersonActivityService pas = context.getBean(PersonActivityService.class);
        try {
            Pair<InputStream, String> audio = pas.getAudio(paid);
            InputStream is = audio.getLeft();
            response.setContentType(audio.getRight());
            IOUtils.copy(is, response.getOutputStream());
            is.close();
        } catch (Exception e) {

            logger.warn("Cannot get audio id " + paid, e);
            response.setStatus(400);
            response.setContentType("text/html");
            PrintWriter pwriter = response.getWriter();
            pwriter.println("400");
            pwriter.close();
        }

    }

    Optional<Part> getPart(Collection<Part> parts, String name) {
        return parts.stream().filter( p -> p.getName().equals(name)).findFirst();
    }


}
