import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@WebServlet("/delete")
public class Delete extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Handle the delete action
        String studentIdToDelete = request.getParameter("id");
        deleteStudentFromXML(studentIdToDelete);
        // Redirect to doGet to refresh the search results
        response.sendRedirect("/studentform/search");
    }

    private void deleteStudentFromXML(String studentIdToDelete) {
        try {
            // Load the XML file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse("data/Students.xml");

            // Normalize the XML structure
            doc.getDocumentElement().normalize();

            // Get the root element
            Element rootElement = doc.getDocumentElement();

            // Get all student nodes
            NodeList studentNodes = rootElement.getElementsByTagName("Student");

            // Iterate through the student nodes
            for (int i = 0; i < studentNodes.getLength(); i++) {
                Node studentNode = studentNodes.item(i);

                // Check if the current node is an element node
                if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) studentNode;

                    // Retrieve the ID of the current student
                    String currentStudentId = studentElement.getAttribute("ID");

                    // Check if the current student's ID matches the ID to delete
                    if (currentStudentId.equals(studentIdToDelete)) {
                        // Remove the student element from the XML document
                        rootElement.removeChild(studentElement);

                        // Save the changes back to the XML file
                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource source = new DOMSource(doc);

                        StreamResult result = new StreamResult(new File("data/Students.xml"));
                        transformer.transform(source, result);

                        break; // Exit the loop after deletion
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
