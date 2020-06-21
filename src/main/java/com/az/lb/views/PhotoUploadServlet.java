package com.az.lb.views;

import com.az.lb.servise.PersonPhotoService;
import com.vaadin.flow.server.VaadinServletConfiguration;
import com.vaadin.flow.spring.SpringServlet;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Optional;

@MultipartConfig
@WebServlet(urlPatterns = "/upload/photo", name = "uploadphoto", asyncSupported = true)
@VaadinServletConfiguration(productionMode = false)
public class PhotoUploadServlet extends SpringServlet {

    private final ApplicationContext context;


    public PhotoUploadServlet(final ApplicationContext context) {
        super(context, true);
        this.context = context;
    }


    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            try {
                Collection<Part> parts = request.getParts();
                Part picture = getPart(parts, "picture_data").get();
                String contentType = request.getParameterValues("picture_ct")[0];
                String userId = request.getParameterValues("pid")[0];
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + userId + contentType + picture.getSize());
                PersonPhotoService personPhotoService = context.getBean(PersonPhotoService.class);
                personPhotoService.addPhoto(userId, contentType, picture.getSize(), picture.getInputStream());
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
            pwriter.println("405 Method " + request.getMethod() + " not allowed");
            pwriter.close();
        }

    }

    Optional<Part> getPart(Collection<Part> parts, String name) {
        return parts.stream().filter( p -> p.getName().equals(name)).findFirst();
    }


}
