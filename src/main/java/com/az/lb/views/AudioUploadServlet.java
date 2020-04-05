package com.az.lb.views;

import com.az.lb.servise.PersonActivityService;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletConfiguration;
import com.vaadin.flow.spring.SpringServlet;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@MultipartConfig
@WebServlet(urlPatterns = "/upload/audio", name = "uploadaudio", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false)
public class AudioUploadServlet extends SpringServlet {

    private final ApplicationContext context;


    public AudioUploadServlet(final ApplicationContext context) {
        super(context, true);
        this.context = context;
    }


    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            try {
                Collection<Part> parts = request.getParts();

                Part audio = getPart(parts, "audio_data").get();
                PersonActivityService pas = context.getBean(PersonActivityService.class);
                pas.addAudio(
                        request.getParameterValues("pid")[0],
                        audio.getContentType(),
                        audio.getSize(),
                        audio.getInputStream()
                );

                response.setStatus(200);
            } catch (IOException | ServletException e) {
                response.setStatus(500);
                response.setContentType("text/html");
                PrintWriter pwriter = response.getWriter();
                pwriter.println(e.getMessage());
                pwriter.close();
            }
        } else {
            response.setStatus(405);
            response.setContentType("text/html");
            PrintWriter pwriter = response.getWriter();
            pwriter.println("405 Method " + request.getMethod() + " Not Allowed");
            pwriter.close();
        }

    }

    Optional<Part> getPart(Collection<Part> parts, String name) {
        return parts.stream().filter( p -> p.getName().equals(name)).findFirst();
    }


}
