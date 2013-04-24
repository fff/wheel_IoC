package com.thoughtworks.wheels;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XmlParser {

    protected static Document parseXml(File file) throws ParserConfigurationException, IOException, SAXException {
        Document doc = null;
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        doc = dbFactory.newDocumentBuilder().parse(file);
        doc.getDocumentElement().normalize();
        return doc;
    }

}
