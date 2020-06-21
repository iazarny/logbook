package com.az.lb.views;

import com.az.lb.model.PersonPhoto;
import com.az.lb.servise.PersonPhotoService;
import com.vaadin.flow.server.VaadinServletConfiguration;
import com.vaadin.flow.spring.SpringServlet;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

@MultipartConfig
@WebServlet(urlPatterns = "/download/photo", name = "downloadphoto", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false)
public class PhotoDownloadServlet extends SpringServlet {

    private static final Logger logger = LoggerFactory.getLogger(PhotoDownloadServlet.class);

    private final ApplicationContext context;


    public PhotoDownloadServlet(final ApplicationContext context) {
        super(context, true);
        this.context = context;
    }


    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String paid = request.getParameterValues("pid")[0];
        PersonPhotoService pas = context.getBean(PersonPhotoService.class);
        try {
            PersonPhoto personPhoto = pas.getPersonPhoto(paid);
            InputStream is = personPhoto.getImage().getBinaryStream();
            response.setContentType(personPhoto.getImagect());
            IOUtils.copy(is, response.getOutputStream());
            is.close();
        } catch (Exception e) {

            logger.warn("Cannot get photo id " + paid, e);
            response.setStatus(400);
            response.setContentType("text/html");
            PrintWriter pwriter = response.getWriter();
            pwriter.println("400");
            pwriter.close();
        }

    }


}
