import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@WebServlet("/search")
public class Search extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        // Display the search form
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Search Form</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; }");
        out.println("h2 { color: #333; }");
        out.println("form { max-width: 400px; margin: 20px auto; padding: 15px; border: 1px solid #ccc; }");
        out.println("label { display: block; margin-bottom: 5px; }");
        out.println("select, input { width: 100%; padding: 8px; margin-bottom: 10px; }");
        out.println("input[type='submit'] { background-color: #4CAF50; color: white; cursor: pointer; }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>Search Form</h2>");

        // Add sorting options
        out.println("<form action='search' method='post'>");
        out.println("<br><label for='sortAttribute'>Sort by:</label>");
        out.println("<select name='sortAttribute' id='sortAttribute'>");
        out.println("<option value='ID'>ID</option>");
        out.println("<option value='firstName'>First Name</option>");
        out.println("<option value='lastName'>Last Name</option>");
        out.println("<option value='Gender'>Gender</option>");
        out.println("<option value='gpa'>GPA</option>");
        out.println("<option value='level'>Level</option>");
        out.println("<option value='address'>Address</option>");
        out.println("</select><br>");

        // Add sorting order options
        out.println("<label for='sortOrder'>Sort order:</label>");
        out.println("<select name='sortOrder' id='sortOrder'>");
        out.println("<option value='asc'>Ascending</option>");
        out.println("<option value='desc'>Descending</option>");
        out.println("</select><br>");

        out.println("<label for='searchType'>Search Type:</label>");
        out.println("<select name='searchType' id='searchType'>");
        out.println("<option value='ID'>id</option>");
        out.println("<option value='FirstName'>First Name</option>");
        out.println("<option value='LastName'>Last Name</option>");
        out.println("<option value='Gender'>Gender</option>");
        out.println("<option value='GPA'>GPA</option>");
        out.println("<option value='Level'>Level</option>");
        out.println("<option value='Address'>Address</option>");

        out.println("</select>");
        out.println("<br>");
        out.println("<label for='searchTerm'>Search Term:</label>");
        out.println("<input type='text' name='searchTerm' id='searchTerm' required>");
        out.println("<br>");
        out.println("<input type='submit' value='Search and Sort'>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();

        // Retrieve search parameters from the form
        String searchType = request.getParameter("searchType");
        String searchTerm = request.getParameter("searchTerm");

        String sortAttribute = request.getParameter("sortAttribute");
        String sortOrder = request.getParameter("sortOrder");

        Document doc = loadXmlFile("data/Students.xml");

        // Search the XML file based on the specified criteria
        List<Student> searchResults = performSearch(searchType, searchTerm);

        // Sort the search results based on user input
        if (sortAttribute != null && sortOrder != null) {
            // Sort and save the XML
            ArrayList<Element> sortedElements = XmlSorter.sortElementsByAttribute(doc, sortAttribute,
                    sortOrder.equalsIgnoreCase("asc"));
            XmlSorter.saveSortedXml(sortedElements, "data/Students.xml");
        }

        // Display search results
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Search Results</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h2>Search Results</h2>");

        if (searchResults.isEmpty()) {
            out.println("<p>No results found for the specified criteria.</p>");
        } else {

            out.println("<style>");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; }");
            out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            out.println("th { background-color: #f2f2f2; }");
            out.println("tr:hover { background-color: #f5f5f5; }");
            out.println("input[type='text'] { width: 100%; padding: 5px; box-sizing: border-box; }");
            out.println(
                    "input[type='submit'] { background-color: #4CAF50; color: white; padding: 8px 12px; cursor: pointer; }");
            out.println("</style>");

            out.println("<p>Number of found students: " + searchResults.size() + "</p>");

            out.println("<table border='1'>");
            out.println("<tr>");
            out.println("<th>ID</th>");
            out.println("<th>First Name</th>");
            out.println("<th>Last Name</th>");
            out.println("<th>Gender</th>");
            out.println("<th>GPA</th>");
            out.println("<th>Level</th>");
            out.println("<th>Address</th>");
            out.println("</tr>");

            for (Student student : searchResults) {
                out.println("<form method='post' action='edit'>");
                out.println("<tr>");
                out.println("<td>" + student.ID + "</td>");
                // out.println("<td><a href='edit?ID=" + student.ID + "'>" + student.firstName
                // + "</a></td>");
                out.println("<input type='hidden' name='id' value='" + student.ID + "'>");
                out.println("<td><input type='text' name='firstname' value='" + student.firstName
                        + "' pattern='[A-Za-z]+' required></td>");
                out.println("<td><input type='text' name='lastname' value='" + student.lastName
                        + "' pattern='[A-Za-z]+' required></td>");
                out.println("<td><input type='text' name='gender' value ='" + student.gender + "' required></td>");
                out.println("<td><input type='number' name='gpa' value='" + student.GPA
                        + "' min='0' max='4' step='0.01' required></td>");
                out.println("<td><input type='number' name='level' value='" + student.level
                        + "' min='1' max='4' required></td>");

                out.println("<td><input type='text' name='address' value ='" + student.address
                        + "' pattern='[A-Za-z]+' required></td>");

                out.println("<td>");
                out.println("<input type='submit' value='Edit'>");

                out.println("<input type='submit' formaction='delete' value='Delete'>");
                out.println("</td>");

                out.println("</tr>");

                out.println("</form>");
            }

            out.println("</table>");
        }

        out.println("</body>");
        out.println("</html>");
    }

    private Document loadXmlFile(String filePath) {
        Document doc = null;
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(filePath);
            doc.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    private List<Student> performSearch(String searchType, String searchTerm) {
        List<Student> searchResults = new ArrayList<>();

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

                if (studentNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element studentElement = (Element) studentNode;

                    // Retrieve values from XML

                    String id = studentElement.getAttribute("ID");
                    String firstName = studentElement.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = studentElement.getElementsByTagName("lastName").item(0).getTextContent();
                    String gender = studentElement.getElementsByTagName("Gender").item(0).getTextContent();
                    String level = studentElement.getElementsByTagName("level").item(0).getTextContent();
                    String address = studentElement.getElementsByTagName("address").item(0).getTextContent();
                    String gpa = studentElement.getElementsByTagName("gpa").item(0).getTextContent();

                    // Perform the search based on the specified criteria
                    if (("FirstName".equals(searchType) && firstName.contains(searchTerm))) {
                        Student student = new Student(id, firstName, lastName, gender, gpa, level, address);
                        searchResults.add(student);
                    } else if (("GPA".equals(searchType) && gpa.equals(searchTerm))) {
                        Student student = new Student(id, firstName, lastName, gender, gpa, level, address);
                        searchResults.add(student);
                    } else if (("LastName").equals(searchType) && lastName.contains(searchTerm)) {
                        Student student = new Student(id, firstName, lastName, gender, gpa, level, address);
                        searchResults.add(student);
                    } else if (("Gender").equals(searchType) && gender.contains(searchTerm)) {
                        Student student = new Student(id, firstName, lastName, gender, gpa, level, address);
                        searchResults.add(student);
                    } else if (("Level").equals(searchType) && level.contains(searchTerm)) {
                        Student student = new Student(id, firstName, lastName, gender, gpa, level, address);
                        searchResults.add(student);
                    } else if (("Address").equals(searchType) && address.contains(searchTerm)) {
                        Student student = new Student(id, firstName, lastName, gender, gpa, level, address);
                        searchResults.add(student);
                    } else if (("ID").equals(searchType) && id.contains(searchTerm)) {
                        Student student = new Student(id, firstName, lastName, gender, gpa, level, address);
                        searchResults.add(student);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return searchResults;
    }
}
