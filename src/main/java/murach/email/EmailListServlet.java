package murach.email;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import murach.business.User;
import murach.data.UserDB;
import murach.util.MailUtilRender;

@WebServlet("/emailList")
public class EmailListServlet extends HttpServlet {

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        String url = "/index.jsp";

        String action =
                request.getParameter("action");

        if (action == null) {

            action = "join";
        }

        if (action.equals("join")) {

            url = "/index.jsp";

        } else if (action.equals("add")) {

            String firstName =
                    request.getParameter("firstName");

            String lastName =
                    request.getParameter("lastName");

            String email =
                    request.getParameter("email");

            User user =
                    new User(
                            firstName,
                            lastName,
                            email
                    );

            String message = "";

            // Kiểm tra email
            if (UserDB.emailExists(
                    user.getEmail())) {

                message =
                        "This email address already exists.<br>"
                        + "Please enter another email address.";

                url = "/index.jsp";

            } else {

                // Lưu vào database
                UserDB.insert(user);

                String subject =
                        "Welcome to our email list";

                String body =
                        "Dear " + firstName + ",\n\n"
                        + "Thanks for joining our email list.\n"
                        + "We'll make sure to send you "
                        + "announcements about new products "
                        + "and promotions.\n\n"
                        + "Have a great day and "
                        + "thanks again!\n\n"
                        + "Email List Team";

                try {

                    MailUtilRender.sendMail(
                            email,
                            firstName + " " + lastName,
                            subject,
                            body
                    );

                } catch (IOException | InterruptedException e) {

                    log("===== BREVO SEND ERROR =====", e);

                    e.printStackTrace();
                }

                url = "/thanks.jsp";
            }

            request.setAttribute(
                    "user",
                    user
            );

            request.setAttribute(
                    "message",
                    message
            );
        }

        getServletContext()
                .getRequestDispatcher(url)
                .forward(
                        request,
                        response
                );
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/index.jsp"
        ).forward(
                request,
                response
        );
    }
}