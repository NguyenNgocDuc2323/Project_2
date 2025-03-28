package helper.CoffeeShop;

import model.CoffeeShop.CartItem;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private final String cartFile = "src/main/java/helper/CoffeeShop/TemporaryOrder.xml";

    private CartManager() {
        // Initialize the cart file if it doesn't exist
        initCartFile();
    }

    public static CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    private void initCartFile() {
        File file = new File(cartFile);
        if (!file.exists()) {
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.newDocument();

                // Create root element
                Element rootElement = doc.createElement("cart");
                doc.appendChild(rootElement);

                // Save the empty cart file
                saveDocument(doc);
            } catch (ParserConfigurationException e) {
                System.err.println("Error initializing cart file: " + e.getMessage());
            }
        }
    }

    public void addToCart(int productId, String name, String size, int quantity, double unitPrice) {
        try {
            Document doc = loadDocument();

            // Check if the product with same size already exists
            NodeList items = doc.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                String existingProductId = item.getElementsByTagName("productId").item(0).getTextContent();
                String existingSize = item.getElementsByTagName("size").item(0).getTextContent();

                if (existingProductId.equals(String.valueOf(productId)) && existingSize.equals(size)) {
                    // Update quantity instead of adding new item
                    int existingQuantity = Integer.parseInt(item.getElementsByTagName("quantity").item(0).getTextContent());
                    item.getElementsByTagName("quantity").item(0).setTextContent(String.valueOf(existingQuantity + quantity));
                    saveDocument(doc);
                    return;
                }
            }

            // Add new item
            Element itemElement = doc.createElement("item");

            Element productIdElement = doc.createElement("productId");
            productIdElement.setTextContent(String.valueOf(productId));

            Element nameElement = doc.createElement("name");
            nameElement.setTextContent(name);

            Element sizeElement = doc.createElement("size");
            sizeElement.setTextContent(size);

            Element quantityElement = doc.createElement("quantity");
            quantityElement.setTextContent(String.valueOf(quantity));

            Element priceElement = doc.createElement("unitPrice");
            priceElement.setTextContent(String.valueOf(unitPrice));

            itemElement.appendChild(productIdElement);
            itemElement.appendChild(nameElement);
            itemElement.appendChild(sizeElement);
            itemElement.appendChild(quantityElement);
            itemElement.appendChild(priceElement);

            doc.getDocumentElement().appendChild(itemElement);

            saveDocument(doc);
        } catch (Exception e) {
            System.err.println("Error adding to cart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<CartItem> getCartItems() {
        List<CartItem> items = new ArrayList<>();
        try {
            Document doc = loadDocument();
            NodeList itemNodes = doc.getElementsByTagName("item");

            for (int i = 0; i < itemNodes.getLength(); i++) {
                Element itemElement = (Element) itemNodes.item(i);

                int productId = Integer.parseInt(itemElement.getElementsByTagName("productId").item(0).getTextContent());
                String name = itemElement.getElementsByTagName("name").item(0).getTextContent();
                String size = itemElement.getElementsByTagName("size").item(0).getTextContent();
                int quantity = Integer.parseInt(itemElement.getElementsByTagName("quantity").item(0).getTextContent());
                double unitPrice = Double.parseDouble(itemElement.getElementsByTagName("unitPrice").item(0).getTextContent());

                items.add(new CartItem(productId, name, size, quantity, unitPrice));
            }
        } catch (Exception e) {
            System.err.println("Error getting cart items: " + e.getMessage());
            e.printStackTrace();
        }
        return items;
    }

    public void clearCart() {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Create root element
            Element rootElement = doc.createElement("cart");
            doc.appendChild(rootElement);

            // Save the empty cart
            saveDocument(doc);
        } catch (ParserConfigurationException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
        }
    }

    private Document loadDocument() throws ParserConfigurationException, IOException, SAXException {
        File file = new File(cartFile);

        // If file doesn't exist or is empty, create a new document with proper structure
        if (!file.exists() || file.length() == 0) {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            // Create root element
            Element rootElement = doc.createElement("cart");
            doc.appendChild(rootElement);

            // Save the properly structured document
            saveDocument(doc);
            return doc;
        }

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            return docBuilder.parse(file);
        } catch (SAXException e) {
            // If parsing fails, create a new document with proper structure
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("cart");
            doc.appendChild(rootElement);

            // Save the properly structured document
            saveDocument(doc);
            return doc;
        }
    }

    private void saveDocument(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(cartFile));
            transformer.transform(source, result);
        } catch (TransformerException e) {
            System.err.println("Error saving cart: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public int getCartItemCount() {
        try {
            Document doc = loadDocument();
            NodeList items = doc.getElementsByTagName("item");
            int count = 0;

            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                int quantity = Integer.parseInt(item.getElementsByTagName("quantity").item(0).getTextContent());
                count += quantity;
            }

            return count;
        } catch (Exception e) {
            System.err.println("Error counting cart items: " + e.getMessage());
            return 0;
        }
    }
}