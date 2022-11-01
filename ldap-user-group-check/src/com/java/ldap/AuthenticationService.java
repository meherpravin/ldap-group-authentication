/**
 * 
 */
package com.java.ldap;

import java.util.List;

import javax.naming.NamingException;

import org.apache.log4j.Logger;

import com.ibm.websphere.security.UserRegistry;

/**
 * @author PMEHER
 * 
 * Get websphere user registry after Standalone LDAP registry configuration
 *
 */
public class AuthenticationService {

    private static Logger log = Logger.getLogger(AuthenticationService.class);

    public static boolean authenticateUserGroup(String user, String group) {
        javax.naming.InitialContext ctx = null;
        UserRegistry userRegistry = null;
        boolean isGroupExist = false;
        try {
            ctx = new javax.naming.InitialContext();
            // Retrieves the local UserRegistry object.
            userRegistry = (com.ibm.websphere.security.UserRegistry) ctx.lookup("UserRegistry");
            if (userRegistry == null) {
                System.out.println("Failed to get UserRegistry");
                return false;
            }

            List<String> groups = userRegistry.getGroupsForUser(user);
            if (groups.contains(group)) {
                isGroupExist = true;
            }
            log.info(String.format("Groups %s assigned to the %s user", groups, user));

        } catch (Exception e) {
            log.error("Exception occured ", e);
        } finally {
            try {
                ctx.close();
            } catch (NamingException e) {
                log.error("Exception occured while closing the context ", e);
            }
        }
        return isGroupExist;
    }

}
