package net.pay.russian_payment_system.cbrrate;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class CbrCurrencyRateParserXml implements CurrencyRateParser {

    @Override
    public List<CurrencyRate> parseRate(String ratesAsString) {
        List<CurrencyRate> rates = new ArrayList<>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();

            try (StringReader reader = new StringReader(ratesAsString)) {
                Document doc = db.parse(new InputSource(reader));
                doc.getDocumentElement().normalize();

                NodeList list = doc.getElementsByTagName("Valute");

                for (int valuteIdx = 0; valuteIdx < list.getLength(); valuteIdx++) {
                    Node node = list.item(valuteIdx);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        CurrencyRate rate = CurrencyRate.builder()
                                .numCode(element.getElementsByTagName("NumCode").item(0).getTextContent())
                                .charCode(element.getElementsByTagName("CharCode").item(0).getTextContent())
                                .nominal(element.getElementsByTagName("Nominal").item(0).getTextContent())
                                .name(element.getElementsByTagName("Name").item(0).getTextContent())
                                .value(element.getElementsByTagName("Value").item(0).getTextContent())
                                .build();
                        rates.add(rate);
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        return rates;
    }
}
