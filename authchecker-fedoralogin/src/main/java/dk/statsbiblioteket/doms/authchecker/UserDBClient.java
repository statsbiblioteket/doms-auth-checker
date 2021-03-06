package dk.statsbiblioteket.doms.authchecker;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.UniformInterfaceException;

import java.util.*;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

import dk.statsbiblioteket.doms.authchecker.user.User;
import dk.statsbiblioteket.doms.authchecker.user.Roles;
import dk.statsbiblioteket.doms.authchecker.exceptions.InvalidCredentialsException;
import dk.statsbiblioteket.doms.authchecker.exceptions.BackendException;
import dk.statsbiblioteket.doms.authchecker.exceptions.ResourceNotFoundException;
import dk.statsbiblioteket.doms.authchecker.exceptions.FedoraException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 1, 2010
 * Time: 11:17:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserDBClient {

    private static final Log logger =
            LogFactory.getLog(UserDBClient.class);


    private static Client client = Client.create();
    private WebResource restApi;


    public UserDBClient(String location) {
        restApi = client.resource(location);

    }

    public Map<String, Set<String>> getRolesFor(String username, String password)
            throws BackendException {

        try {
            logger.debug("Getting roles for user "+username);
            User user = restApi.path("/getRolesForUser/")
                    .path(URLEncoder.encode(username,"UTF-8"))
                    .path("/withPassword/")
                    .path(URLEncoder.encode(password,"UTF-8"))
                    .get(User.class);
            logger.debug("Gotten this user object "+user);

            HashMap<String, Set<String>> map;
            map = new HashMap<String, Set<String>>();
            for (Roles roles : user.getAttributes()) {
                HashSet<String> set;
                set = new HashSet<String>();
                set.addAll(roles.getRoles());
                map.put(roles.getAssociation(),set);
            }
            if (logger.isDebugEnabled()){
                for (String key : map.keySet()) {
                    logger.debug("user have attributes for key " + key+map.get(key));
                }
            }
            return map;
        } catch (UnsupportedEncodingException e) {
            throw new Error("UTF-8 not known on this platform",e);
        } catch (UniformInterfaceException e){
            String reason
                    = e.getResponse().getResponseStatus().toString();
            switch (e.getResponse().getResponseStatus()) {
                case FORBIDDEN:
                    throw new InvalidCredentialsException(reason,e);
                case INTERNAL_SERVER_ERROR:
                    throw new FedoraException(reason,e);
                case NOT_FOUND:
                    throw new ResourceNotFoundException(reason,e);
                case UNAUTHORIZED:
                    throw new InvalidCredentialsException(reason,e);
                default:
                    throw new BackendException(reason,e);
            }
        }
    }
}
