package org.dspace.app.rest;


import org.dspace.app.rest.utils.ContextUtil;
import org.dspace.core.Context;
import org.dspace.eperson.EPerson;
import org.dspace.eperson.factory.EPersonServiceFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.Collection;

@RestController
@RequestMapping("/")
public class StatusRestController {

    @RequestMapping(value="/status", method = RequestMethod.GET)
    public String status(HttpServletRequest request, HttpServletResponse response) throws SQLException {

        Context context = ContextUtil.obtainContext(request);
        //context.getDBConnection().setAutoCommit(false); // Disable autocommit.

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && !(authentication.getPrincipal().equals("anonymousUser"))) {
            Collection<SimpleGrantedAuthority> authorities = (Collection<SimpleGrantedAuthority>) authentication.getAuthorities();

            context.setCurrentUser(EPersonServiceFactory.getInstance().getEPersonService().findByEmail(context, authentication.getName()));

            EPerson current = context.getCurrentUser();
            String status = "EPerson: " + current.getEmail() + "\nFull name: " + current.getFullName() + "\nAuthorities: \n";
            for (SimpleGrantedAuthority authority: authorities) {
                status += authority.getAuthority();
            }
            return status;

        } else {
            return "Not authenticated";
        }
    }
}
