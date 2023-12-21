import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document; // Add this line
import org.w3c.dom.Element;
import org.w3c.dom.Node;

// xml file is created in: C:\apache-tomcat-9.0.82\apache-tomcat-9.0.82\bin\data

public class SubmitForm extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");

        // Retrieve the number of students from the form
        String numberOfStudentsString = request.getParameter("numberOfStudents");
        int numberOfStudents = 0;
        if (numberOfStudentsString != null && !numberOfStudentsString.isEmpty()) {
            numberOfStudents = Integer.parseInt(numberOfStudentsString);
        }

        // check if student data is received to build XML file
        if (numberOfStudents == 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No student data received");
            return;
        }

        //////////////////////////
        // check if student ids is unique if not retuen to form page with error message
        ArrayList<String> idList = new ArrayList<String>();
        for (int i = 1; i <= numberOfStudents; i++) {
            String idString = request.getParameter("id" + i);
            if (idList.contains(idString)) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Student ID must be unique");
                return;
            }
            idList.add(idString);
        }
        //////////////////////////

        // build XML file
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();

            // add elements to Document
            Element rootElement = doc.createElement("University");
            doc.appendChild(rootElement);

            for (int i = 1; i <= numberOfStudents; i++) {
                String idString = request.getParameter("id" + i);
                String firstname = request.getParameter("firstname" + i);
                String lastname = request.getParameter("lastname" + i);
                String gender = request.getParameter("gender" + i);
                String gpaString = request.getParameter("gpa" + i);
                String levelString = request.getParameter("level" + i);
                String address = request.getParameter("address" + i);

                // append first child element to root element
                rootElement.appendChild(createUserElement(doc, idString, firstname, lastname, gender, gpaString,
                        levelString, address));
            }

            // for output to file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            // for pretty print
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);

            // write to file
            StreamResult file = new StreamResult(new File("data/Students.xml"));
            transformer.transform(source, file);

            // Redirect to another page after processing
            response.sendRedirect("/studentform/search");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Node createUserElement(Document doc, String id, String firstName, String lastName, String gender,
            String gpaString, String levelString, String address) {
        Element user = doc.createElement("Student");

        // set id attribute
        user.setAttribute("ID", id);

        // create firstName element
        user.appendChild(createUserElements(doc, "firstName", firstName));

        // create lastName element
        user.appendChild(createUserElements(doc, "lastName", lastName));

        // create gender element
        user.appendChild(createUserElements(doc, "Gender", gender));

        // create gpa element
        user.appendChild(createUserElements(doc, "gpa", gpaString));

        // create level element
        user.appendChild(createUserElements(doc, "level", levelString));

        // create address element
        user.appendChild(createUserElements(doc, "address", address));

        return user;
    }

    // utility method to create text node
    private static Node createUserElements(Document doc, String name, String value) {
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }
}
