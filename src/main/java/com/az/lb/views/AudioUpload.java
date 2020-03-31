package com.az.lb.views;

import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletConfiguration;
import com.vaadin.flow.spring.SpringServlet;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebServlet(urlPatterns = "/upload/audio", name = "uploadaudio", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false)
public class AudioUpload extends SpringServlet {


    /**
     * Creates a new Vaadin servlet instance with the application
     * {@code context} provided.
     *
     * @param context            the Spring application context
     * @param forwardingEnforced
     */
    public AudioUpload(ApplicationContext context, boolean forwardingEnforced) {
        super(context, forwardingEnforced);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.service(request, response);
    }
}
