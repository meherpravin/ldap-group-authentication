/**
 * 
 */
package com.java.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 * @author PMEHER
 *
 */
public class LdapService {
    public static void main(String[] args) {
        authenticateUser("test", "pass");
        getGroupUsers("test", "pass");
    }

    private static void getGroupUsers(String userName, String password) {
        String searchBase = "";
        String returnedAttrs[] = { "cn", "uniqueMember" };
        // String searchFilter = "(&(objectclass=person)(cn=*test*))";
        String searchFilter = "(&(objectclass=groupOfUniqueNames)(cn=admin))";
        // String searchFilter = "(&(cn=admin))";
        Hashtable<String, String> ldapProperties = createLdapProperties(userName, password);
        DirContext ctx = null;
        try{
         SearchControls searchCtls = new SearchControls();
            searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // searchCtls.setReturningAttributes(returnedAttrs);
         ctx = new InitialDirContext(ldapProperties);
          System.out.println("Search Base: "+searchBase);
          System.out.println("Search Filter: "+searchFilter);
          NamingEnumeration users = ctx.search(searchBase, searchFilter, searchCtls);
          while (users.hasMore()) {
              SearchResult rslt = (SearchResult) users.next();
                System.out.println(rslt.getNameInNamespace());
              Attributes attrs = rslt.getAttributes();
                displayAttribute("uniqueMember", attrs);
              }
             
         }catch (NamingException e){
          System.out.println("Problem searching directory: "+e);            
        } finally {
            try {
                ctx.close();
            } catch (NamingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
         ctx=null;   
        }
    }

    private static void displayAttribute(String attrName, final Attributes attributes) throws NamingException {

        if (attributes == null) {
            System.out.println("*** No attributes ***");
        } else {
            for (NamingEnumeration enums = attributes.getAll(); enums.hasMore();) {
                final Attribute attribute = (Attribute) enums.next();
                // System.out.println("\t = " + attribute);
                if (attribute.getID().equals(attrName)) {
                    for (NamingEnumeration namingEnu = attribute.getAll(); namingEnu.hasMore();)
                        System.out.println("\t        = " + namingEnu.next());

                    break;
                }
            }

        }
    }

    public static boolean authenticateUser(String userName, String password) {

        Hashtable<String, String> ldapProperties = createLdapProperties(userName, password);
        DirContext context = null;
        boolean isValidUser = false;

        try {
            context = new InitialDirContext(ldapProperties);

            System.out.println("Authentication successful" + ", User= " + ldapProperties.get(Context.SECURITY_PRINCIPAL));
            isValidUser = true;
        } catch (NamingException e) {
            System.out.println("Authentication failed" + ", User= " + ldapProperties.get(Context.SECURITY_PRINCIPAL));
            System.out.println(e.getMessage());
            // e.printStackTrace();

            // throw new NamingException("Authentication failed");
        } finally {
            if (context != null) {
                try {
                    context.close();
                } catch (NamingException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return isValidUser;
    }

    private static Hashtable<String, String> createLdapProperties(String userName, String password) {
        Hashtable<String, String> environment = new Hashtable<String, String>();

        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL, "ldaps://host:port");
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");

        environment.put(Context.SECURITY_PROTOCOL, "ssl");
        environment.put("java.naming.ldap.factory.socket", "com.java.ldap.LdapSSLSocketFactory");
        // environment.put(Context.SECURITY_CREDENTIALS, password);
        //environment.put(Context.SECURITY_PRINCIPAL, "cn=" + userName + ",ou=Internal,ou=users");
        
        return environment;
    }

}
