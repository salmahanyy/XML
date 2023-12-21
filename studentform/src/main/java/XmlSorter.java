import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlSorter {

    public static ArrayList<Element> sortElementsByAttribute(Document doc, final String attributeName,
            final boolean ascending) {

        ArrayList<Element> elements = getElementsList(doc.getDocumentElement());

        Collections.sort(elements, new Comparator<Element>() {
            @Override
            public int compare(Element element1, Element element2) {
                String attributeValue1 = "";
                String attributeValue2 = "";

                if (attributeName.equals("ID")) {
                    attributeValue1 = element1.getAttribute(attributeName);
                    attributeValue2 = element2.getAttribute(attributeName);
                } else {
                    attributeValue1 = element1.getElementsByTagName(attributeName).item(0).getTextContent();
                    attributeValue2 = element2.getElementsByTagName(attributeName).item(0).getTextContent();
                }

                // Check if attributes are integers
                boolean isIntAttribute = isInteger(attributeValue1) && isInteger(attributeValue2);

                if (isIntAttribute) {
                    int int1 = Integer.parseInt(attributeValue1);
                    int int2 = Integer.parseInt(attributeValue2);

                    // Perform integer comparison
                    return ascending ? Integer.compare(int1, int2) : Integer.compare(int2, int1);
                } else {
                    // Perform string comparison
                    return ascending ? attributeValue1.compareTo(attributeValue2)
                            : attributeValue2.compareTo(attributeValue1);
                }
            }
        });

        return elements;
    }

    private static ArrayList<Element> getElementsList(Element element) {
        ArrayList<Element> elements = new ArrayList<>();
        NodeList nodeList = element.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) node);
            }
        }

        return elements;
    }

    // Helper method to check if a string represents an integer
    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void saveSortedXml(ArrayList<Element> sortedElements, String filename) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document sortedDoc = dBuilder.newDocument();

            Element rootElement = sortedDoc.createElement("University");
            sortedDoc.appendChild(rootElement);

            for (Element element : sortedElements) {
                Node importedNode = sortedDoc.importNode(element, true);
                rootElement.appendChild(importedNode);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(sortedDoc);
            StreamResult result = new StreamResult(new java.io.File(filename));
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
