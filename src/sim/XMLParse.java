package sim;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLParse {
    
    File input;
    FileInputStream inputStream;
    DocumentBuilderFactory factory;
    DocumentBuilder builder;
    Document doc;

    public XMLParse(File input) throws SAXException, IOException{
        this.input = input;
      
        factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        doc = builder.parse(input);
        doc.getDocumentElement().normalize();
    }

    public HashMap<String, Float> getCosts(){
        HashMap<String, Float> values = new HashMap<>();
        NodeList list = doc.getElementsByTagName("server");
        for (int j=0; j<list.getLength(); ++j) {
            Node prop = list.item(j);
            NamedNodeMap attr = prop.getAttributes();
            if (null != attr) {
                Node type = attr.getNamedItem("type");
                Node rate = attr.getNamedItem("hourlyRate");
                values.put(type.getNodeValue(), Float.valueOf(Float.parseFloat(rate.getNodeValue())));
               
            }
        }
        return values;
    }

}
