package controllers;

import models.User;
import org.codehaus.jackson.node.ObjectNode;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.libs.Json;
import play.mvc.*;

import static play.libs.Json.toJson;
import static play.mvc.Controller.request;
import static play.mvc.Controller.response;

public class SecurityController extends Action.Simple {

    public final static String AUTH_TOKEN_HEADER = "X-AUTH-TOKEN";
    public static final String AUTH_TOKEN = "authToken";

    public Result call(Http.Context ctx) throws Throwable {
        User user = null;
        String[] authTokenHeaderValues = ctx.request().headers().get(AUTH_TOKEN_HEADER);
        if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1) && (authTokenHeaderValues[0] != null)) {
            user = models.User.findByAuthToken(authTokenHeaderValues[0]);
            if (user != null) {
                ctx.args.put("user", user);
                return delegate.call(ctx);
            }
        }
        
        return unauthorized("unauthorized");
    }

    public static User getUser() {
        return (User)Http.Context.current().args.get("user");
    }

    // returns an authToken
    public static Result login() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();

        if (loginForm.hasErrors()) {
            return badRequest(loginForm.errorsAsJson());
        }

        Login login = loginForm.get();

        User user = User.findByEmailAddressAndPassword(login.emailAddress, login.password);

        if (user == null) {
            return unauthorized(); // BEN: return HTTP 401
        }
        else {
            String authToken = user.createToken(); // BEN: creates authToken, writes it to db
            ObjectNode authTokenJson = Json.newObject();
            authTokenJson.put(AUTH_TOKEN, authToken); // BEN: drop cookie with authToken
            response().setCookie(AUTH_TOKEN, authToken);
            return ok(authTokenJson); // BEN: why are we returing this json???
        }
    }

    @With(SecurityController.class)
    public static Result logout() {
        response().discardCookie(AUTH_TOKEN);
        getUser().deleteAuthToken();
        return redirect("/");
    }

    public static class Login {

        @Constraints.Required
        @Constraints.Email
        public String emailAddress;

        @Constraints.Required
        public String password;

    }


}
